import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { Component } from '@angular/core';
import { UploadService } from 'src/app/servizi/upload.service';
import { CalendariopopupComponent } from './calendariopopup/calendariopopup.component';
import { ItinerariService } from 'src/app/servizi/itinerari.service';
import { CookieService } from 'ngx-cookie-service';
import { UtenteService } from 'src/app/servizi/utente.service';
import { DomSanitizer, SafeResourceUrl, SafeUrl } from '@angular/platform-browser';

import { PrenotazioniAlloggioService } from 'src/app/servizi/prenotazioni-alloggio.service';
import { PrenotazioniAttivitaService } from 'src/app/servizi/prenotazioni-attivita.service';
import { ConnectionPositionPair } from '@angular/cdk/overlay';
import { forkJoin, map, switchMap } from 'rxjs';
import { PopupComponent } from '../gestione-attivita/gestione-valori/popup/popup.component';
import { ErrorPopupComponent } from './error-popup/error-popup.component';

@Component({
  selector: 'app-generazione-automatica',
  templateUrl: './generazione-automatica.component.html',
  styleUrls: ['./generazione-automatica.component.css']
})
export class GenerazioneAutomaticaComponent {

  alloggioImageUrls: string[] = [];
  attivitaTuristicaImageUrls: string[] = [];

  itinerarioAutoId: any;

  prenotazioniAlloggio: any;
  prenotazioniAttivitaTuristica: any;

  alloggi: any;
  attivitaTuristiche: any;

  idPrenotazioniAlloggio!: number[];
  idPrenotazioniAttivitaTuristiche!: number[];

  prenotazioniUnite: any = [];

  constructor(private itinerarioService: ItinerariService,
    private uploadService: UploadService,
    private route: Router,
    private dialog: MatDialog,
    private cookieService: CookieService,
    private utenteService: UtenteService,
    private prenotazioniAlloggioService: PrenotazioniAlloggioService,
    private prenotazioniAttivitaService: PrenotazioniAttivitaService) {
  }

  ngOnInit(): void {
    this.utenteService.getPreferenze().subscribe((risposta: any) => {
      console.log(risposta);
      this.generaItinerario();
    }, (error) => {
      const dialogRef = this.dialog.open(ErrorPopupComponent, {
        width: '250px',
      });
    })
  }

  generaItinerario(): void {
    this.alloggi = [];
    this.attivitaTuristiche = [];

    this.idPrenotazioniAlloggio = [];
    this.idPrenotazioniAttivitaTuristiche = [];

    this.itinerarioService.generaItinerario().subscribe((itinerarioRif) => {
      this.itinerarioAutoId = itinerarioRif.data.id;

      this.itinerarioService.visualizzaItinerario(this.itinerarioAutoId).subscribe((itinerario) => {
        this.prenotazioniAlloggio = itinerario.data.prenotazioniAlloggio.filter((item: any) => !item.camera.alloggio.eliminata);
        this.prenotazioniAttivitaTuristica = itinerario.data.prenotazioniAttivitaTuristica.filter((item: any) => !item.attivitaTuristica.eliminata);

        this.prenotazioniAlloggio.forEach((item: any, index: number) => {
          if (item.camera.alloggio.media !== null) {
            this.uploadService.elencaFileCaricati(item.camera.alloggio.media).subscribe((listaFiles) => {
              if (listaFiles.data.length > 0) {
                const fileName = listaFiles.data[0];
                this.uploadService.serviFile(item.camera.alloggio.media, fileName).subscribe((file) => {
                  let reader = new FileReader();
                  reader.onloadend = () => {
                    this.alloggioImageUrls[index] = reader.result as string;
                    this.alloggi.push({
                      ...item.camera.alloggio,
                      img: {
                        image: this.alloggioImageUrls[index],
                        fileName: fileName
                      }
                    });
                  };
                  reader.readAsDataURL(file);
                });
              }
            });
          }
        });

        this.prenotazioniAttivitaTuristica.forEach((item: any, index: number) => {
          if (item.attivitaTuristica.media !== null) {
            this.uploadService.elencaFileCaricati(item.attivitaTuristica.media).subscribe((listaFiles) => {
              if (listaFiles.data.length > 0) {
                const fileName = listaFiles.data[0];
                this.uploadService.serviFile(item.attivitaTuristica.media, fileName).subscribe((file) => {
                  let reader = new FileReader();
                  reader.onloadend = () => {
                    this.attivitaTuristicaImageUrls[index] = reader.result as string;
                    this.attivitaTuristiche.push({
                      ...item.attivitaTuristica,
                      img: {
                        image: this.attivitaTuristicaImageUrls[index],
                        fileName: fileName
                      }
                    });
                  };
                  reader.readAsDataURL(file);
                });
              }
            });
          }
        });

        console.log("ALLOGGI: ", this.alloggi);
        console.log("ATTIVITà: ", this.attivitaTuristiche);

        this.prenotazioniUnite = this.prenotazioniAlloggio.concat(this.prenotazioniAttivitaTuristica);
        console.log(this.prenotazioniUnite);
      });
    })

  }

