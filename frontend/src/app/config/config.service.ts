import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';

export interface AppConfig {
  apiBaseUrl: string;
}

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private config: AppConfig | null = null;

  constructor(private http: HttpClient) {}

  loadConfig(): Observable<AppConfig> {
    return this.http.get<AppConfig>('/assets/config.json').pipe(
      tap(config => {
        this.config = config;
      }),
      catchError(() => {
        // Fallback to default config if file not found
        this.config = { apiBaseUrl: 'http://localhost:8080' };
        return of(this.config);
      })
    );
  }

  getConfig(): AppConfig {
    if (!this.config) {
      throw new Error('Config not loaded. Call loadConfig() first.');
    }
    return this.config;
  }

  get apiBaseUrl(): string {
    return this.getConfig().apiBaseUrl;
  }
}
