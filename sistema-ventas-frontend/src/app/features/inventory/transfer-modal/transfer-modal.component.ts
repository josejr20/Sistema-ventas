import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-transfer-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="transfer-modal">
      <h2>Transferir Stock</h2>
      <div class="form-group">
        <label for="origen">Almacén origen</label>
        <select id="origen" [(ngModel)]="fromWarehouse">
          <option [ngValue]="null">Seleccionar</option>
        </select>
      </div>
      <div class="form-group">
        <label for="destino">Almacén destino</label>
        <select id="destino" [(ngModel)]="toWarehouse">
          <option [ngValue]="null">Seleccionar</option>
        </select>
      </div>
      <button class="btn-transfer" (click)="transfer()">Transferir</button>
    </div>
  `,
  styles: [`
    .transfer-modal {
      background: white;
      border-radius: 12px;
      padding: 24px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    }
    h2 {
      font-size: 18px;
      font-weight: 700;
      margin: 0 0 16px;
    }
    .form-group {
      margin-bottom: 16px;
    }
    .form-group label {
      display: block;
      font-size: 14px;
      font-weight: 500;
      margin-bottom: 6px;
    }
    .form-group select {
      width: 100%;
      padding: 10px;
      border: 1px solid #E0E0E0;
      border-radius: 8px;
    }
    .btn-transfer {
      padding: 12px 24px;
      background: #E8344E;
      color: white;
      border: none;
      border-radius: 8px;
      cursor: pointer;
    }
  `]
})
export class TransferModalComponent {
  fromWarehouse = null;
  toWarehouse = null;

  transfer(): void {}
}