  rigeneraItinerario() {
    this.cancellaItinerario();
    this.generaItinerario();
  }


  clickedHome() {
    this.cancellaItinerario();
    this.route.navigate(['/areaRiservata']);
  }

  openCalendario() {
    const dialogRef = this.dialog.open(CalendariopopupComponent, {
      data: {
        idItinerario: this.itinerarioAutoId,
        prenotazioniAlloggio: this.prenotazioniAlloggio.filter((item: any) => !item.camera.alloggio.eliminata),
        prenotazioniAttivitaTuristica: this.prenotazioniAttivitaTuristica.filter((item: any) => !item.attivitaTuristica.eliminata),
      },
      width: '500px',
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log(`Dialog result: ${result}`);
    });
  }

  cancellaItinerario() {
    this.itinerarioService.cancellaItinerario(this.itinerarioAutoId).subscribe((risposta) => {
      console.log("CANCELLAZIONE ITINERARIO", risposta);
    })
  }

  /*Visualizza dati itinerario*/

  // visualizzaItinerario(id: number): void {
  //   this.itinerarioService.visualizzaItinerario(id).subscribe(
  //     (dettagliItinerario) => {

  //       const prenotazioniAlloggio = dettagliItinerario.data.prenotazioniAlloggio;
  //       const prenotazioniAttivita = dettagliItinerario.data.prenotazioniAttivitaTuristica;


  //       // this.alloggioDataList = this.itinerarioAuto.filter((item: any) => item.)



  //       if (prenotazioniAlloggio && prenotazioniAlloggio.length > 0) {
  //         this.loadDataAlloggio(prenotazioniAlloggio[0].camera.alloggio);
  //       }

  //       if (prenotazioniAttivita && prenotazioniAttivita.length > 0) {
  //         for (let i = 0; i < prenotazioniAttivita.length; i++) {
  //           const datiAttivita = prenotazioniAttivita[i].attivitaTuristica;
  //           this.loadDataAttivitaTuristica(datiAttivita);
  //         }
  //       } else {
  //         console.log('Nessuna prenotazione di attività turistiche disponibile.');
  //       }

  //       this.itinerarioCompleto = this.alloggioDataList.concat(this.attivitaTuristicaDataList);
  //       console.log("ITINERARIO COMPLETO", this.itinerarioCompleto)
  //     }
  //   );
  // }

  /*mi aiuta alla visualizzazione dei dati dell'alloggio*/

  // loadDataAlloggio(alloggioData: any): void {
  //   console.log('Alloggio - Nome:', alloggioData.nome);
  //   console.log('Alloggio - DescrizioneBreve:', alloggioData.descrizioneBreve);
  //   console.log('immagine ALLOGGIO', alloggioData.media);


  //   const alloggioElement = {
  //     data: {
  //       nome: alloggioData.nome,
  //       descrizioneBreve: alloggioData.descrizioneBreve,
  //     }
  //   };

  //   this.alloggioDataList.push(alloggioElement);

  //   if (alloggioData.media && alloggioData.media.length > 0) {
  //     this.processMedia(alloggioData.media);
  //   }
  // }

