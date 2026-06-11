import { Component, input } from '@angular/core';

@Component({
  selector: 'app-loading-spinner',
  standalone: true,
  imports: [],
  template: `
    <div class="loading-spinner" [class.small]="size() === 'small'" [class.large]="size() === 'large'">
      <div class="spinner-circle"></div>
      @if (message()) {
        <p class="spinner-message">{{ message() }}</p>
      }
    </div>
  `,
  styles: [`
    .loading-spinner {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      gap: 12px;
      padding: 32px;
    }
    .spinner-circle {
      width: 36px;
      height: 36px;
      border: 3px solid #E0E0E0;
      border-top-color: #E8344E;
      border-radius: 50%;
      animation: spin 0.8s linear infinite;
    }
    .loading-spinner.small .spinner-circle {
      width: 20px;
      height: 20px;
      border-width: 2px;
    }
    .loading-spinner.large .spinner-circle {
      width: 48px;
      height: 48px;
      border-width: 4px;
    }
    .spinner-message {
      color: #666;
      font-size: 14px;
      margin: 0;
    }
    @keyframes spin {
      to { transform: rotate(360deg); }
    }
  `]
})
export class LoadingSpinnerComponent {
  size = input<'small' | 'medium' | 'large'>('medium');
  message = input<string>('');
}
