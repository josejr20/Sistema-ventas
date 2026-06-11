import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { Producto } from '../../../core/models';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { PriceDisplayComponent } from '../../../shared/components/price-display/price-display.component';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent, EmptyStateComponent, PriceDisplayComponent],
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss'
})
export class ProductDetailComponent implements OnInit {
  product = signal<Producto | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);
  quantity = signal(1);
  productId: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private apiService: ApiService
  ) {}

  ngOnInit(): void {
    this.productId = this.route.snapshot.paramMap.get('id');
    if (this.productId) {
      this.loadProduct();
    }
  }

  loadProduct(): void {
    this.loading.set(true);
    this.apiService.getOne<Producto>(`/productos/${this.productId}`).subscribe({
      next: (response: any) => {
        this.product.set(response.data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Error al cargar producto');
        this.loading.set(false);
      }
    });
  }

  get subtotal(): number {
    const p = this.product();
    return p ? p.precioVenta * this.quantity() : 0;
  }

  decreaseQuantity(): void {
    this.quantity.update(q => Math.max(1, q - 1));
  }

  increaseQuantity(): void {
    this.quantity.update(q => q + 1);
  }

  addToCart(): void {
    const p = this.product();
    if (!p) return;
    const items = JSON.parse(localStorage.getItem('sv_cart') || '[]');
    const existing = items.find((item: any) => item.id === p.id);
    if (existing) {
      existing.cantidad += this.quantity();
    } else {
      items.push({
        id: p.id,
        nombre: p.nombre,
        precio: p.precioVenta,
        cantidad: this.quantity(),
        imagenUrl: p.imagenUrl
      });
    }
    localStorage.setItem('sv_cart', JSON.stringify(items));
    this.router.navigate(['/cart']);
  }

  goToCatalog(): void {
    this.router.navigate(['/catalog']);
  }
}