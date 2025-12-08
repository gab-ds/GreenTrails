import { PrenotazioniAttivitaTuristicheService } from './../../servizi/prenotazioni-attivita-turistiche.service';
import { PrenotazioniAlloggiService } from './../../servizi/prenotazioni-alloggi.service';
import { forkJoin } from 'rxjs';
import { MatTableDataSource } from '@angular/material/table';
import { Component, ElementRef, HostListener, ViewChild } from '@angular/core';
import { PopupDeleteConfermaComponent } from './popupDeleteConferma/popupDeleteConferma.component';
import { MatDialog } from '@angular/material/dialog';
import { PopupDettagliComponent } from './popupDettagli/popupDettagli.component';
import { PopupDettagliAttivitaComponent } from './popupDettagliAttivita/popupDettagliAttivita.component';


@Component({
  selector: 'app-gestione-prenotazioni-attive',
  templateUrl: './gestione-prenotazioni-attive.component.html',
  styleUrls: ['./gestione-prenotazioni-attive.component.css']
})

export class GestionePrenotazioniAttiveComponent {



  idalloggio: any;
  idAttivita: any;
  mostraSoloInCorso: boolean = false;

  alloggi: any[] = []
  attivita: any[] = []

  updatePaginatedData() {
    throw new Error('Method not implemented.');
  }

  prenotazioniAttivita: any[] = [];
  prenotazioniAlloggio: any[] = [];
  displayedColumns: string[] = ['stato', 'id', 'check-in', 'check-out', 'bambini', 'adulti', 'prezzo', 'actions'];
  dataSource: MatTableDataSource<any> = new MatTableDataSource<any>([]);
  pageSize: number = 5;


