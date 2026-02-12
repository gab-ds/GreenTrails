import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { Observable, catchError, of, tap } from 'rxjs';
import { API_BASE_URL } from '../config/api-config';

@Injectable({
  providedIn: 'root'
})
export class UtenteService {
private isLogged: boolean = false;

private url = `${API_BASE_URL}/api/utenti`;

constructor(private http: HttpClient, private cookieService: CookieService) {}

registerUser(isGestore: boolean, dati: any, HttpHeaders = { }): Observable<any> {
  const urlWithParams = `${this.url}?isGestore=${isGestore}`;

  return this.http.put(urlWithParams, dati);
}

login(email: String, password: String ): Observable<any> {

  const base64credential = btoa(email + ":" + password);
  const headers = ({Authorization: 'Basic ' + base64credential} );


  return this.http.get<any>(`${this.url}`, { headers }).pipe(
    tap((response) => {
      this.isLogged = true;
      console.log('Login successful:', response);

      // Save user data in a cookie
      this.cookieService.set('user', JSON.stringify(response.data));
      this.cookieService.set('credenziali', JSON.stringify(base64credential));
      this.cookieService.set('userId', (response.data.id));
      this.cookieService.set('email', (response.data.email));
      this.cookieService.set('password', (response.data.password));
      this.cookieService.set('ruolo', (response.data.authorities[0].authority))
    }),
    catchError((error) => {
      console.error('Error during login:', error);
      throw error;
    })
  );
}

logout(): Observable<any> {
  this.isLogged = false;

  const allCookies = this.cookieService.getAll();

    for (const cookieName in allCookies) {
      if (allCookies.hasOwnProperty(cookieName)) {
        this.cookieService.delete(cookieName);
      }
    }

    return of({ success: true });


}

isLoggedInUser(): boolean {
  return this.isLogged;
}

invioQuestionario(
  viaggioPreferito: any,
  alloggioPreferito:any,
  attivitaPreferita:any,
  preferenzaAlimentare:any,
  animaleDomestico:any,
  budgetPreferito:any,
  souvenir:any,
  stagioniPreferite:any
   ): Observable<any> {

    const headers = new HttpHeaders({
      Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
    });


    const params = new HttpParams()
    .set('viaggioPreferito', viaggioPreferito)
    .set('alloggioPreferito', alloggioPreferito)
    .set('attivitaPreferita', attivitaPreferita)
    .set('preferenzaAlimentare', preferenzaAlimentare)
    .set('animaleDomestico' , animaleDomestico)
    .set('budgetPreferito', budgetPreferito)
    .set('souvenir', souvenir)
    .set('stagioniPreferite', stagioniPreferite)

    return this.http.post<any>(`${this.url}/questionario`,params, {headers});
   }

getPreferenze():Observable<any> {

  const headers = new HttpHeaders({
    Authorization: 'Basic ' + this.cookieService.get('credenziali').replace(/"/g, '')
  });
  return this.http.get<any>(`${this.url}/preferenze`, {headers});

}

}