  /*mi aiuta alla visualizzazione dei dati delle attivita */
  // loadDataAttivitaTuristica(attivitaTuristicaData: any): void {
  //   console.log('AttivitaTuristica - Nome:', attivitaTuristicaData.nome);
  //   console.log('AttivitaTuristica - DescrizioneBreve:', attivitaTuristicaData.descrizioneBreve);
  //   console.log('immagine ATTIVITà TURISTICA', attivitaTuristicaData.media);

  //   const attivitaTuristicaElement = {
  //     data: {
  //       nome: attivitaTuristicaData.nome,
  //       descrizioneBreve: attivitaTuristicaData.descrizioneBreve,
  //     }
  //   };

  //   this.attivitaTuristicaDataList.push(attivitaTuristicaElement);

  //   if (attivitaTuristicaData.media && attivitaTuristicaData.media.length > 0) {
  //     this.processMedia(attivitaTuristicaData.media);
  //   }
  // }

  // processMedia(mediaItem: any): void {
  //   if (mediaItem) {
  //     this.uploadService.elencaFileCaricati(mediaItem).subscribe((listaFiles) => {
  //       if (listaFiles.data.length > 0) {
  //         const fileName = listaFiles.data[0];
  //         this.uploadService.serviFile(mediaItem, fileName).subscribe((file) => {
  //           let reader = new FileReader();
  //           reader.onloadend = () => {
  //             this.imageUrls.push(reader.result as string);
  //           };
  //           reader.readAsDataURL(file);
  //         });
  //       }
  //     });
  //   } else {
  //     console.error('mediaItem non è definito:', mediaItem);
  //   }
  // }


  /*Prendo le preferenze e genero l'itinerario */
  // generaItinerario(): void {
  //   this.getPreferenze()
  //     .then((preferenze) => {
  //       return this.itinerarioService.generaItinerario();
  //     })
  //     .then((itinerarioObservable) => {
  //       itinerarioObservable.subscribe(
  //         (itinerarioGenerato) => {
  //           const idItinerarioGenerato = itinerarioGenerato.data.id;
  //           console.log('Itinerario generato:', itinerarioGenerato);
  //           console.log('dati', idItinerarioGenerato);

  //           this.salvaIdItinerarioNelloStorageLocale(idItinerarioGenerato);
  //           this.visualizzaItinerario(idItinerarioGenerato);
  //         },
  //         (errore) => {
  //           console.error('Errore durante la generazione dell\'itinerario:', errore);
  //         }
  //       );
  //     })
  //     .catch((errore) => {
  //       console.error('Errore durante il recupero delle preferenze:', errore);
  //     });
  // }

  /*metodo che mi prende le preferenze dell'utente*/

  // getPreferenze(): Promise<any> {
  //   const userIdString = this.cookieService.get('userId');
  //   const userId = parseInt(userIdString, 10);

  //   return new Promise<any>((resolve, reject) => {
  //     if (!isNaN(userId)) {
  //       this.utenteService.getPreferenze().subscribe(
  //         (preferenze) => {
  //           console.log('Preferenze utente:', preferenze);
  //           resolve(preferenze);
  //         },
  //         (errore) => {
  //           console.error('Errore durante il recupero delle preferenze dell\'utente:', errore);
  //           reject(errore);
  //         }
  //       );
  //     } else {
  //       console.error('ID dell\'utente non trovato');
  //       reject('ID dell\'utente non trovato');
  //     }
  //   });
  // }

  // getPreferenze(): Promise<any> {
  //   const userIdString = this.cookieService.get('userId');
  //   const userId = parseInt(userIdString, 10);

  //   return new Promise<any>((resolve, reject) => {
  //     if (!isNaN(userId)) {
  //       this.utenteService.getPreferenze().subscribe(
  //         (preferenze) => {
  //           console.log('Preferenze utente:', preferenze);
  //           resolve(preferenze);
  //         },
  //         (errore) => {
  //           console.error('Errore durante il recupero delle preferenze dell\'utente:', errore);
  //           reject(errore);
  //         }
  //       );
  //     } else {
  //       console.error('ID dell\'utente non trovato');
  //       reject('ID dell\'utente non trovato');
  //     }
  //   });
  // }

}
