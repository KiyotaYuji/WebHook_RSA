import { Component } from '@angular/core';

@Component({
  selector: 'app-teste',
  standalone: true,
  template: `
    <div style="padding: 20px; background: lightblue;">
      <h1>Componente de Teste</h1>
      <p>Se você consegue ver isso, o Angular está renderizando corretamente.</p>
    </div>
  `
})
export class TesteComponent {}
