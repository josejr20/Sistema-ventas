import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [],
  template: `
    <div class="confirm-overlay" (click)="cancel.emit()">
      <div class="confirm-dialog" (click)="$event.stopPropagation()">
        <h3 class="confirm-title">{{ title() }}</h3>
        <p class="confirm-message">{{ message() }}</p>
        <div class="confirm-actions">
          <button class="btn-cancel" (click)="cancel.emit()">{{ cancelLabel() }}</button>
          <button class="btn-confirm" (click)="confirm.emit()">{{ confirmLabel() }}</button>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .confirm-overlay {
      position: fixed;
      inset: 0;
      background: rgba(0, 0, 0, 0.5);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
      animation: fadeIn 0.2s ease;
    }
    .confirm-dialog {
      background: white;
      border-radius: 12px;
      padding: 24px;
      max-width: 400px;
      width: 90%;
      box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
    }
    .confirm-title {
      font-size: 18px;
      font-weight: 700;
      color: #000;
      margin: 0 0 8px;
    }
    .confirm-message {
      font-size: 14px;
      color: #666;
      margin: 0 0 24px;
      line-height: 1.5;
    }
    .confirm-actions {
      display: flex;
      gap: 12px;
      justify-content: flex-end;
    }
    .btn-cancel {
      padding: 10px 20px;
      border: 1px solid #E0E0E0;
      background: white;
      color: #333;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 500;
      cursor: pointer;
      transition: background 0.2s;
    }
    .btn-cancel:hover {
      background: #F5F5F5;
    }
    .btn-confirm {
      padding: 10px 20px;
      border: none;
      background: #E8344E;
      color: white;
      border-radius: 8px;
      font-size: 14px;
      font-weight: 600;
      cursor: pointer;
      transition: background 0.2s;
    }
    .btn-confirm:hover {
      background: #D02A40;
    }
    @keyframes fadeIn {
      from { opacity: 0; }
      to { opacity: 1; }
    }
  `]
})
export class ConfirmDialogComponent {
  title = input.required<string>();
  message = input.required<string>();
  confirmLabel = input<string>('Confirmar');
  cancelLabel = input<string>('Cancelar');
  confirm = output<void>();
  cancel = output<void>();
}
