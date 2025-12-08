import { ItinerariService } from './../../../../servizi/itinerari.service';
import { PrenotazioniAttivitaService } from './../../../../servizi/prenotazioni-attivita.service';
import { AttivitaService } from 'src/app/servizi/attivita.service';
import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { PrenotazioniAlloggioService } from 'src/app/servizi/prenotazioni-alloggio.service';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-politiche-ecosostenibili-attivita',
  templateUrl: './politiche-ecosostenibili-attivita.component.html',
  styleUrls: ['./politiche-ecosostenibili-attivita.component.css']
})
export class PoliticheEcosostenibiliAttivitaComponent implements OnInit {
  id: number = 0;
  isAlloggio: boolean = false;
  valoriEcosostenibilita: string[] = [];

  isVisitatore: boolean;

  constructor(private attivitaService: AttivitaService,
     private route: ActivatedRoute, 
     private prenotazioniAlloggioService : PrenotazioniAlloggioService,
     private prenotazioniAttivitaService : PrenotazioniAttivitaService,
     private itinerariService: ItinerariService,
     private cookieService: CookieService,
    public dialog: MatDialog ) {
      this.isVisitatore = cookieService.get('ruolo') === 'ROLE_VISITATORE'
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      let idAttivita = parseInt(params.get('id')!);
      this.id = idAttivita;
      this.itinerariService.changeId(this.id); 
      console.log(this.id)// Invia l'ID al servizio
      this.visualizzaDettagliAttivita();
    })
  }

  visualizzaDettagliAttivita(): void {
    this.attivitaService.visualizzaAttivita(this.id).subscribe((attivita) => {
      let valoriEcosostenibilitaTrue: string[] = Object.entries(attivita.data.valoriEcosostenibilita)
        .filter(([nomePolitica, valore]) => valore === true)
        .map(([nomePolitica, valore]) => this.convertCamelCaseToReadable(nomePolitica));
        this.isAlloggio = attivita.data?.alloggio;

      this.valoriEcosostenibilita = attivita.data.valoriEcosostenibilita;
      
      this.valoriEcosostenibilita = valoriEcosostenibilitaTrue;
    }, (error) => {
      console.error(error);
    })
  }

  convertCamelCaseToReadable(camelCase: string): string {
    let result = camelCase.replace(/([A-Z])/g, ' $1');
    result = result.replace('C O2', 'CO2');
    return result.charAt(0).toUpperCase() + result.slice(1);
}

openDialog() {
  console.log('Attivita: ', this.isAlloggio)
     if (this.isAlloggio === true) {
      this.prenotazioniAlloggioService.apriDialogAlloggio();
} else {
      this.prenotazioniAttivitaService.apriDialogAttivita();
    }
  }

}
