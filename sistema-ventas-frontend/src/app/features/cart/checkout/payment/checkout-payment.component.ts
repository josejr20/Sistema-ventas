import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from '../../../../core/services/api.service';
import { AuthService } from '../../../../core/services/auth.service';
import { VentaRequest, DetalleVentaRequest, Producto, Cliente, Almacen } from '../../../../core/models';
import { LoadingSpinnerComponent } from '../../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-checkout-payment',
  standalone: true,
  imports: [CommonModule, FormsModule, LoadingSpinnerComponent, EmptyStateComponent],
  templateUrl: './checkout-payment.component.html',
  styleUrl: './checkout-payment.component.scss'
})
export class CheckoutPaymentComponent implements OnInit {
  cart = signal<any[]>([]);
  clients = signal<Cliente[]>([]);
  warehouses = signal<Almacen[]>([]);
  selectedClientId = signal<number | null>(null);
  selectedWarehouseId = signal<number | null>(null);
  observations = signal('');
  loading = signal(true);
  submitting = signal(false);
  error = signal<string | null>(null);

  constructor(private apiService: ApiService, private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    const cart = JSON.parse(localStorage.getItem('sv_cart') || '[]');
    this.cart.set(cart);
    this.loadClients();
    this.loadWarehouses();
    this.loading.set(false);
  }

  loadClients(): void {
    this.apiService.get<Cliente>('/clientes', { page: 0, size: 50 }).subscribe({
      next: (response: any) => {
        this.clients.set(response.data?.content ?? []);
      },
      error: () => {
        this.clients.set([]);
      }
    });
  }

  loadWarehouses(): void {
    this.apiService.get<Almacen>('/almacenes').subscribe({
      next: (response: any) => {
        this.warehouses.set(response.data?.content ?? []);
        if (this.warehouses().length > 0) {
          this.selectedWarehouseId.set(this.warehouses()[0].id);
        }
      },
      error: () => {
        this.warehouses.set([]);
      }
    });
  }

  get total(): number {
    return this.cart().reduce((sum, item) => sum + (item.precio * item.cantidad), 0);
  }

  submitSale(): void {
    if (!this.selectedWarehouseId()) {
      this.error.set('Selecciona un almacén');
      return;
    }

    this.submitting.set(true);
    this.error.set(null);

    const detalles: DetalleVentaRequest[] = this.cart().map((item) => ({
      productoId: item.id,
      cantidad: item.cantidad,
      descuentoPorcentaje: 0
    }));

    const request: VentaRequest = {
      clienteId: this.selectedClientId() ?? undefined,
      almacenId: this.selectedWarehouseId()!,
      moneda: 'PEN',
      observaciones: this.observations() || undefined,
      detalles
    };

    this.apiService.post<any>('/ventas', request).subscribe({
      next: (response: any) => {
        localStorage.removeItem('sv_cart');
        this.router.navigate(['/sales']);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Error al crear venta');
        this.submitting.set(false);
      }
    });
  }

  goToCatalog(): void {
    this.router.navigate(['/catalog']);
  }
}
