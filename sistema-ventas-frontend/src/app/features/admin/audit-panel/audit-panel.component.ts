import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-audit-panel',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent],
  template: `
    <div class="audit-panel">
      <h1>Panel de Auditoría</h1>
      @if (loading()) {
        <app-loading-spinner></app-loading-spinner>
      } @else {
        <p>Funcionalidad en desarrollo.</p>
      }
    </div>
  `,
  styles: [`
    .audit-panel {
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
export class AuditPanelComponent {
  loading = signal(false);
}