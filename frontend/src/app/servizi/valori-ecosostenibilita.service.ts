import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';

@Injectable({
  providedIn: 'root'
})
export class ValoriEcosostenibilitaService {

  baseUrl: string = `${API_BASE_URL}/api/valori`;

  constructor(private http: HttpClient, private cookieService: CookieService) { }

  creaValoriEcosostenibilitaVisitatore(politicheAntispreco: boolean, prodottiLocali: boolean,
    energiaVerde: boolean, raccoltaDifferenziata: boolean,
    limiteEmissioneCO2: boolean, contattoConNatura: boolean): Observable<any> {

    const params = new HttpParams()
      .set('politicheAntispreco', politicheAntispreco)
      .set('prodottiLocali', prodottiLocali)
      .set('energiaVerde', energiaVerde)
      .set('raccoltaDifferenziata', raccoltaDifferenziata)
      .set('limiteEmissioneCO2', limiteEmissioneCO2)
      .set('contattoConNatura', contattoConNatura);

    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });

    return this.http.post<any>(`${this.baseUrl}`, params, { headers });
  }

  modificaValoriEcosostenibilita(id: number, politicheAntispreco: boolean, prodottiLocali: boolean,
    energiaVerde: boolean, raccoltaDifferenziata: boolean,
    limiteEmissioneCO2: boolean, contattoConNatura: boolean): Observable<any> {

    const params = new HttpParams()
      .set('politicheAntispreco', politicheAntispreco)
      .set('prodottiLocali', prodottiLocali)
      .set('energiaVerde', energiaVerde)
      .set('raccoltaDifferenziata', raccoltaDifferenziata)
      .set('limiteEmissioneCO2', limiteEmissioneCO2)
      .set('contattoConNatura', contattoConNatura);

    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });

    return this.http.post<any>(`${this.baseUrl}/${id}`, params, { headers });
  }
}
