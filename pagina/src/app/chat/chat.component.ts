import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ChatService, MensagemRecebida } from '../chat.service'; // Importe MensagemRecebida se ainda não estiver
import { Subscription, interval, of } from 'rxjs';
import { switchMap, catchError, finalize } from 'rxjs/operators';

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule
  ],
  templateUrl: './chat.component.html',
  styleUrls: ['./chat.component.css']
})
export class ChatComponent implements OnInit, OnDestroy {
  mensagemParaEnviar: string = "";
  // remetente: string = "AngularApp"; // Removido pois não é usado
  respostaDoServidor: string = "";
  mensagensRecebidas: MensagemRecebida[] = []; // Tipagem mais específica
  instanciaAtual: number = 8080; // Porta padrão da primeira instância
  carregando: boolean = false;
  conectado: boolean = false;
  inicializado: boolean = false; // Para controlar o estado inicial de carregamento
  private atualizacaoSubscription?: Subscription;

  constructor(private chatService: ChatService, private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    console.log('ChatComponent ngOnInit: Iniciando componente.');
    //this.selecionarInstancia(this.instanciaAtual); // Carrega mensagens para a instância padrão
    this.inicializado = true;
    this.conectado = false;
  }

  ngOnDestroy(): void {
    console.log('ChatComponent ngOnDestroy: Destruindo componente.');
    this.pararAtualizacaoAutomatica();
  }

  carregarMensagens(): void {
    this.carregando = true;
    console.log(`carregarMensagens (porta ${this.instanciaAtual}): Iniciando.`);

    this.chatService.getMensagens().subscribe({
      next: (mensagens) => {
        console.log(`Sucesso. Mensagens recebidas: ${mensagens.length}`);
        this.mensagensRecebidas = mensagens;
        this.conectado = true;
        this.carregando = false;
        this.inicializado = true;
        this.iniciarAtualizacaoAutomatica();
      },
      error: (error) => {
        console.error(`Erro ao carregar mensagens:`, error);
        this.mensagensRecebidas = [];
        this.conectado = false;
        this.carregando = false;
        this.inicializado = true;
        this.respostaDoServidor = `Erro de conexão: ${error.message || 'Erro desconhecido'}`;
      }
    });
  }

  iniciarAtualizacaoAutomatica(): void {
    this.pararAtualizacaoAutomatica(); // Garante que não haja múltiplas subscriptions
    console.log(`iniciarAtualizacaoAutomatica (porta ${this.instanciaAtual}): Iniciando polling.`);
    this.atualizacaoSubscription = interval(5000).pipe( // 5 segundos
      switchMap(() => {
        console.log(`Atualização automática (porta ${this.instanciaAtual}): Chamando getMensagens.`);
        return this.chatService.getMensagens().pipe(
          catchError(error => {
            console.error(`Erro na atualização automática (porta ${this.instanciaAtual}):`, error);
            console.log(`Atualização automática (porta ${this.instanciaAtual}): definindo conectado = false devido a erro.`);
            this.conectado = false;
            this.respostaDoServidor = `Perda de conexão com a instância ${this.instanciaAtual}. Tentando reconectar...`;
            this.cdr.detectChanges();
            return of([]); // Retorna um array vazio para não quebrar o pipe principal
          })
        );
      })
    ).subscribe({
      next: (mensagens) => {
        // Só atualiza se conectado era false e agora temos mensagens (ou se conectado já era true)
        if (!this.conectado && mensagens.length > 0) {
          this.conectado = true;
          this.respostaDoServidor = ""; // Limpa mensagem de erro de conexão
        } else if (this.conectado && mensagens.length === 0 && this.mensagensRecebidas.length > 0) {
          // Se estava conectado e agora não há mensagens, pode ser um problema, mas mantém conectado por enquanto
          // A lógica de erro no catchError acima deve tratar a desconexão.
        }
        // Se a requisição foi bem sucedida (mesmo que vazia), mas o catchError acima definiu conectado = false,
        // precisamos reavaliar.
        if (this.conectado) { // Se o catchError não pegou, estamos bem
          console.log(`Atualização automática (porta ${this.instanciaAtual}): Sucesso. Mensagens recebidas: ${mensagens.length}`);
          this.mensagensRecebidas = mensagens;
          if (this.respostaDoServidor.includes("Perda de conexão")) { // Limpa mensagem de erro se reconectou
            this.respostaDoServidor = "";
          }
        }
        this.cdr.detectChanges();
      },
      error: (error) => { // Erro fatal na subscrição do interval/switchMap (menos provável)
        console.error(`Erro fatal na subscrição da atualização automática (porta ${this.instanciaAtual}):`, error);
        this.conectado = false;
        this.respostaDoServidor = `Erro crítico na atualização automática com a instância ${this.instanciaAtual}.`;
        this.cdr.detectChanges();
      }
    });
  }

  pararAtualizacaoAutomatica(): void {
    if (this.atualizacaoSubscription) {
      console.log(`pararAtualizacaoAutomatica (porta ${this.instanciaAtual}): Parando polling.`);
      this.atualizacaoSubscription.unsubscribe();
      this.atualizacaoSubscription = undefined;
    }
  }

  enviar(): void {
    if (!this.mensagemParaEnviar.trim()) {
      this.respostaDoServidor = "A mensagem não pode estar vazia.";
      return;
    }
    if (!this.conectado) {
      this.respostaDoServidor = "Não conectado à instância do backend. Verifique a conexão.";
      return;
    }

    this.carregando = true; // Pode ser um 'enviando' específico
    console.log(`enviar (porta ${this.instanciaAtual}): Enviando mensagem: "${this.mensagemParaEnviar}"`);
    this.chatService.enviarMensagem(this.mensagemParaEnviar).pipe(
      finalize(() => {
        this.carregando = false; // Ou 'enviando = false'
        this.cdr.detectChanges();
      })
    ).subscribe({
      next: (response) => {
        console.log(`enviar (porta ${this.instanciaAtual}): Sucesso. Resposta:`, response);
        this.respostaDoServidor = `Servidor: ${response.mensagem}`;
        this.mensagemParaEnviar = ""; // Limpa o campo após o envio
        // Opcional: chamar carregarMensagens() para atualizar a lista imediatamente,
        // ou esperar pela próxima atualização automática.
        // this.carregarMensagens();
      },
      error: (error) => {
        console.error(`Erro ao enviar mensagem (porta ${this.instanciaAtual}):`, error);
        this.respostaDoServidor = `Erro ao enviar mensagem: ${error.message || 'Verifique o console para detalhes.'}`;
        if (error.status === 0 || error.status === 503) {
          this.conectado = false; // Perda de conexão
          this.pararAtualizacaoAutomatica();
        }
      }
    });
  }

  selecionarInstancia(porta: number): void {
    console.log(`selecionarInstancia: Trocando para porta ${porta}. Instância atual era ${this.instanciaAtual}`);
    this.pararAtualizacaoAutomatica(); // Para a atualização da instância antiga
    this.instanciaAtual = porta;
    this.chatService.setInstancia(porta);
    this.mensagensRecebidas = []; // Limpa mensagens da instância anterior
    this.conectado = false; // Reseta o status de conexão
    this.respostaDoServidor = `Conectando à instância na porta ${porta}...`;
    this.carregarMensagens(); // Inicia o carregamento para a nova instância
  }
}
