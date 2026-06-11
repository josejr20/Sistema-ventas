import { Component, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';
import { AuthService } from '../../../core/services/auth.service';
import { StorageService } from '../../../core/services/storage.service';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { Inventario, Almacen } from '../../../core/models';

@Component({
  selector: 'app-cart-page',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent, EmptyStateComponent, ConfirmDialogComponent],
  templateUrl: './cart-page.component.html',
  styleUrl: './cart-page.component.scss'
})
export class CartPageComponent implements OnInit {
  items = signal<any[]>([]);
  loading = signal(true);
  showConfirm = signal(false);

  constructor(
    private storageService: StorageService,
    private authService: AuthService,
    private router: Router
  ) {
    this.items.set(this.storageService.getCart());
  }

  ngOnInit(): void {
    this.loading.set(false);
  }

  get total(): number {
    return this.items().reduce((sum, item) => sum + (item.precio * item.cantidad), 0);
  }

  remove(index: number): void {
    this.storageService.removeFromCart(index);
    this.items.set(this.storageService.getCart());
  }

  updateQuantity(index: number, delta: number): void {
    const items = this.items();
    const newQty = items[index].cantidad + delta;
    if (newQty <= 0) {
      this.remove(index);
      return;
    }
    items[index].cantidad = newQty;
    this.storageService.setCart(items);
    this.items.set([...items]);
  }

  checkout(): void {
    if (!this.authService.isAuthenticated()) {
      alert('Debes iniciar sesión para continuar');
      this.router.navigate(['/login']);
      return;
    }
    this.router.navigate(['/checkout']);
  }

  goToCatalog(): void {
    this.router.navigate(['/catalog']);
  }

  clearCart(): void {
    this.storageService.clearCart();
    this.items.set([]);
  }
}
