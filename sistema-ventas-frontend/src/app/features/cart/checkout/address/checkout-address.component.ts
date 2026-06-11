import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-checkout-address',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="checkout-address">
      <h2>Dirección de entrega</h2>
      <div class="form-group">
        <label for="direccion">Dirección</label>
        <input type="text" id="direccion" [(ngModel)]="direccion" placeholder="Av. Principal 123" />
      </div>
      <div class="form-group">
        <label for="distrito">Distrito</label>
        <input type="text" id="distrito" [(ngModel)]="distrito" placeholder="San Isidro" />
      </div>
      <div class="form-group">
        <label for="provincia">Provincia</label>
        <input type="text" id="provincia" [(ngModel)]="provincia" placeholder="Lima" />
      </div>
    </div>
  `,
  styles: [`
    .checkout-address {
      background: white;
      border-radius: 12px;
      padding: 24px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    }
    h2 {
      font-size: 18px;
      font-weight: 700;
      margin: 0 0 16px;
    }
    .form-group {
      margin-bottom: 16px;
    }
    .form-group label {
      display: block;
      font-size: 14px;
      font-weight: 500;
      margin-bottom: 6px;
      color: #333;
    }
    .form-group input {
      width: 100%;
      padding: 10px 12px;
      border: 1px solid #E0E0E0;
      border-radius: 8px;
      font-size: 14px;
    }
  `]
})
export class CheckoutAddressComponent {
  direccion = '';
  distrito = '';
  provincia = '';
}