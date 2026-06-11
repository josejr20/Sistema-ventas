import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'catalog', pathMatch: 'full' },
  { path: 'login', loadComponent: () => import('./features/auth/login/login.component').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./features/auth/register/register.component').then(m => m.RegisterComponent) },
  { path: 'catalog', loadComponent: () => import('./features/catalog/product-list/product-list.component').then(m => m.ProductListComponent) },
  { path: 'product/:id', loadComponent: () => import('./features/catalog/product-detail/product-detail.component').then(m => m.ProductDetailComponent) },
  { path: 'cart', loadComponent: () => import('./features/cart/cart-page/cart-page.component').then(m => m.CartPageComponent) },
  { path: 'checkout', loadComponent: () => import('./features/cart/checkout/payment/checkout-payment.component').then(m => m.CheckoutPaymentComponent) },
  { path: 'store', loadComponent: () => import('./features/catalog/product-list/product-list.component').then(m => m.ProductListComponent) },
  { path: 'notifications', loadComponent: () => import('./shared/components/empty-state/empty-state.component').then(m => m.EmptyStateComponent) },
  { path: 'coupons', loadComponent: () => import('./shared/components/empty-state/empty-state.component').then(m => m.EmptyStateComponent) },
  { path: 'points', loadComponent: () => import('./shared/components/empty-state/empty-state.component').then(m => m.EmptyStateComponent) },
  { path: 'recently-viewed', loadComponent: () => import('./shared/components/empty-state/empty-state.component').then(m => m.EmptyStateComponent) },
  { path: 'services', loadComponent: () => import('./shared/components/empty-state/empty-state.component').then(m => m.EmptyStateComponent) },
  { path: 'sales', loadComponent: () => import('./features/sales/sale-list/sale-list.component').then(m => m.SaleListComponent), canActivate: [authGuard] },
  { path: 'inventory', loadComponent: () => import('./features/inventory/stock-panel/stock-panel.component').then(m => m.StockPanelComponent), canActivate: [authGuard] },
  { path: 'admin', loadComponent: () => import('./features/admin/dashboard/dashboard.component').then(m => m.DashboardComponent), canActivate: [authGuard] },
  { path: 'acceso-denegado', loadComponent: () => import('./shared/components/empty-state/empty-state.component').then(m => m.EmptyStateComponent) }
];
