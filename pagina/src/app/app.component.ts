import { Component } from '@angular/core';
import { ChatComponent } from './chat/chat.component';
import {TesteComponent} from './chat/teste';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    ChatComponent,
    TesteComponent
  ],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'pagina';
}
