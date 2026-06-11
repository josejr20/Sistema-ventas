import { Component, input, output } from '@angular/core';
import { CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-price-display',
  standalone: true,
  imports: [CurrencyPipe],
  template: `
    <span class="price" [class.has-discount]="originalPrice() > 0 && originalPrice() > price()">
      @if (originalPrice() > 0 && originalPrice() > price()) {
        <span class="price-original">{{ originalPrice() | currency:currency():'':'':'1.2-2' }}</span>
      }
      <span class="price-current">{{ price() | currency:currency():'':'':'1.2-2' }}</span>
    </span>
  `,
  styles: [`
    .price {
      display: inline-flex;
      align-items: center;
      gap: 8px;
      font-weight: 700;
      font-size: 18px;
      color: #000;
    }
    .price-original {
      font-size: 14px;
      color: #999;
      text-decoration: line-through;
      font-weight: 400;
    }
    .price-current {
      color: #E8344E;
    }
  `]
})
export class PriceDisplayComponent {
  price = input.required<number>();
  originalPrice = input<number>(0);
  currency = input<string>('PEN');
}
