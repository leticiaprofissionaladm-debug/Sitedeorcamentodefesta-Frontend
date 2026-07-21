import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe, CurrencyPipe } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { ContaService } from '../../core/services/conta.service';
import {
  Cliente,
  OrcamentoResumo,
  AlterarSenha
} from '../../models/cliente';

@Component({
  selector: 'app-conta',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DatePipe,
    CurrencyPipe
  ],
  templateUrl: './conta.component.html',
  styleUrls: ['./conta.component.css']
})
export class ContaComponent implements OnInit {

  clienteId = 1;

  cliente: Cliente | null = null;
  orcamentos: OrcamentoResumo[] = [];

  carregando = true;
  erro = '';
  mensagemSucesso = '';

  editando = false;
  mostrarFormSenha = false;
  salvandoDados = false;
  salvandoSenha = false;

  formDados = {
    nome: '',
    email: '',
    telefone: '',
    endereco: ''
  };

  formSenha: AlterarSenha & { confirmarSenha: string } = {
    senhaAtual: '',
    novaSenha: '',
    confirmarSenha: ''
  };

  erroSenha = '';

  constructor(
    private contaService: ContaService
  ) {}

  ngOnInit(): void {
    this.carregarConta();
    //this.carregarOrcamentos();
  }

  carregarConta(): void {

    this.carregando = true;
    this.erro = '';

    this.contaService.buscarCliente(this.clienteId)
      .subscribe({

        next: (cliente) => {

          this.cliente = cliente;

          this.formDados = {
            nome: cliente.nome,
            email: cliente.email,
            telefone: cliente.telefone,
            endereco: cliente.endereco
          };

          this.carregando = false;

        },

        error: (erro) => {

          console.error(erro);

          this.erro = 'Não foi possível carregar os dados da conta. Tente novamente em instantes.';
          this.carregando = false;

        }

      });

  }

  carregarOrcamentos(): void {

    this.contaService
      .listarOrcamentos(this.clienteId)
      .subscribe({

        next: (lista) => {

          this.orcamentos = lista ?? [];

        },

        error: (erro) => {

          console.error(erro);

          this.orcamentos = [];

        }

      });

  }

  ativarEdicao(): void {

    this.editando = true;
    this.mensagemSucesso = '';

  }

  cancelarEdicao(): void {

    this.editando = false;

    if (this.cliente) {

      this.formDados = {
        nome: this.cliente.nome,
        email: this.cliente.email,
        telefone: this.cliente.telefone,
        endereco: this.cliente.endereco
      };

    }

  }

  salvarDados(): void {

    this.salvandoDados = true;
    this.erro = '';

    this.contaService
      .atualizarConta(this.clienteId, this.formDados)
      .subscribe({

        next: (clienteAtualizado) => {

          this.cliente = clienteAtualizado;
          this.editando = false;
          this.salvandoDados = false;

          this.mensagemSucesso = 'Dados atualizados com sucesso!';

          setTimeout(() => this.mensagemSucesso = '', 4000);

        },

        error: (erro) => {

          console.error(erro);

          this.erro = 'Não foi possível atualizar os dados.';
          this.salvandoDados = false;

        }

      });

  }

  salvarNovaSenha(): void {

    this.erroSenha = '';

    if (!this.formSenha.senhaAtual || !this.formSenha.novaSenha) {

      this.erroSenha = 'Preencha todos os campos.';
      return;

    }

    if (this.formSenha.novaSenha !== this.formSenha.confirmarSenha) {

      this.erroSenha = 'As senhas não conferem.';
      return;

    }

    this.salvandoSenha = true;

    this.contaService
      .alterarSenha(this.clienteId, {
        senhaAtual: this.formSenha.senhaAtual,
        novaSenha: this.formSenha.novaSenha
      })
      .subscribe({

        next: () => {

          this.mensagemSucesso = 'Senha alterada com sucesso!';
          this.mostrarFormSenha = false;
          this.salvandoSenha = false;

          this.formSenha = {
            senhaAtual: '',
            novaSenha: '',
            confirmarSenha: ''
          };

          setTimeout(() => this.mensagemSucesso = '', 4000);

        },

        error: (erro) => {

          console.error(erro);

          this.erroSenha = erro?.status === 400
            ? 'Senha atual incorreta.'
            : 'Erro ao alterar senha.';

          this.salvandoSenha = false;

        }

      });

  }

  statusClasse(status: string): string {

    switch (status) {

      case 'APROVADO':
        return 'status aprovado';

      case 'RECUSADO':
        return 'status recusado';

      case 'CONCLUIDO':
        return 'status concluido';

      default:
        return 'status pendente';

    }

  }

}