import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError, of } from 'rxjs';
import { catchError, timeout } from 'rxjs/operators';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private readonly baseUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient, private authService: AuthService) {}

  get<T>(url: string, params?: Record<string, any>): Observable<any> {
    return this.request('GET', url, params);
  }

  getOne<T>(url: string): Observable<any> {
    return this.request('GET', url);
  }

  post<T>(url: string, body: any): Observable<any> {
    return this.request('POST', url, undefined, body);
  }

  put<T>(url: string, body: any): Observable<any> {
    return this.request('PUT', url, undefined, body);
  }

  delete<T>(url: string): Observable<any> {
    return this.request('DELETE', url);
  }

  private request(method: string, url: string, params?: Record<string, any>, body?: any): Observable<any> {
    const fullUrl = `${this.baseUrl}${url}`;
    const token = this.authService.getAccessToken();
    const headers: Record<string, string> = {};
    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }

    return this.http.request(method, fullUrl, {
      body,
      params,
      headers,
      observe: 'body',
      responseType: 'json'
    }).pipe(
      timeout(10000),
      catchError((error) => {
        if (error.status === 0 || error.code === 'ERR_NETWORK' || error.name === 'TimeoutError') {
          this.authService.connectionError.set('Error de conexión: No se puede conectar al servidor');
        }
        return throwError(() => error);
      })
    );
  }
}