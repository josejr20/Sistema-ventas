import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CurrencyPipe } from '@angular/common';
import { ApiService } from '../../../core/services/api.service';
import { Venta } from '../../../core/models';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { DateFormatPipe } from '../../../shared/pipes';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent, CurrencyPipe, DateFormatPipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
  todaySales = signal<number>(0);
  pendingOrders = signal<number>(0);
  lowStockCount = signal<number>(0);
  newClients = signal<number>(0);
  recentSales = signal<Venta[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.loading.set(true);
    this.apiService.get<Venta>('/ventas', { page: 0, size: 5 }).subscribe({
      next: (response: any) => {
        const ventas = response.data?.content ?? [];
        this.recentSales.set(ventas);
        const total = ventas.reduce((sum: number, v: Venta) => sum + v.total, 0);
        this.todaySales.set(total);
        const pendientes = ventas.filter((v: Venta) => v.estadoVenta.codigo === 'PENDIENTE').length;
        this.pendingOrders.set(pendientes);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Error al cargar dashboard');
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
}
