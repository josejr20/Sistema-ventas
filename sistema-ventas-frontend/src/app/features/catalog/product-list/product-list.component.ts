import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { Producto } from '../../../core/models';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { PriceDisplayComponent } from '../../../shared/components/price-display/price-display.component';
import { TruncatePipe } from '../../../shared/pipes';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterLink, LoadingSpinnerComponent, EmptyStateComponent, PriceDisplayComponent, TruncatePipe],
  templateUrl: './product-list.component.html',
  styleUrl: './product-list.component.scss'
})
export class ProductListComponent implements OnInit {
  products = signal<Producto[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  page = 0;
  size = 20;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading.set(true);
    this.apiService.get<Producto>('/productos', { page: this.page, size: this.size }).subscribe({
      next: (response: any) => {
        this.products.set(response.data?.content ?? []);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Error al cargar productos');
        this.loading.set(false);
      }
    });
  }
}
