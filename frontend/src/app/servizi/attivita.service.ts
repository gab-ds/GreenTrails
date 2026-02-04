import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';
import { CookieService } from 'ngx-cookie-service';
import { InserimentoAttivitaComponent } from '../componenti/inserimento-attivita/inserimento-attivita.component';
import { MatDialog } from '@angular/material/dialog';
import { PopupModificaComponent } from '../componenti/gestione-attivita/popup-modifica-attivita-turistica/popup-modifica-attivita-turistica.component';
@Injectable({
  providedIn: 'root'
})
export class AttivitaService {

  private baseUrl = `${API_BASE_URL}/api/attivita`;

  constructor(private http: HttpClient, private cookieService: CookieService, private dialog: MatDialog) { }

  apriDialog() {
    const dialogRef =
      this.dialog.open(InserimentoAttivitaComponent, { width: '100%' })
  }

  visualizzaAttivita(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }

  visualizzaAttivitaPerPrezzo(limite: number): Observable<any> {
    const pararms = new HttpParams()
      .set('limite', limite.toString());

    console.log('Limite:', limite)

    return this.http.get<any>(`${this.baseUrl}/perPrezzo`);
  }


  getAlloggi(limite: number): Observable<any> {
    const pararms = new HttpParams()
      .set('limite', limite.toString());
    console.log('Limite:', limite)
    return this.http.get<any>(`${this.baseUrl}/alloggi`);
  }


  getAttivitaTuristiche(limite: number): Observable<any> {
    const pararms = new HttpParams()
      .set('limite', limite.toString());
    console.log('Limite:', limite)
    return this.http.get<any>(`${this.baseUrl}/attivitaTuristiche`);;
  }

  findAll(): Observable<any> {
    return this.http.get(`${this.baseUrl}/all`)
  }

  visualizzaAttivitaPerGestore(): Observable<any> {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });

    return this.http.get<any>(`${this.baseUrl}/perGestore`, { headers });
  }

  cancellaAttivita(id: number): Observable<any> {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });

    return this.http.delete(`${this.baseUrl}/${id}`, { headers });
  }

  inserimentoAttivita(dati: any): Observable<any> {

    let params = new HttpParams();
    Object.keys(dati).forEach(key => {
      params = params.set(key, dati[key]);
    });
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });
    return this.http.post<any>(`${this.baseUrl}`, dati, { headers, params });

  }

  modificaAttivita(id: number, dati: any): Observable<any> {

    let params = new HttpParams();
    Object.keys(dati).forEach(key => {
      params = params.set(key, dati[key]);
      console.log("KEY: ", key)
      console.log("DATI[KEY]", dati[key])
    });
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });
    return this.http.post<any>(`${this.baseUrl}/${id}`, dati, { headers });
  }


}
