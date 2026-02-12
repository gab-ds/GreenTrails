import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';
import { API_BASE_URL } from '../config/api-config';

@Injectable({
  providedIn: 'root'
})
export class PrenotazioniAttivitaTuristicheService {

  private baseUrl = `${API_BASE_URL}/api/prenotazioni-attivita-turistica`

  constructor(private http: HttpClient, private cookie: CookieService) { }

  getPrenotazioniAttivitaTuristicaVisitatore(): Observable<any> {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookie.get('credenziali').replace(/"/g, '')
    });
    return this.http.get<any>(`${this.baseUrl}`, {headers});
  }

  deletePrenotazioneAttivitaTuristica (idPrenotazione: number) {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookie.get('credenziali').replace(/"/g, '')
    });
    const url = `${this.baseUrl}/${idPrenotazione}`;
    return this.http.delete<void>(url, {headers});
  }
}
