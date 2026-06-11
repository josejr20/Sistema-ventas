import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, LoadingSpinnerComponent],
  template: `
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <h1>Sistema de Ventas</h1>
          <p>Ingresa a tu cuenta</p>
        </div>

        <form (ngSubmit)="onSubmit()" class="login-form">
          <div class="form-group">
            <label for="email">Email</label>
            <input
              type="email"
              id="email"
              name="email"
              [(ngModel)]="email"
              placeholder="correo@ejemplo.com"
              required
              autocomplete="email"
            />
          </div>

          <div class="form-group">
            <label for="password">Contraseña</label>
            <input
              type="password"
              id="password"
              name="password"
              [(ngModel)]="password"
              placeholder="********"
              required
              autocomplete="current-password"
            />
          </div>

          @if (error()) {
            <div class="error-message">{{ error() }}</div>
          }

          <button type="submit" class="btn-primary" [disabled]="loading()">
            @if (loading()) {
              <app-loading-spinner size="small"></app-loading-spinner>
            } @else {
              Ingresar
            }
          </button>
        </form>

        <div class="login-footer">
          <p>No tienes cuenta? <a routerLink="/register">Regístrate</a></p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #F5F5F5;
      padding: 24px;
    }
    .login-card {
      background: white;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      padding: 40px;
      width: 100%;
      max-width: 400px;
    }
    .login-header {
      text-align: center;
      margin-bottom: 32px;
    }
    .login-header h1 {
      font-size: 24px;
      font-weight: 700;
      color: #000;
      margin: 0 0 8px;
    }
    .login-header p {
      color: #666;
      font-size: 14px;
      margin: 0;
    }
    .login-form {
      display: flex;
      flex-direction: column;
      gap: 20px;
    }
    .form-group {
      display: flex;
      flex-direction: column;
      gap: 6px;
    }
    .form-group label {
      font-size: 14px;
      font-weight: 500;
      color: #333;
    }
    .form-group input {
      padding: 12px 16px;
      border: 1px solid #E0E0E0;
      border-radius: 8px;
      font-size: 16px;
      outline: none;
      transition: border-color 0.2s;
    }
    .form-group input:focus {
      border-color: #E8344E;
    }
    .error-message {
      background: #FFF0F0;
      color: #E8344E;
      padding: 12px;
      border-radius: 8px;
      font-size: 14px;
      border: 1px solid #FFD0D0;
    }
    .btn-primary {
      padding: 14px;
      background: #000;
      color: white;
      border: none;
      border-radius: 8px;
      font-size: 16px;
      font-weight: 600;
      cursor: pointer;
      transition: background 0.2s;
      display: flex;
      align-items: center;
      justify-content: center;
      min-height: 48px;
    }
    .btn-primary:hover:not(:disabled) {
      background: #333;
    }
    .btn-primary:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }
    .login-footer {
      text-align: center;
      margin-top: 24px;
      font-size: 14px;
      color: #666;
    }
    .login-footer a {
      color: #E8344E;
      text-decoration: none;
      font-weight: 500;
    }
    .login-footer a:hover {
      text-decoration: underline;
    }
  `]
})
export class LoginComponent {
  email = signal('');
  password = signal('');
  error = signal<string | null>(null);

  constructor(private authService: AuthService, private router: Router) {}

  get loading() {
    return this.authService.loading;
  }

  onSubmit(): void {
    this.error.set(null);
    this.authService.connectionError.set(null);

    this.authService.login({ email: this.email(), password: this.password() }).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.error.set(err.error?.message || this.authService.connectionError() || 'Credenciales inválidas');
      }
    });
  }
}
