import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../core/services/api.service';
import { CurrencyPipe } from '../../../shared/pipes';

@Component({
  selector: 'app-cart-summary',
  standalone: true,
  imports: [CommonModule, CurrencyPipe],
  template: `
    <div class="cart-summary-component">
      <div class="summary-content">
        <div class="summary-row">
          <span>Subtotal</span>
          <span>{{ subtotal | currency }}</span>
        </div>
        <div class="summary-row">
          <span>IGV (18%)</span>
          <span>{{ igv | currency }}</span>
        </div>
        <div class="summary-row total">
          <span>Total</span>
          <span>{{ total | currency }}</span>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .cart-summary-component {
      background: white;
      border-radius: 12px;
      padding: 20px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    }
    .summary-content {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .summary-row {
      display: flex;
      justify-content: space-between;
      font-size: 14px;
      color: #666;
    }
    .summary-row.total {
      font-weight: 700;
      font-size: 16px;
      color: #000;
      padding-top: 12px;
      border-top: 1px solid #E0E0E0;
    }
  `]
})
export class CartSummaryComponent {
  subtotal = 0;
  igv = 0;
  total = 0;
}