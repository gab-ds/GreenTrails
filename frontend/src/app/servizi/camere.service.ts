import { CookieService } from 'ngx-cookie-service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';

@Injectable({
  providedIn: 'root'
})
export class CamereService {

  private baseUrl= `${API_BASE_URL}/api/camere`;

  constructor(private http: HttpClient, private cookieService: CookieService) { }

  inserimentoCamere(idAlloggio: any, tipoCamera: string, disponibilita: any,
    descrizione: string, capienza: any, prezzo: any): Observable<any> {

      const params = new HttpParams()
      .set('idAlloggio', idAlloggio)
      .set('tipoCamera', tipoCamera)
      .set('disponibilita', disponibilita)
      .set('descrizione', descrizione)
      .set('capienza', capienza)
      .set('prezzo', prezzo)

      const headers = new HttpHeaders({
        Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
      });

    return this.http.post<any>(`${this.baseUrl}`, params, {headers});
  }

  visualizzaCamerePerAlloggio(idAlloggio: number): Observable<any> {

    return this.http.get(`${this.baseUrl}/perAlloggio/${idAlloggio}`)
  }

  cancellaCamera(id: number): Observable<any> {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });

    return this.http.delete<any>(`${this.baseUrl}/${id}`, {headers});
  }

  visualizzaCamera(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }

  getCamereDisponibili(id: number): Observable<any>{

    return this.http.get<any[]>(`${this.baseUrl}/perAlloggio/${id}`);

  }

}
