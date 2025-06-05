import { ApplicationConfig, mergeApplicationConfig } from '@angular/core';
import { provideServerRendering } from '@angular/platform-server';
import { appConfig } from './app.config'; // Supondo que você tenha um app.config.ts
import { provideHttpClient, withInterceptorsFromDi, withFetch } from '@angular/common/http';

const serverConfig: ApplicationConfig = {
  providers: [
    provideServerRendering(),
    // Adicione o provideHttpClient aqui
    provideHttpClient(
      withInterceptorsFromDi(),
      withFetch()
    )
  ]
};

// Mescle as configurações do appConfig com o serverConfig
export const config = mergeApplicationConfig(appConfig, serverConfig);
