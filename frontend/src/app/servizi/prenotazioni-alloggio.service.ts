import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { CookieService } from 'ngx-cookie-service';
import { Observable } from 'rxjs';
import { PrenotazioniComponent } from '../componenti/pagina-attivita/prenotazioni/prenotazioni.component';
import { API_BASE_URL } from '../config/api-config';

@Injectable({
  providedIn: 'root'
})
export class PrenotazioniAlloggioService {

  constructor(private dialog: MatDialog, private http: HttpClient, private cookieService: CookieService) { }

  private baseUrl = `${API_BASE_URL}/api/prenotazioni-alloggio`;

  apriDialogAlloggio() {
    const dialogRef =

      this.dialog.open(PrenotazioniComponent, { width: '60%' })


    dialogRef.afterClosed().subscribe((risultato) => {
      console.log(`Dialog chiuso con risultato: ${risultato}`);
    });
  }

  prenotazioneAlloggio(idItinerario: number, idCamera: number, numAdulti: number, numBambini: number, numCamere: number, dataInizio: any, dataFine: any): Observable<any> {

    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });
    const params = new HttpParams()
      .set('idItinerario', idItinerario)
      .set('idCamera', idCamera)
      .set('numAdulti', numAdulti)
      .set('numBambini', numBambini)
      .set('numCamere', numCamere)
      .set('dataInizio', dataInizio)
      .set('dataFine', dataFine);

    return this.http.post<any>(`${this.baseUrl}`, params, { headers });
  }

  getPrenotazioneAlloggio(id: number): Observable<any> {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });
    return this.http.get<any>(`${this.baseUrl}/api/prenotazioni-alloggio/${id}`, { headers });

  }

  verificaDisponibilitaAlloggio(idCamera: number, dataInizio: any, dataFine: any): Observable<any> {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });

    const params = new HttpParams()
      .set('idCamera', idCamera)
      .set('dataInizio', dataInizio)
      .set('dataFine', dataFine);


    return this.http.get<any>(`${this.baseUrl}/perCamera/${idCamera}/disponibilita`, { params, headers });

  }

  deletePrenotazioneAlloggio(idPrenotazione: number) {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    })
    const url = `${this.baseUrl}/${idPrenotazione}`;
    return this.http.delete<any>(url, { headers });
  }

  confermaPrenotazioneAlloggio(id: number, numAdulti: number, numBambini: number, dataInizio: string, dataFine: string, numCamere: number): Observable<any> {
    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    })

    let params = new HttpParams()
      .set('numAdulti', numAdulti)
      .set('numBambini', numBambini)
      .set('dataInizio', dataInizio)
      .set('dataFine', dataFine)
      .set('numCamere', numCamere)

    return this.http.post<any>(`${this.baseUrl}/${id}`, params, {headers})
  }
}
