import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';

@Injectable({
  providedIn: 'root'
})
export class CategoriaService {

  private baseUrl= `${API_BASE_URL}/api/categorie`;

  constructor(private http: HttpClient, private cookieService: CookieService) { }


  aggiungiCategoria(idAttivita: any, id: any): Observable<any> {

      const params = new HttpParams()
      .set('idAttivita', idAttivita)
      .set('id', id)


      const headers = new HttpHeaders({
        Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
      });

    return this.http.post<any>(`${this.baseUrl}/${id}`, params, {headers});
  }

  rimuoviCategoria(id: number, idAttivita: number): Observable<any> {
    const params = new HttpParams()
    .set('idAttivita', idAttivita)
    .set('id', id)

    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });

    const options = { params, headers };

    return this.http.delete<any>(`${this.baseUrl}/${id}`, options);
  }

  visualizzaCategoria(id: number): Observable<any> {

    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });

    return this.http.get<any>(`${this.baseUrl}/${id}`, {headers});
  }
}
