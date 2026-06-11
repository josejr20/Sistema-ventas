import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { Producto } from '../../../core/models';

@Component({
  selector: 'app-product-management',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent],
  template: `
    <div class="product-management">
      <h1>Gestión de Productos</h1>
      @if (loading()) {
        <app-loading-spinner></app-loading-spinner>
      } @else {
        <p>Listado de productos en desarrollo.</p>
      }
    </div>
  `,
  styles: [`
    .product-management {
      max-width: 1000px;
      margin: 0 auto;
      padding: 24px;
    }
    h1 {
      font-size: 24px;
      font-weight: 700;
      margin: 0 0 16px;
    }
  `]
})
export class ProductManagementComponent {
  products = signal<Producto[]>([]);
  loading = signal(false);
}