import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';
import { API_BASE_URL } from '../config/api-config';

@Injectable({
  providedIn: 'root',
})
export class UploadService {
  private baseUrl = `${API_BASE_URL}/api/file`;

  constructor(private http: HttpClient, private cookieService: CookieService) { }

  elencaFileCaricati(media: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${media}`);
  }

  serviFile(media: string, filename: string): Observable<any> {
    return this.http.get(`${this.baseUrl}/${media}/${filename}`, { responseType: 'blob' });
  }
}
