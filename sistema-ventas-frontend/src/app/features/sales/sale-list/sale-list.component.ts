import { Component, signal, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { AuthService } from '../../../core/services/auth.service';
import { Venta } from '../../../core/models';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { DateFormatPipe } from '../../../shared/pipes';

@Component({
  selector: 'app-sale-list',
  standalone: true,
  imports: [CommonModule, RouterLink, LoadingSpinnerComponent, EmptyStateComponent, CurrencyPipe, DateFormatPipe],
  templateUrl: './sale-list.component.html',
  styleUrl: './sale-list.component.scss'
})
export class SaleListComponent implements OnInit {
  sales = signal<Venta[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor(private apiService: ApiService, private authService: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.loadSales();
  }

  loadSales(): void {
    this.loading.set(true);
    this.apiService.get<Venta>('/ventas', { page: 0, size: 20 }).subscribe({
      next: (response: any) => {
        this.sales.set(response.data?.content ?? []);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Error al cargar ventas');
        this.loading.set(false);
      }
    });
  }

  getEstadoClass(codigo: string): string {
    const map: Record<string, string> = {
      PENDIENTE: 'estado-pendiente',
      PAGADO: 'estado-pagado',
      ANULADO: 'estado-anulado',
      DEVUELTO: 'estado-devuelto'
    };
    return map[codigo] || '';
  }

  goToCatalog(): void {
    this.router.navigate(['/catalog']);
  }
}
