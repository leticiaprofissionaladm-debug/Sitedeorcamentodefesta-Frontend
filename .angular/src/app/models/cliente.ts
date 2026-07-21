export interface Cliente {
  id: number;
  nome: string;
  email: string;
  telefone: string;
  endereco: string;
  dataCadastro?: string;
}

export interface OrcamentoResumo {
  id: number;
  tipoFesta: string;
  dataFesta: string;
  dataSolicitacao: string;
  valorEstimado: number;
  status: string;
}

export interface AlterarSenha {
  senhaAtual: string;
  novaSenha: string;
}