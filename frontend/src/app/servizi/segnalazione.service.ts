import { CookieService } from 'ngx-cookie-service';
import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';

@Injectable({
  providedIn: 'root',
})
export class SegnalazioneService {

  private Url = `${API_BASE_URL}/api/segnalazioni`;

  constructor(private http : HttpClient, private cookie: CookieService) { }

  mandaDatiSegnalazione(descrizione: string, immagine: FileList, idAttivita: number): Observable<any>{
    const formData: FormData = new FormData();

    formData.append('descrizione', descrizione);
    if(immagine != null)
    {Array.from(immagine).forEach((file, index) => {
      formData.append('immagine', immagine[index], immagine[index].name);
    })}
    formData.append('idAttivita', idAttivita.toString());

    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookie.get('credenziali').replace(/"/g, '')
    });
    return this.http.post<any>(`${this.Url}`, formData, {headers});
  }

  recuperoSegnalazioni(isForRecensione: any): Observable<any>{

    const params = new HttpParams()
    .set('isForRecensione', isForRecensione)

    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookie.get('credenziali').replace(/"/g, '')
    });
    return this.http.get<any>(`${this.Url}`, {headers, params});
  }


}
