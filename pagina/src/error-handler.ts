import { ErrorHandler, Injectable } from '@angular/core';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  handleError(error: any): void {
    console.error('Erro global capturado:', error);
    // Opcional: adicione código para mostrar um alerta ou elemento na tela
    const errorDiv = document.createElement('div');
    errorDiv.textContent = 'Erro na aplicação: ' + (error.message || 'Desconhecido');
    errorDiv.style.cssText = 'position:fixed;top:0;left:0;background:red;color:white;padding:10px;z-index:9999;';
    document.body.appendChild(errorDiv);
  }
}
