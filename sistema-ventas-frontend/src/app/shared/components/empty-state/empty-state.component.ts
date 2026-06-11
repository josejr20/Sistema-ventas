import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [],
  template: `
    <div class="empty-state">
      <div class="empty-icon">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <line x1="12" y1="8" x2="12" y2="12"></line>
          <line x1="12" y1="16" x2="12.01" y2="16"></line>
        </svg>
      </div>
      <h3 class="empty-title">{{ title() }}</h3>
      <p class="empty-message">{{ message() }}</p>
      @if (actionLabel()) {
        <button class="empty-action" (click)="onAction()">{{ actionLabel() }}</button>
      }
    </div>
  `,
  styles: [`
    .empty-state {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 48px 24px;
      text-align: center;
      gap: 12px;
    }
    .empty-icon {
      color: #CCCCCC;
      margin-bottom: 8px;
    }
    .empty-title {
      font-size: 18px;
      font-weight: 600;
      color: #333;
      margin: 0;
    }
    .empty-message {
      font-size: 14px;
      color: #666;
      max-width: 320px;
      margin: 0;
      line-height: 1.5;
    }
    .empty-action {
      margin-top: 16px;
      padding: 10px 24px;
      background: #E8344E;
      color: white;
      border: none;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 600;
      cursor: pointer;
      transition: background 0.2s;
    }
    .empty-action:hover {
      background: #D02A40;
    }
  `]
})
export class EmptyStateComponent {
  title = input.required<string>();
  message = input<string>('');
  actionLabel = input<string>('');
  action = output<void>();

  onAction(): void {
    this.action.emit();
  }
}
