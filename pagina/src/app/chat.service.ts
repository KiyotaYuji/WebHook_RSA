// pagina/src/app/chat.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

export interface EnviarMensagemPayload {
  texto: string;
}

export interface MensagemResponse {
  status: string;
  mensagem: string;
}

export interface MensagemRecebida {
  texto: string;
  assinaturaValida: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8080/api/chat';

  constructor(private http: HttpClient) { }

  setInstancia(porta: number): void {
    this.apiUrl = `http://localhost:${porta}/api/chat`;
  }

  enviarMensagem(texto: string): Observable<MensagemResponse> {
    const payload: EnviarMensagemPayload = { texto };
    return this.http.post<MensagemResponse>(`${this.apiUrl}/enviar`, payload)
      .pipe(
        catchError(err => {
          console.error('Erro na requisição:', err);
          return throwError(() => err);
        })
      );
  }

  getMensagens(): Observable<MensagemRecebida[]> {
    return this.http.get<MensagemRecebida[]>(`${this.apiUrl}/mensagens`)
      .pipe(
        catchError(err => {
          console.error('Erro ao obter mensagens:', err);
          return throwError(() => err);
        })
      );
  }
}
