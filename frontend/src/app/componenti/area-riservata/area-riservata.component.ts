import { Component, OnInit } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';
import { UtenteService } from 'src/app/servizi/utente.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-area-riservata',
  templateUrl: './area-riservata.component.html',
  styleUrls: ['./area-riservata.component.css']
})
export class AreaRiservataComponent implements OnInit {

  userData: any;

  constructor(
    private cookieService: CookieService,
    private router: Router,
    private utenteService: UtenteService,
  ) { }

  ngOnInit(): void {
    const userCookieValue = this.cookieService.get('user');

    // Controlla se il cookie "user" contiene dati
    if (userCookieValue) {
      this.userData = JSON.parse(userCookieValue);
    } else {
      console.error('Il cookie "user" non contiene dati.');
    }
  }

  clickedMieiViaggi() {
    this.router.navigate(['/tabellaPrenotazioni']);
  }

  clickedSuggeriti() {
    this.router.navigate(['/']);
  }

  clickedQuestionario() {
    this.router.navigate(['/questionario']); // inserire path della pagina dedicata al questionario
  }

  clickedGenera() {
    this.router.navigate(['/itinerarioAutomatico']); // inserire path della pagina dedicata alla generazione dell'itinerario automatico
  }

  clickedLeMieAttivita() {
    this.router.navigate(['/mieAttivita']); // inserire path della pagina con la lista di attività di un gestore
  }

  clickedListaSegnalazioni() {
    this.router.navigate(['/listaSegnalazioni'])// inserire path per visualizzare la lista delle segnalazioni
  }

  logout() {
    this.utenteService.logout();
    this.router.navigate(['/']);
  }

}
