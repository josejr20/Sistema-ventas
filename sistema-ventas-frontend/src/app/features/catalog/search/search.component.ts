import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../../core/services/api.service';

@Component({
  selector: 'app-search',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  template: `
    <div class="search-page">
      <h1>Buscar Productos</h1>
      <div class="search-form">
        <input type="text" [(ngModel)]="query" placeholder="Buscar por nombre..." />
        <button (click)="search()">Buscar</button>
      </div>
      @if (results().length > 0) {
        <div class="search-results">
          @for (product of results(); track product.id) {
            <a [routerLink]="['/product', product.id]" class="search-item">
              {{ product.nombre }}
            </a>
          }
        </div>
      } @else if (searched() && results().length === 0) {
        <p class="no-results">No se encontraron resultados.</p>
      }
    </div>
  `,
  styles: [`
    .search-page {
      max-width: 800px;
      margin: 0 auto;
      padding: 24px;
    }
    h1 {
      font-size: 24px;
      font-weight: 700;
      margin: 0 0 24px;
    }
    .search-form {
      display: flex;
      gap: 12px;
      margin-bottom: 24px;
    }
    input {
      flex: 1;
      padding: 12px;
      border: 1px solid #E0E0E0;
      border-radius: 8px;
    }
    button {
      padding: 12px 24px;
      background: #000;
      color: white;
      border: none;
      border-radius: 8px;
      cursor: pointer;
    }
    .search-results {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }
    .search-item {
      padding: 12px;
      background: white;
      border-radius: 8px;
      text-decoration: none;
      color: #000;
    }
    .no-results {
      color: #666;
      text-align: center;
      padding: 24px;
    }
  `]
})
export class SearchComponent {
  query = '';
  results = signal<any[]>([]);
  searched = signal(false);

  search(): void {
    this.searched.set(true);
  }
}