import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-checkout-confirmation',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="checkout-confirmation">
      <h2>Confirmación de venta</h2>
      <p>Revisa los datos antes de confirmar tu compra.</p>
      <div class="confirmation-details">
        <p><strong>Subtotal:</strong> S/ {{ subtotal.toFixed(2) }}</p>
        <p><strong>IGV:</strong> S/ {{ igv.toFixed(2) }}</p>
        <p><strong>Total:</strong> S/ {{ total.toFixed(2) }}</p>
      </div>
    </div>
  `,
  styles: [`
    .checkout-confirmation {
      background: white;
      border-radius: 12px;
      padding: 24px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    }
    h2 {
      font-size: 18px;
      font-weight: 700;
      margin: 0 0 8px;
    }
    p {
      color: #666;
      margin: 0 0 16px;
    }
    .confirmation-details {
      background: #F5F5F5;
      padding: 16px;
      border-radius: 8px;
    }
  `]
})
export class CheckoutConfirmationComponent {
  subtotal = 0;
  igv = 0;
  total = 0;
}