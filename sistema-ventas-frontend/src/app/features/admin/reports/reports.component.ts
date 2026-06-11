import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../core/services/api.service';
import { LoadingSpinnerComponent } from '../../../shared/components/loading-spinner/loading-spinner.component';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, LoadingSpinnerComponent],
  template: `
    <div class="reports">
      <h1>Reportes</h1>
      @if (loading()) {
        <app-loading-spinner></app-loading-spinner>
      } @else {
        <p>Generación de reportes en desarrollo.</p>
      }
    </div>
  `,
  styles: [`
    .reports {
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
export class ReportsComponent {
  loading = signal(false);
}