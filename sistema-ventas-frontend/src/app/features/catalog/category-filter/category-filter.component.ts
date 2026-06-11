import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from '../../../core/services/api.service';
import { Categoria } from '../../../core/models';

@Component({
  selector: 'app-category-filter',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="category-filter">
      <select class="filter-select" (change)="onCategoryChange($event)">
        <option [value]="null">Todas las categorías</option>
        @for (category of categories(); track category.id) {
          <option [value]="category.id">{{ category.nombre }}</option>
        }
      </select>
    </div>
  `,
  styles: [`
    .category-filter {
      margin-bottom: 16px;
    }
    .filter-select {
      padding: 10px 12px;
      border: 1px solid #E0E0E0;
      border-radius: 8px;
      font-size: 14px;
      background: white;
    }
  `]
})
export class CategoryFilterComponent {
  categories = signal<Categoria[]>([]);
  categoryChange = signal<number | null>(null);

  constructor(private apiService: ApiService) {}

  onCategoryChange(event: Event): void {
    const select = event.target as HTMLSelectElement;
    this.categoryChange.set(+(select.value) || null);
  }
}