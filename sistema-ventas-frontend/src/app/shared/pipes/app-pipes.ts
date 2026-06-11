import { Pipe, PipeTransform } from '@angular/core';

@Pipe({ name: 'currency' })
export class CurrencyPipe implements PipeTransform {
  transform(value: number | string | null | undefined, currency = 'PEN', symbol = 'S/ ', decimals = 2): string {
    if (value === null || value === undefined || isNaN(Number(value))) return `${symbol}0.00`;
    const num = Number(value);
    return `${symbol}${num.toFixed(decimals)}`;
  }
}

@Pipe({ name: 'dateFormat' })
export class DateFormatPipe implements PipeTransform {
  transform(value: string | Date | null | undefined, format = 'short'): string {
    if (!value) return '-';
    const date = typeof value === 'string' ? new Date(value) : value;
    if (isNaN(date.getTime())) return '-';
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    if (format === 'short') return `${day}/${month}/${year}`;
    if (format === 'long') {
      const months = ['Enero','Febrero','Marzo','Abril','Mayo','Junio','Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre'];
      return `${day} de ${months[date.getMonth()]} de ${year}`;
    }
    if (format === 'time') {
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      return `${day}/${month}/${year} ${hours}:${minutes}`;
    }
    return `${day}/${month}/${year}`;
  }
}

@Pipe({ name: 'truncate' })
export class TruncatePipe implements PipeTransform {
  transform(value: string | null | undefined, limit = 50, suffix = '...'): string {
    if (!value) return '';
    if (value.length <= limit) return value;
    return value.substring(0, limit).trimEnd() + suffix;
  }
}
