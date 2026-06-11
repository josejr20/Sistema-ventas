import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../core/services/api.service';
import { Inventario, Almacen } from '../../../core/models';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-stock-panel',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent, EmptyStateComponent],
  templateUrl: './stock-panel.component.html',
  styleUrl: './stock-panel.component.scss'
})
export class StockPanelComponent implements OnInit {
  inventory = signal<Inventario[]>([]);
  warehouses = signal<Almacen[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  selectedWarehouse = signal<number | null>(null);

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadWarehouses();
    this.loadInventory();
  }

  loadWarehouses(): void {
    this.apiService.get<Almacen>('/almacenes').subscribe({
      next: (response: any) => {
        this.warehouses.set(response.data?.content ?? []);
      },
      error: () => {
        this.warehouses.set([]);
      }
    });
  }

  loadInventory(): void {
    this.loading.set(true);
    const params: any = { page: 0, size: 50 };
    if (this.selectedWarehouse()) {
      params.almacenId = this.selectedWarehouse();
    }
    this.apiService.get<Inventario>('/inventario', params).subscribe({
      next: (response: any) => {
        this.inventory.set(response.data?.content ?? []);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Error al cargar inventario');
        this.loading.set(false);
      }
    });
  }

  getStockClass(item: Inventario): string {
    if (item.stockActual === 0) return 'stock-zero';
    if (item.stockMinimo && item.stockActual < item.stockMinimo) return 'stock-low';
    return 'stock-ok';
  }

  onWarehouseChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.selectedWarehouse.set(+(select.value));
    this.loadInventory();
  }
}