/**
 * SERVIÇO: Catálogo de Produtos e Serviços
 *
 * Responsável por todas as operações HTTP com o backend Spring Boot
 * relacionadas ao catálogo de itens disponíveis para orçamento.
 */

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Catalogo } from '../../models/catalogo';
import { environment } from '../../../environments/environment';


@Injectable({
  providedIn: 'root'
})
export class CatalogoService {


  /**
   * URL base da API Spring Boot
   * Exemplo:
   * http://localhost:8080/api/catalogos
   */
  private apiUrl = `${environment.apiUrl}/catalogos`;


  constructor(private http: HttpClient) {}


  /**
   * Lista todos os itens do catálogo
   */
  listarTodos(): Observable<Catalogo[]> {

    return this.http.get<Catalogo[]>(
      this.apiUrl
    );

  }


  /**
   * Busca itens por categoria
   */
  listarPorCategoria(categoria: string): Observable<Catalogo[]> {

    return this.http.get<Catalogo[]>(
      `${this.apiUrl}/categoria/${categoria}`
    );

  }


  /**
   * Busca item por ID
   */
  buscarPorId(id: number): Observable<Catalogo> {

    return this.http.get<Catalogo>(
      `${this.apiUrl}/${id}`
    );

  }


  /**
   * Cria novo item no catálogo
   */
  criar(item: Catalogo): Observable<Catalogo> {

    return this.http.post<Catalogo>(
      this.apiUrl,
      item
    );

  }


  /**
   * Atualiza item existente
   */
  atualizar(id: number, item: Catalogo): Observable<Catalogo> {

    return this.http.put<Catalogo>(
      `${this.apiUrl}/${id}`,
      item
    );

  }


  /**
   * Remove item do catálogo
   */
  remover(id: number): Observable<void> {

    return this.http.delete<void>(
      `${this.apiUrl}/${id}`
    );

  }


}