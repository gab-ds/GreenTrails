import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Sort } from '@angular/material/sort';
import { AttivitaService } from 'src/app/servizi/attivita.service';
import { PopupEliminazioneComponent } from './popup-eliminazione-attivita/popup-eliminazione-attivita.component';
import { PopupModificaComponent } from './popup-modifica-attivita-turistica/popup-modifica-attivita-turistica.component';
import { PopupModificaDatiAlloggioComponent } from './popup-modifica-dati-alloggio/popup-modifica-dati-alloggio.component';
import { CookieService } from 'ngx-cookie-service';

export interface Attivita {
  numero: number;
  nome: string;
  categoria: string;
}

@Component({
  selector: 'app-gestione-attivita',
  templateUrl: './gestione-attivita.component.html',
  styleUrls: ['./gestione-attivita.component.css']
})
export class GestioneAttivitaComponent implements OnInit {

  listaAttivita: Attivita[] = [
    { numero: 0, nome: '', categoria: '' },
  ];

  sortedData: Attivita[];
  idAttivita: number = 0;
  isGestore: boolean = false;

  filterTerm!: string;

  constructor(private attivitaService: AttivitaService, private dialog: MatDialog, private cookieService: CookieService) {
    this.sortedData = this.listaAttivita.slice();

    this.isGestore = cookieService.get('ruolo') === "ROLE_GESTORE_ATTIVITA";
  }

  ngOnInit(): void {
    this.visualizzaAttivitaPerGestore();
  }

  sortData(sort: Sort) {
    const data = this.listaAttivita.slice();
    if (!sort.active || sort.direction === '') {
      this.sortedData = data;
      return;
    }

    this.sortedData = data.sort((a, b) => {
      const isAsc = sort.direction === 'asc';
      switch (sort.active) {
        case 'numero':
          return this.compare(a.numero, b.numero, isAsc);
        case 'nome':
          return this.compare(a.nome, b.nome, isAsc);
        case 'categoria':
          return this.compare(a.categoria, b.categoria, isAsc);
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

  visualizzaAttivitaPerGestore() {
    this.attivitaService.visualizzaAttivitaPerGestore().subscribe((risposta) => {
      console.log("Attività per gestore: ", risposta);
      this.listaAttivita = risposta.data
        .filter((attivita: any) => !attivita.eliminata) // Filtra solo le attività non eliminate
        .map((attivita: any, index: any) => ({
          numero: attivita.id,
          nome: attivita.nome,
          categoria: attivita.alloggio ? 'Alloggio' : 'Attività Turistica'
        }));
      this.sortedData = this.listaAttivita.slice();
    });
  }

  createAttivita() {
    this.attivitaService.apriDialog();
  }

  edit(id: number) {
    this.attivitaService.visualizzaAttivita(id).subscribe((risposta) => {
      console.log("Attivita con id: " + id, risposta);
  
      let dialogRef: any;
  
      if (!risposta.data.alloggio) {
        dialogRef = this.dialog.open(PopupModificaComponent, {
          data: {
            id: id,
            cap: risposta.data.cap,
            categoriaAlloggio: risposta.data.categoriaAlloggio,
            categoriaAttivitaTuristica: risposta.data.categoriaAttivitaTuristica,
            categorie: risposta.data.categorie,
            citta: risposta.data.citta,
            latitudine: risposta.data.coordinate.x,
            longitudine: risposta.data.coordinate.y,
            descrizioneBreve: risposta.data.descrizioneBreve,
            descrizioneLunga: risposta.data.descrizioneLunga,
            disponibilita: risposta.data.disponibilita,
            gestore: risposta.data.gestore,
            indirizzo: risposta.data.indirizzo,
            media: risposta.data.media,
            nome: risposta.data.nome,
            prezzo: risposta.data.prezzo,
            provincia: risposta.data.provincia,
            valori: risposta.data.valoriEcosostenibilita.id,
            eliminata: risposta.data.eliminata,
            tipo: risposta.data.alloggio,
          },
          disableClose: true
        });
  
        dialogRef.afterClosed().subscribe((result: any) => {
        }, (error: any) => {
          console.log(error);
        });
      } else {
        dialogRef = this.dialog.open(PopupModificaDatiAlloggioComponent, {
          data: {
            id: id,
            cap: risposta.data.cap,
            categoriaAlloggio: risposta.data.categoriaAlloggio,
            categoriaAttivitaTuristica: risposta.data.categoriaAttivitaTuristica,
            categorie: risposta.data.categorie,
            citta: risposta.data.citta,
            latitudine: risposta.data.coordinate.x,
            longitudine: risposta.data.coordinate.y,
            descrizioneBreve: risposta.data.descrizioneBreve,
            descrizioneLunga: risposta.data.descrizioneLunga,
            disponibilita: risposta.data.disponibilita,
            gestore: risposta.data.gestore,
            indirizzo: risposta.data.indirizzo,
            media: risposta.data.media,
            nome: risposta.data.nome,
            prezzo: risposta.data.prezzo,
            provincia: risposta.data.provincia,
            valori: risposta.data.valoriEcosostenibilita.id,
            eliminata: risposta.data.eliminata,
            tipo: risposta.data.alloggio,
          },
          disableClose: true
        });
  
        dialogRef.afterClosed().subscribe((result: any)  => {
        }, (error: any) => {
          console.log(error);
        });
      }
    });
  }
  

  delete(id: number): void {
    this.attivitaService.visualizzaAttivita(id).subscribe((risposta) => {
      console.log("Attivita con id: " + id, risposta);

      const dialogRef = this.dialog.open(PopupEliminazioneComponent, {
        data: {
          message: 'Sei sicuro di voler eliminare l\'\attività?',
          id: id,
          cap: risposta.data.cap,
          categoriaAlloggio: risposta.data.categoriaAlloggio,
          categoriaAttivitaTuristica: risposta.data.categoriaAttivitaTuristica,
          categorie: risposta.data.categorie,
          citta: risposta.data.citta,
          latitudine: risposta.data.coordinate.x,
          longitudine: risposta.data.coordinate.y,
          descrizioneBreve: risposta.data.descrizioneBreve,
          descrizioneLunga: risposta.data.descrizioneLunga,
          disponibilita: risposta.data.disponibilita,
          gestore: risposta.data.gestore,
          indirizzo: risposta.data.indirizzo,
          media: risposta.data.media,
          nome: risposta.data.nome,
          prezzo: risposta.data.prezzo,
          provincia: risposta.data.provincia,
          valori: risposta.data.valoriEcosostenibilita.id,
          eliminata: risposta.data.eliminata,
        }
      });

      dialogRef.afterClosed().subscribe(result => {
        if (result) {
          this.attivitaService.cancellaAttivita(id).subscribe((risposta) => {
            console.log("Eliminazione attività: ", risposta);
          });
        }
      }, (error) => {
        console.log(error);
      });
    });
  }
}
