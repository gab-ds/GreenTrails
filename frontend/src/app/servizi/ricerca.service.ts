import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';

@Injectable({
  providedIn: 'root'
})
export class RicercaService {
  baseUrl: string = `${API_BASE_URL}/api/ricerca`;

  constructor(private http: HttpClient) { }

  cercaPerPosizione(latitudine: any, longitudine: any, raggio: any): Observable<any> {
    const params = new HttpParams()
      .set('latitudine', latitudine)
      .set('longitudine', longitudine)
      .set('raggio', raggio)


    return this.http.post(`${this.baseUrl}/perPosizione`, params);
  }
}
