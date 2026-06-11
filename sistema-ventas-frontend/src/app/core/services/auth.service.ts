import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, throwError, of } from 'rxjs';
import { map, tap, catchError, retry, timeout, finalize } from 'rxjs/operators';
import { AuthResponse, LoginRequest, RefreshTokenRequest, RegisterRequest } from '../models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = 'http://localhost:8080/api/auth';
  private readonly tokenKey = 'sv_access_token';
  private readonly refreshKey = 'sv_refresh_token';
  private readonly userKey = 'sv_current_user';

  currentUser = signal<any | null>(null);
  isAuthenticated = signal(false);
  connectionError = signal<string | null>(null);
  loading = signal(false);

  constructor(private http: HttpClient) {
    this.loadSession();
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    this.loading.set(true);
    this.connectionError.set(null);
    return this.http.post<any>(`${this.apiUrl}/login`, request).pipe(
      timeout(10000),
      map((response: any) => response.data),
      tap((data: AuthResponse) => this.setSession(data)),
      catchError((error) => {
        this.handleError(error, 'No se pudo conectar al servidor. Verifique que el backend esté corriendo.');
        return throwError(() => error);
      }),
      finalize(() => this.loading.set(false))
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    this.loading.set(true);
    this.connectionError.set(null);
    return this.http.post<any>(`${this.apiUrl}/register`, request).pipe(
      timeout(10000),
      map((response: any) => response.data),
      tap((data: AuthResponse) => this.setSession(data)),
      catchError((error) => {
        this.handleError(error, 'No se pudo conectar al servidor. Verifique que el backend esté corriendo.');
        return throwError(() => error);
      }),
      finalize(() => this.loading.set(false))
    );
  }

  refreshToken(request: RefreshTokenRequest): Observable<AuthResponse> {
    return this.http.post<any>(`${this.apiUrl}/refresh`, request).pipe(
      timeout(10000),
      map((response: any) => response.data),
      tap((data: AuthResponse) => this.setSession(data)),
      catchError((error) => {
        this.logout();
        return throwError(() => error);
      })
    );
  }

  logout(): void {
    const refreshToken = localStorage.getItem(this.refreshKey);
    if (refreshToken) {
      this.http.post<any>(`${this.apiUrl}/logout`, { refreshToken }).subscribe();
    }
    this.clearSession();
  }

  getAccessToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.refreshKey);
  }

  hasRole(role: string): boolean {
    const user = this.currentUser();
    return user?.roles?.includes(role) ?? false;
  }

  hasAnyRole(roles: string[]): boolean {
    return roles.some((role) => this.hasRole(role));
  }

  hasAllRoles(roles: string[]): boolean {
    return roles.every((role) => this.hasRole(role));
  }

  private setSession(authResponse: AuthResponse): void {
    localStorage.setItem(this.tokenKey, authResponse.accessToken);
    localStorage.setItem(this.refreshKey, authResponse.refreshToken);
    localStorage.setItem(this.userKey, JSON.stringify(authResponse.usuario));
    this.currentUser.set(authResponse.usuario);
    this.isAuthenticated.set(true);
  }

  private loadSession(): void {
    const token = localStorage.getItem(this.tokenKey);
    const userStr = localStorage.getItem(this.userKey);
    if (token && userStr) {
      try {
        const user = JSON.parse(userStr);
        this.currentUser.set(user);
        this.isAuthenticated.set(true);
      } catch {
        this.clearSession();
      }
    }
  }

  private clearSession(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.refreshKey);
    localStorage.removeItem(this.userKey);
    this.currentUser.set(null);
    this.isAuthenticated.set(false);
  }

  private handleError(error: any, defaultMessage: string): void {
    if (error.status === 0 || error.code === 'ERR_NETWORK') {
      this.connectionError.set('Error de conexión: No se puede conectar al servidor');
    } else if (error.error?.message) {
      this.connectionError.set(error.error.message);
    } else {
      this.connectionError.set(defaultMessage);
    }
  }
}