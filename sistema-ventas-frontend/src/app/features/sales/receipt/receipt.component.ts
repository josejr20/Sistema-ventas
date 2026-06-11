import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Venta } from '../../../core/models';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-receipt',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent, CurrencyPipe],
  template: `
    <div class="receipt">
      @if (loading()) {
        <app-loading-spinner message="Cargando comprobante..."></app-loading-spinner>
      } @else if (sale()) {
        <div class="receipt-card">
          <h1>Comprobante de Venta</h1>
          <p>Número: {{ sale()!.numeroVenta }}</p>
          <p>Total: {{ sale()!.total | currency:'PEN' }}</p>
          <button class="btn-print" (click)="print()">Imprimir</button>
        </div>
      }
    </div>
  `,
  styles: [`
    .receipt {
      max-width: 600px;
      margin: 0 auto;
      padding: 24px;
    }
    .receipt-card {
      background: white;
      border-radius: 12px;
      padding: 24px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    }
    h1 {
      font-size: 20px;
      font-weight: 700;
      margin: 0 0 16px;
    }
    p {
      margin: 0 0 8px;
      font-size: 14px;
    }
    .btn-print {
      margin-top: 16px;
      padding: 10px 24px;
      background: #000;
      color: white;
      border: none;
      border-radius: 8px;
      cursor: pointer;
    }
  `]
})
export class ReceiptComponent {
  sale = signal<Venta | null>(null);
  loading = signal(true);

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    const saleId = this.route.snapshot.paramMap.get('id');
    if (saleId) {
      this.loading.set(false);
    }
  }

  print(): void {
    window.print();
  }
}