import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LoadingSpinnerComponent],
  template: `
    <div class="register-container">
      <div class="register-card">
        <div class="register-header">
          <h1>Crear cuenta</h1>
          <p>Completa tus datos para registrarte</p>
        </div>

        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()" class="register-form">
          <div class="form-row">
            <div class="form-group">
              <label for="nombre">Nombre</label>
              <input type="text" id="nombre" formControlName="nombre" placeholder="Juan" required />
              @if (registerForm.get('nombre')?.invalid && registerForm.get('nombre')?.touched) {
                <span class="field-error">Nombre requerido</span>
              }
            </div>
            <div class="form-group">
              <label for="apellido">Apellido</label>
              <input type="text" id="apellido" formControlName="apellido" placeholder="Pérez" required />
            </div>
          </div>

          <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" formControlName="email" placeholder="correo@ejemplo.com" required />
            @if (registerForm.get('email')?.invalid && registerForm.get('email')?.touched) {
              <span class="field-error">Email inválido</span>
            }
          </div>

          <div class="form-group">
            <label for="dni">DNI (opcional)</label>
            <input type="text" id="dni" formControlName="dni" placeholder="12345678" maxlength="8" />
          </div>

          <div class="form-group">
            <label for="telefono">Teléfono (opcional)</label>
            <input type="tel" id="telefono" formControlName="telefono" placeholder="987654321" />
          </div>

          <div class="form-group">
            <label for="password">Contraseña</label>
            <input type="password" id="password" formControlName="password" placeholder="********" required />
            @if (registerForm.get('password')?.invalid && registerForm.get('password')?.touched) {
              <span class="field-error">Mínimo 6 caracteres</span>
            }
          </div>

          @if (error()) {
            <div class="error-message">{{ error() }}</div>
          }

          <button type="submit" class="btn-primary" [disabled]="registerForm.invalid || loading()">
            @if (loading()) {
              <app-loading-spinner size="small" message="Creando cuenta..."></app-loading-spinner>
            } @else {
              Crear cuenta
            }
          </button>
        </form>

        <div class="register-footer">
          <p>Ya tienes cuenta? <a routerLink="/login">Ingresa aquí</a></p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .register-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: #F5F5F5;
      padding: 24px;
    }
    .register-card {
      background: white;
      border-radius: 12px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      padding: 40px;
      width: 100%;
      max-width: 480px;
    }
    .register-header {
      text-align: center;
      margin-bottom: 32px;
    }
    .register-header h1 {
      font-size: 24px;
      font-weight: 700;
      color: #000;
      margin: 0 0 8px;
    }
    .register-header p {
      color: #666;
      font-size: 14px;
      margin: 0;
    }
    .register-form {
      display: flex;
      flex-direction: column;
      gap: 16px;
    }
    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 16px;
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
    .field-error {
      font-size: 12px;
      color: #E8344E;
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
    }
    .btn-primary:hover:not(:disabled) {
      background: #333;
    }
    .btn-primary:disabled {
      opacity: 0.7;
      cursor: not-allowed;
    }
    .register-footer {
      text-align: center;
      margin-top: 24px;
      font-size: 14px;
      color: #666;
    }
    .register-footer a {
      color: #E8344E;
      text-decoration: none;
      font-weight: 500;
    }
    .register-footer a:hover {
      text-decoration: underline;
    }
    @media (max-width: 480px) {
      .form-row {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class RegisterComponent {
  registerForm: any;
  error = signal<string | null>(null);

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {
    this.registerForm = this.fb.group({
      nombre: ['', [Validators.required]],
      apellido: [''],
      email: ['', [Validators.required, Validators.email]],
      dni: [''],
      telefono: [''],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  get loading() {
    return this.authService.loading;
  }

  onSubmit(): void {
    if (this.registerForm.invalid) return;

    this.error.set(null);
    this.authService.connectionError.set(null);

    this.authService.register(this.registerForm.value).subscribe({
      next: () => {
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.error.set(err.error?.message || this.authService.connectionError() || 'Error al crear cuenta');
      }
    });
  }
}
