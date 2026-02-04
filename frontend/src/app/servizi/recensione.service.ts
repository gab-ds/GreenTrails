import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { CookieService } from 'ngx-cookie-service';
import { API_BASE_URL } from '../config/api-config';

@Injectable({
  providedIn: 'root'
})
export class RecensioneService {

  private baseUrl = `${API_BASE_URL}/api/recensioni`;

  constructor(private http: HttpClient, private cookieService: CookieService) { }

  visualizzaRecensioniPerAttivita(idAttivita: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/perAttivita/${idAttivita}`);
  }

  creaRecensione(idAttivita: number, valutazioneStelleEsperienza: number, descrizione: string, idValori: number, immagine: File): Observable<any> {

    const formData: FormData = new FormData();
    formData.append('idAttivita', idAttivita.toString())
    formData.append('valutazioneStelleEsperienza', valutazioneStelleEsperienza.toString())
    formData.append('descrizione', descrizione)
    formData.append('idValori', idValori.toString())

    if (immagine != null) {
      formData.append('immagine', immagine, immagine.name);
      console.log('name: ', immagine.name);
      console.log('size: ', immagine.size);
      console.log('type: ', immagine.type);
    }

    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });

    console.log("idAttivita: " + idAttivita);
    console.log("valutazioneStelleEsperienza: " + valutazioneStelleEsperienza);
    console.log("descrizione: " + descrizione);
    console.log("idValori: " + idValori);
    return this.http.post<any>(`${this.baseUrl}`, formData, { headers });
  }


  visualizzaRecensione(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

}
