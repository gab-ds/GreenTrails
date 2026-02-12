import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { CookieService } from 'ngx-cookie-service';
import { Observable } from 'rxjs';
import { PrenotazioniAttivitaComponent } from '../componenti/pagina-attivita/prenotazioni-attivita/prenotazioni-attivita.component';
import { API_BASE_URL } from '../config/api-config';

@Injectable({
  providedIn: 'root'
})
export class PrenotazioniAttivitaService {

  constructor(private dialog: MatDialog, private http: HttpClient, private cookieService: CookieService) { }

  private baseUrl = `${API_BASE_URL}/api/prenotazioni-attivita-turistica`;

  apriDialogAttivita() {
    const dialogRef =

      this.dialog.open(PrenotazioniAttivitaComponent, { width: '60%' })


    dialogRef.afterClosed().subscribe((risultato) => {
      console.log(`Dialog chiuso con risultato: ${risultato}`);
    });
  }

  verificaDisponibilitaAttivita(idAttivita: number,dataInizio: any):Observable<any> {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });
    const params = new HttpParams()
    .set('idAttivita', idAttivita)
    .set('dataInizio', dataInizio)


    return this.http.get(`${this.baseUrl}/perAttivita/${idAttivita}/disponibilita`,  { params, headers });

  }

  prenotazioneAttivita(idItinerario: number, idAttivita: number,numAdulti: number, numBambini: number, dataInizio: string, dataFine: string ): Observable<any>{

    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });

    const params = new HttpParams()
      .set('idItinerario', idItinerario)
      .set('idAttivita', idAttivita)
      .set('numAdulti', numAdulti)
      .set('numBambini', numBambini)
      .set('dataInizio', dataInizio)
      .set('dataFine', dataFine);

    return this.http.post<any>(`${this.baseUrl}`, params, { headers });
  }


  deletePrenotazioneAttivitaTuristica(idPrenotazione: number) {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });
    const url = `${this.baseUrl}/${idPrenotazione}`;
    return this.http.delete<any>(url, { headers });
  }

  confermaPrenotazioneAttivitaTuristica(id: number, numAdulti: number, numBambini: number, dataInizio: any, dataFine: any): Observable<any> {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });

    let params = new HttpParams()
      .set('numAdulti', numAdulti)
      .set('numBambini', numBambini)
      .set('dataInizio', dataInizio)
      .set('dataFine', dataFine)

      return this.http.post<any>(`${this.baseUrl}/${id}`, params, {headers});
  }

}
