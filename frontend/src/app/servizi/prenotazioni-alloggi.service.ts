import { CookieService } from 'ngx-cookie-service';
import { Observable, catchError } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class PrenotazioniAlloggiService {

  private baseUrl = 'http://localhost:8080/api/prenotazioni-alloggio' 

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
