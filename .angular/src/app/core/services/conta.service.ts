import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from '../../models/cliente';
@Injectable({
  providedIn: 'root'
})
export class ContaService {

  private apiUrl = 'http://localhost:8080/api/conta';

  constructor(private http: HttpClient) {}

  buscarCliente(): Observable<Cliente> {
    return this.http.get<Cliente>(this.apiUrl);
  }

}