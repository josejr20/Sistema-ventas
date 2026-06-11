import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../core/services/api.service';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';
import { EmptyStateComponent } from '../../../shared/components/empty-state/empty-state.component';

@Component({
  selector: 'app-movement-history',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent, EmptyStateComponent],
  template: `
    <div class="movement-history">
      <h1>Historial de Movimientos</h1>
      @if (loading()) {
        <app-loading-spinner message="Cargando movimientos..."></app-loading-spinner>
      } @else if (movements().length === 0) {
        <app-empty-state
          title="Sin movimientos"
          message="No hay movimientos de stock registrados."
        ></app-empty-state>
      } @else {
        <p>Lista de movimientos en desarrollo.</p>
      }
    </div>
  `,
  styles: [`
    .movement-history {
      max-width: 1000px;
      margin: 0 auto;
      padding: 24px;
    }
    h1 {
      font-size: 24px;
      font-weight: 700;
      margin: 0 0 24px;
    }
  `]
})
export class MovementHistoryComponent {
  movements = signal<any[]>([]);
  loading = signal(false);
}