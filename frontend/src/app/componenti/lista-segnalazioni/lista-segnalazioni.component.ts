import { SegnalazioneService } from './../../servizi/segnalazione.service';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Sort } from '@angular/material/sort';
import { MatSortModule } from '@angular/material/sort';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';

export interface Segnalazione {
  numero: number;
  idUtente: string;
  idAttivita: string;
  nomeAttivita: string;
  descrizione: string;
}

@Component({
  selector: 'app-lista-segnalazioni',
  templateUrl: './lista-segnalazioni.component.html',
  styleUrls: ['./lista-segnalazioni.component.css']
})
export class ListaSegnalazioniComponent implements OnInit {

  listaSegnalazione: Segnalazione[] = [
    { numero: 0, idUtente: '',idAttivita: '', nomeAttivita: '', descrizione: '' },
  ];

  sortedData: Segnalazione[] = [];
  nomeAttivita!: string;

  constructor(private segnalazioneService: SegnalazioneService,
     private dialog: MatDialog,  private router: Router,  private cookieService: CookieService) { }

  ngOnInit(): void {
    this.visualizzaSegnalazioni();
  }

  sortData(sort: Sort) {
    const data = this.listaSegnalazione.slice();
    if (!sort.active || sort.direction === '') {
      this.sortedData = data;
      return;
    }

    this.sortedData = data.sort((a, b) => {
      const isAsc = sort.direction === 'asc';
      switch (sort.active) {
        case 'numero':
          return this.compare(a.numero, b.numero, isAsc);
        case 'idUtente':
          return this.compare(a.idUtente, b.idUtente, isAsc);
        case 'nomeAttivita':
          return this.compare(a.nomeAttivita, b.nomeAttivita, isAsc);
        case 'descrizione':
          return this.compare(a.descrizione, b.descrizione, isAsc);
        default:
          return 0;
      }
    });
  }

  compare(a: number | string, b: number | string, isAsc: boolean) {
    if (typeof a === "number" && typeof b === "number") {
      return (a < b ? -1 : 1) * (isAsc ? 1 : -1);
    } else if (typeof a === "string" && typeof b === "string") {
      return a.localeCompare(b) * (isAsc ? 1 : -1);
    } else {
      return 0;
    }
  }

  visualizzaSegnalazioni() {
    this.segnalazioneService.recuperoSegnalazioni(false).subscribe((risposta) => {
      console.log("Segnalazioni risposta:", risposta);
        this.listaSegnalazione = risposta.data.map((segnalazione: any, index: any) => ({
          numero: index + 1,
          idUtente: segnalazione.utente.email,
          idAttivita : segnalazione.attivita.id,
          nomeAttivita: segnalazione.attivita.nome,
          descrizione: segnalazione.descrizione,
        }));
        console.log("listaSegnalazione:", this.listaSegnalazione);
        this.sortedData = this.listaSegnalazione.slice();
    });
  }

  modifica(idAttivita: any){

    this.cookieService.set('idAttivita', idAttivita);
        this.router.navigate(['/modificaValoriAdmin']); 
        console.log(idAttivita)
  }
 
}
