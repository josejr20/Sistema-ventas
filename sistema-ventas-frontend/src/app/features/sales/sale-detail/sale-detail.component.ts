import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { Venta } from '../../../core/models';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { CurrencyPipe } from '@angular/common';
import { DateFormatPipe } from '../../../shared/pipes';

@Component({
  selector: 'app-sale-detail',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent, CurrencyPipe, DateFormatPipe],
  template: `
    <div class="sale-detail">
      @if (loading()) {
        <app-loading-spinner message="Cargando venta..."></app-loading-spinner>
      } @else if (error() || !sale()) {
        <p class="error-message">{{ error() || 'Venta no encontrada' }}</p>
      } @else {
        <div class="detail-card">
          <h1>Venta #{{ sale()!.numeroVenta }}</h1>
          <div class="detail-info">
            <p><strong>Cliente:</strong> {{ sale()!.cliente?.nombre || 'Consumidor final' }}</p>
            <p><strong>Fecha:</strong> {{ sale()!.fechaVenta | dateFormat:'long' }}</p>
            <p><strong>Total:</strong> {{ sale()!.total | currency:'PEN' }}</p>
            <p><strong>Estado:</strong> {{ sale()!.estadoVenta.descripcion }}</p>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    .sale-detail {
      max-width: 800px;
      margin: 0 auto;
      padding: 24px;
    }
    .detail-card {
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
    .detail-info {
      display: flex;
      flex-direction: column;
      gap: 8px;
      font-size: 14px;
    }
    .error-message {
      color: #E8344E;
      padding: 24px;
      text-align: center;
    }
  `]
})
export class SaleDetailComponent {
  sale = signal<Venta | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);
  saleId: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private apiService: ApiService
  ) {}

  ngOnInit(): void {
    this.saleId = this.route.snapshot.paramMap.get('id');
    if (this.saleId) {
      this.loadSale();
    }
  }

  loadSale(): void {
    this.loading.set(true);
    this.apiService.getOne<Venta>(`/ventas/${this.saleId}`).subscribe({
      next: (response: any) => {
        this.sale.set(response.data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Error al cargar venta');
        this.loading.set(false);
      }
    });
  }
}