  constructor(
    private prenotazioniAlloggiService: PrenotazioniAlloggiService,
    private prenotazioniAttivitaTurService: PrenotazioniAttivitaTuristicheService,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {
    this.populateTable();
  }

  populateTable() {
    forkJoin({
      prenotazioniAlloggio: this.prenotazioniAlloggiService.getPrenotazioniAlloggioVisitatore(),
      prenotazioniAttivita: this.prenotazioniAttivitaTurService.getPrenotazioniAttivitaTuristicaVisitatore(),
    }).subscribe(
      (responses: any) => {
        const prenotazioniAlloggio = responses.prenotazioniAlloggio.data;
        const prenotazioniAttivita = responses.prenotazioniAttivita.data;
        console.log("alloggio", prenotazioniAlloggio)
        console.log("attivita", prenotazioniAttivita)

        const mergedData = [...prenotazioniAlloggio, ...prenotazioniAttivita];

        const mappedData = mergedData.map(item => ({
          id: item.id,
          stato: item.stato,
          checkIn: item.dataInizio,
          checkOut: item.dataFine,
          bambini: item.numBambini,
          adulti: item.numAdulti,
          prezzo: item.prezzo,
          tipo: item.attivitaTuristica ? 'attivita' : 'alloggio',

        }));

        this.dataSource.data = mappedData;
      },
      error => {
        console.error('Errore nel recupero delle prenotazioni:', error);
      }
    );
  }
  VisualizzaAttivita(selectedPrenotazione: any): void {
    this.prenotazioniAttivitaTurService.getPrenotazioniAttivitaTuristicaVisitatore().subscribe(
      (response: any) => {
        const prenotazioniAttivita = response.data;
        console.log("Prenotazioni Attivita:");

        const selectedAttivita = prenotazioniAttivita.find(
          (prenotazione: any) => prenotazione.id === selectedPrenotazione.id
        );

        if (selectedAttivita) {
          console.log("Selected Attività:");
          console.log("- Nome:", selectedAttivita.attivitaTuristica.nome);
          console.log("- Indirizzo:", selectedAttivita.attivitaTuristica.indirizzo);

          this.openPopup1(selectedAttivita);
        } else {
          console.error('Selected attività not found in the response.');
        }
      },
      error => {
        console.error('Errore nel recupero delle prenotazioni attività:', error);
      }
    );
  }

  openPopup1(prenotazione: any): void {
    const dialogRef = this.dialog.open(PopupDettagliAttivitaComponent, {
      data: { prenotazione },
      width: '400px',
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }


  visualizzaAlloggio(selectedPrenotazione: any): void {
    this.prenotazioniAlloggiService.getPrenotazioniAlloggioVisitatore().subscribe(
      (response: any) => {
        const prenotazioniAlloggio = response.data;
        console.log("Prenotazioni Alloggio:");

        const selectedAlloggio = prenotazioniAlloggio.find(
          (prenotazione: any) => prenotazione.id === selectedPrenotazione.id
        );

        if (selectedAlloggio) {
          console.log("Selected Alloggio:");
          console.log("- Nome Alloggio:", selectedAlloggio.camera.alloggio.nome);
          console.log("- Indirizzo Alloggio:", selectedAlloggio.camera.alloggio.indirizzo);

          this.openPopup(selectedAlloggio);
        } else {
          console.error('Selected alloggio not found in the response.');
        }
      },
      error => {
        console.error('Errore nel recupero delle prenotazioni alloggio:', error);
      }
    );
  }

  openPopup(prenotazione: any): void {
    const dialogRef = this.dialog.open(PopupDettagliComponent, {
      data: { prenotazione },
      width: '400px',
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
    });
  }



  onDeletePrenotazioneAlloggio(idPrenotazione: number, statoAlloggio: string) {
    if (statoAlloggio === 'IN_CORSO') {
      const dialogRef = this.dialog.open(PopupDeleteConfermaComponent, {
        data: {
          message: 'Conferma eliminazione',
          action: 'Sei sicuro di voler eliminare questa prenotazione?',
        },
      });

      dialogRef.afterClosed().subscribe((result: boolean) => {
        if (result) {
          this.prenotazioniAlloggiService.deletePrenotazioneAlloggio(idPrenotazione).subscribe(
            () => {
              console.log(`Prenotazione Alloggio con ID ${idPrenotazione} eliminata con successo`);
              this.populateTable();
            },
            error => {
              console.error('Errore nell\'eliminazione della prenotazione alloggio:', error);
            }
          );
        } else {
          console.log(`L'utente ha annullato l'eliminazione della prenotazione con ID ${idPrenotazione}.`);
        }
      });
    } else {
      console.log(`La prenotazione con ID ${idPrenotazione} non è nello stato "IN_CORSO" e non può essere eliminata.`);
    }
  }

  onDeletePrenotazioneAttivita(idPrenotazione: number, statoAttivita: string) {
    if (statoAttivita === 'IN_CORSO') {
      const dialogRef = this.dialog.open(PopupDeleteConfermaComponent, {
        data: {
          message: 'Conferma eliminazione',
          action: 'Sei sicuro di voler eliminare questa prenotazione di attività?',
        },
      });

      dialogRef.afterClosed().subscribe((result: boolean) => {
        if (result) {
          this.prenotazioniAttivitaTurService.deletePrenotazioneAttivitaTuristica(idPrenotazione).subscribe(
            () => {
              console.log(`Prenotazione Attività con ID ${idPrenotazione} eliminata con successo`);
              this.populateTable();
            },
            error => {
              console.error('Errore nell\'eliminazione della prenotazione attività:', error);
            }
          );
        } else {
          console.log(`L'utente ha annullato l'eliminazione della prenotazione con ID ${idPrenotazione}.`);
        }
      });
    } else {
      console.log(`La prenotazione con ID ${idPrenotazione} non è nello stato "IN_CORSO" e non può essere eliminata.`);
    }
  }
  toggleMostraSoloInCorso() {
    this.mostraSoloInCorso = !this.mostraSoloInCorso;
    console.log('Stato mostraSoloInCorso:', this.mostraSoloInCorso);
    this.updateTable();
  }

  updateTable() {
    if (this.mostraSoloInCorso) {
      const prenotazioniInCorso = this.dataSource.data.filter(prenotazione => prenotazione.stato === 'IN_CORSO');
      this.dataSource.data = prenotazioniInCorso;
    } else {
      this.populateTable();
    }
  }


  @ViewChild('tableContainer')
  tableContainer!: ElementRef;

  @HostListener('window:scroll', ['$event'])
  onScroll(event: Event) {
    const tableContainer = this.tableContainer.nativeElement;
    const scrollPosition = window.pageYOffset + window.innerHeight;
    const tableBottom = tableContainer.offsetTop + tableContainer.offsetHeight;

  }

  formatStato(stato: string): string {
    return stato.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, (c) => c.toUpperCase());
  }
  formatDateTime(dateTimeString: string): string {
    const today = new Date();

    const formattedDate = this.formatDate(today);

    return `${formattedDate}`;
  }

  formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

}