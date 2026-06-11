import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { Usuario } from '../../../core/models';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent],
  template: `
    <div class="user-management">
      <h1>Gestión de Usuarios</h1>
      @if (loading()) {
        <app-loading-spinner></app-loading-spinner>
      } @else {
        <p>Administración de usuarios en desarrollo.</p>
      }
    </div>
  `,
  styles: [`
    .user-management {
      max-width: 1000px;
      margin: 0 auto;
      padding: 24px;
    }
    h1 {
      font-size: 24px;
      font-weight: 700;
      margin: 0 0 16px;
    }
  `]
})
export class UserManagementComponent {
  users = signal<Usuario[]>([]);
  loading = signal(false);
}