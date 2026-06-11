import { Component, signal, computed, inject } from '@angular/core';
import { RouterLink, RouterOutlet, Router } from '@angular/router';
import { AuthService } from './core/services/auth.service';
import { StorageService } from './core/services/storage.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterLink, RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class AppComponent {
  title = signal('sistema-ventas-frontend');
  cartItems = signal<any[]>([]);
  cartCount = computed(() => this.cartItems().length);
  isDarkMode = signal(false);
  showUserMenu = signal(false);

  authService = inject(AuthService);
  storageService = inject(StorageService);
  router = inject(Router);

  constructor() {
    this.cartItems.set(this.storageService.getCart());
  }

  get isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }

  toggleDarkMode(): void {
    this.isDarkMode.update((v) => !v);
    document.documentElement.setAttribute('data-theme', this.isDarkMode() ? 'dark' : 'light');
  }

  toggleUserMenu(): void {
    this.showUserMenu.update(v => !v);
  }

  goTo(route: string): void {
    this.showUserMenu.set(false);
    this.router.navigate([route]);
  }
}