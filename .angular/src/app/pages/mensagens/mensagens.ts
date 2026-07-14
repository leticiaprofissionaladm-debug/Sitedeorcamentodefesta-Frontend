import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-mensagens',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './mensagens.html',
  styleUrl: './mensagens.css'
})
export class MensagensComponent {

  faqAberto = -1;

  abrirWhatsapp(): void {

    const telefone = '5521969252477';

    const mensagem = encodeURIComponent(
      'Olá! Gostaria de solicitar um orçamento no Festa Planner.'
    );

    window.open(
      `https://wa.me/${telefone}?text=${mensagem}`,
      '_blank'
    );

  }

  abrirIA(): void {

    alert(
      'Olá! Sou o assistente virtual do Festa Planner 🤖\n\n' +
      'Em breve vou ajudar com dúvidas sobre eventos e orçamentos.'
    );

  }

  toggleFaq(index: number): void {

    if (this.faqAberto === index) {
      this.faqAberto = -1;
    } else {
      this.faqAberto = index;
    }

  }

}