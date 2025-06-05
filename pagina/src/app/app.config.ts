import { ApplicationConfig, ErrorHandler, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { GlobalErrorHandler} from '../error-handler';

export const appConfig: ApplicationConfig = {
  providers: [
     provideZoneChangeDetection({
       eventCoalescing: true,
       runCoalescing: true
    }),
    { provide: ErrorHandler, useClass: GlobalErrorHandler },
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi())
  ]
};
