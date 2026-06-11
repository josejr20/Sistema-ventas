import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'currency', standalone: true })
export class CurrencyPipe implements PipeTransform {
  transform(value: number | string | null | undefined, currency = 'PEN', symbol = 'S/ ', decimals = 2): string {
    if (value === null || value === undefined || isNaN(Number(value))) {
      return `${symbol}0.00`;
    }
    const num = Number(value);
    return `${symbol}${num.toFixed(decimals)}`;
  }
}
