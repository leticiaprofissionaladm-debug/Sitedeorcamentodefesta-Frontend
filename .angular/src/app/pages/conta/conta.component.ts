import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { OrcamentoService } from '../../core/services/orcamento-service';
import { Cliente } from '../../models/cliente';
import { Orcamento } from '../../models/orcamento';

@Component({
  selector: 'app-conta',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './conta.component.html',
  styleUrls: ['./conta.component.css']
})
export class ContaComponent implements OnInit {

  cliente!: Cliente;
  orcamentos: Orcamento[] = [];

  constructor(
    private orcamentoService: OrcamentoService
  ) {}

  ngOnInit(): void {

  const email = 'leticia.profissional.adm@gmail.com';

  this.orcamentoService.buscarPorEmail(email).subscribe({

    next: (orcamentos: Orcamento[]) => {

      this.orcamentos = orcamentos;

      if (orcamentos.length > 0) {

        this.cliente = {
          id: 0,
          nome: orcamentos[0].nomeCliente,
          email: orcamentos[0].emailCliente,
          telefone: orcamentos[0].telefoneCliente
        };

      }

    },

    error: (erro: any) => {
      console.error(erro);
    }

  });

}
}