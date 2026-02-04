import { CookieService } from 'ngx-cookie-service';
import { Observable, catchError } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { API_BASE_URL } from '../config/api-config';

@Injectable({
  providedIn: 'root'
})
export class PrenotazioniAlloggiService {

  private baseUrl = `${API_BASE_URL}/api/prenotazioni-alloggio`

  constructor(private http: HttpClient, private cookie: CookieService) { }

  getPrenotazioniAlloggioVisitatore(): Observable<any[]> {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookie.get('credenziali').replace(/"/g, '')
    });
    return this.http.get<any[]>(`${this.baseUrl}`, {headers});
  }

  deletePrenotazioneAlloggio(idPrenotazione: number){
    const headers = new HttpHeaders ({
      Authorization: 'Basic ' + this.cookie.get('credenziali').replace(/"/g, '')
    })
    const url = `${this.baseUrl}/${idPrenotazione}`;
    return this.http.delete<void>(url, {headers});
  }
}
