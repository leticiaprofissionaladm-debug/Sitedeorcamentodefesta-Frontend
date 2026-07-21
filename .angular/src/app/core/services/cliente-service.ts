import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import {
  Cliente,
  OrcamentoResumo,
  AlterarSenha
} from '../../models/cliente';

@Injectable({
  providedIn: 'root'
})
export class ContaService {

  private apiUrl = 'http://localhost:8080/api/clientes';

  constructor(
    private http: HttpClient
  ) {}

  buscarCliente(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(
      `${this.apiUrl}/${id}`
    );
  }

  listarOrcamentos(id: number): Observable<OrcamentoResumo[]> {
    return this.http.get<OrcamentoResumo[]>(
      `${this.apiUrl}/${id}/orcamentos`
    );
  }

  atualizarConta(id: number, dados: Partial<Cliente>): Observable<Cliente> {
    return this.http.put<Cliente>(
      `${this.apiUrl}/${id}`,
      dados
    );
  }

  alterarSenha(id: number, dados: AlterarSenha): Observable<void> {
    return this.http.put<void>(
      `${this.apiUrl}/${id}/senha`,
      dados
    );
  }
}