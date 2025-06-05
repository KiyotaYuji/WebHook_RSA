import { bootstrapApplication } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptorsFromDi, withFetch } from '@angular/common/http';

import { AppComponent } from './app/app.component';
import { routes } from './app/app.routes';
import { appConfig } from './app/app.config'; // Importe appConfig

console.log('SCRIPT main.ts: Iniciando execução.'); // Log inicial

bootstrapApplication(AppComponent, appConfig) // Use appConfig aqui
  .then(ref => {
    console.log('SCRIPT main.ts: AppComponent bootstrap SUCESSO!', ref);
    // Pequeno delay para garantir que o DOM possa ter sido atualizado
    setTimeout(() => {
      const appRoot = document.querySelector('app-root');
      if (appRoot) {
        console.log('SCRIPT main.ts: Elemento <app-root> ENCONTRADO no DOM.');
        if (appRoot.innerHTML.trim() !== '') {
          console.log('SCRIPT main.ts: <app-root> TEM conteúdo:', appRoot.innerHTML);
        } else {
          console.warn('SCRIPT main.ts: <app-root> está VAZIO após bootstrap.');
        }
      } else {
        console.error('SCRIPT main.ts: Elemento <app-root> NÃO ENCONTRADO no DOM após bootstrap.');
        console.log('SCRIPT main.ts: Conteúdo atual do body:', document.body.innerHTML);
      }
    }, 100); // 100ms de delay
  })
  .catch(err => {
    console.error('SCRIPT main.ts: Erro FATAL no bootstrapApplication:', err);
    // Tenta exibir o erro na tela em branco
    document.body.innerHTML = `<div style="color: red; padding: 20px; font-family: monospace;">
                                <h1>Erro no Bootstrap do Angular</h1>
                                <pre>${err.message || JSON.stringify(err)}</pre>
                              </div>`;
  });
