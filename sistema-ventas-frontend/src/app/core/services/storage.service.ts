import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class StorageService {
  private readonly prefix = 'sv_';

  set(key: string, value: any): void {
    localStorage.setItem(`${this.prefix}${key}`, JSON.stringify(value));
  }

  get<T>(key: string): T | null {
    const item = localStorage.getItem(`${this.prefix}${key}`);
    if (!item) return null;
    try {
      return JSON.parse(item) as T;
    } catch {
      return null;
    }
  }

  remove(key: string): void {
    localStorage.removeItem(`${this.prefix}${key}`);
  }

  clear(): void {
    Object.keys(localStorage)
      .filter((key) => key.startsWith(this.prefix))
      .forEach((key) => localStorage.removeItem(key));
  }

  getCart(): any[] {
    return this.get<any[]>('cart') || [];
  }

  setCart(items: any[]): void {
    this.set('cart', items);
  }

  addToCart(item: any): void {
    const cart = this.getCart();
    cart.push(item);
    this.setCart(cart);
  }

  removeFromCart(index: number): void {
    const cart = this.getCart();
    cart.splice(index, 1);
    this.setCart(cart);
  }

  clearCart(): void {
    this.remove('cart');
  }
}
