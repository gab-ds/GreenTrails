import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgbRatingConfig } from '@ng-bootstrap/ng-bootstrap';
import { CookieService } from 'ngx-cookie-service';
import { AttivitaService } from 'src/app/servizi/attivita.service';
import { RecensioneService } from 'src/app/servizi/recensione.service';
import { UploadService } from 'src/app/servizi/upload.service';
import { UtenteService } from 'src/app/servizi/utente.service';

@Component({
  selector: 'app-home-page',
  templateUrl: './home-page.component.html',
  styleUrls: ['./home-page.component.css']
})
export class HomePageComponent implements OnInit {

  attivitaList: any[] = [];
  fileNames: string[] = [];
  imageUrls: string[][] = [];

  limite: number = 5;
  nomeAttivita: string = 'nome';
  attivitaTuristicheList: any[] = [];
  alloggiList: any[] = [];
  attivitaPerPrezzoList: any[] = [];
  filteredAttivitaPerPrezzoList: any[] = [];

  idPerPrezzo: any;
  idAlloggi: any;
  idAttivita: any;

  ratingAlloggi: any = [];
  ratingAttivita: any = [];
  ratingPerPrezzo: any = [];

  idSuggeriti: any = [];
  suggeriti: any = [];
  ratingSuggeriti: any = [];


  constructor(
    private attivitaService: AttivitaService,
    private uploadService: UploadService,
    private router: Router,
    private userService: UtenteService,
    private recensioneService: RecensioneService,
    config: NgbRatingConfig) {
    config.max = 5;
    config.readonly = true;
  }



  ngOnInit(): void {
    this.suggeriti = (this.attivitaTuristicheList || []).concat(this.alloggiList || [])
    Promise.all([
      this.loadDataForAttivitaPerPrezzo(5),
      this.loadDataForAlloggi(5),
      this.loadDataForAttivitaTuristiche(5),
    ]).then(() => {
      this.filterByPrezzo();
    });

  }

  loadDataForAttivitaPerPrezzo(limite: number): Promise<void> {
    return new Promise<void>((resolve) => {
      this.visualizzaListaAttivitaPerPrezzo(limite).then(() => {
        this.processMediaFiles().then(() => {
          console.log('Data loaded for AttivitaPerPrezzo:', this.attivitaPerPrezzoList);
          resolve();
        });
      });
    });
  }

  loadDataForAlloggi(limite: number): void {
    this.visualizzaListaAlloggi(limite).then(() => {
      this.processMediaFiles().then(() => {
        console.log('Data loaded for Alloggi:', this.alloggiList);
        this.suggeriti = this.attivitaTuristicheList.concat(this.alloggiList);

        this.suggeriti.forEach((item: any, index: number) => {
          this.recensioneService.visualizzaRecensioniPerAttivita(item.id).subscribe((risposta: any) => {
            if (risposta.data && risposta.data.length > 0) {
              const sommaValutazioni = risposta.data.reduce((acc: any, recensione: any) => acc + recensione.valutazioneStelleEsperienza, 0);
              const mediaValutazioni = sommaValutazioni / risposta.data.length;
              console.log(`Average ratings for activities suggerite ${item.id}:`, mediaValutazioni);
              this.ratingSuggeriti[index] = mediaValutazioni;
            } else {
              console.log(`No reviews available to calculate the average for activity ${item.id}.`);
            }
          });
        });
      });
    });
  }

  loadDataForAttivitaTuristiche(limite: number): void {
    this.visualizzaListaAttivitaTuristiche(limite).then(() => {
      this.processMediaFiles().then(() => {
        console.log('Data loaded for AttivitaTuristiche:', this.attivitaTuristicheList);
        this.suggeriti = this.attivitaTuristicheList.concat(this.alloggiList);

        this.suggeriti.forEach((item: any, index: number) => {
          this.recensioneService.visualizzaRecensioniPerAttivita(item.id).subscribe((risposta: any) => {
            if (risposta.data && risposta.data.length > 0) {
              const sommaValutazioni = risposta.data.reduce((acc: any, recensione: any) => acc + recensione.valutazioneStelleEsperienza, 0);
              const mediaValutazioni = sommaValutazioni / risposta.data.length;
              console.log(`Average ratings for activities suggerite ${item.id}:`, mediaValutazioni);
              this.ratingSuggeriti[index] = mediaValutazioni;
            } else {
              console.log(`No reviews available to calculate the average for activity ${item.id}.`);
            }
          });
        });
      });
    });
  }

  private visualizzaListaAttivitaPerPrezzo(limite: number): Promise<void> {
    return new Promise<void>((resolve) => {
      this.attivitaService.visualizzaAttivitaPerPrezzo(limite).subscribe(
        (result) => {
          console.log("API Response for AttivitaPerPrezzo:", result);

          if (result.data) {
            const newAttivita = result.data;
            console.log("New Attivita from API for AttivitaPerPrezzo:", newAttivita);

            const filteredAttivita = newAttivita.filter((item: { id: any; prezzo: number; eliminata: boolean }) => item.prezzo < 300 && !item.eliminata);

            this.attivitaPerPrezzoList.push(...filteredAttivita);

            this.idPerPrezzo = this.attivitaPerPrezzoList.map((item: any) => {
              return {
                id: item.id
              }
            });

            console.log("REVIEWS FOR PRICE")
            this.idPerPrezzo.forEach((attivita: any, index: number) => {
              this.recensioneService.visualizzaRecensioniPerAttivita(attivita.id).subscribe((risposta: any) => {
                if (risposta.data && risposta.data.length > 0) {
                  const sommaValutazioni = risposta.data.reduce((acc: any, recensione: any) => acc + recensione.valutazioneStelleEsperienza, 0);
                  const mediaValutazioni = sommaValutazioni / risposta.data.length;
                  console.log(`Average ratings for activities by price ${attivita.id}:`, mediaValutazioni);
                  this.ratingPerPrezzo[index] = mediaValutazioni; // Assegnazione corretta
                } else {
                  console.log(`No reviews available to calculate the average for activity ${attivita.id}.`);
                }
              });
            });

            this.processMediaFiles().then(() => {
              console.log("Data loaded for AttivitaPerPrezzo:", this.attivitaPerPrezzoList);
              resolve();
            });
          } else {
            console.error("Unexpected API response structure for AttivitaPerPrezzo:", result);
            resolve();
          }
        },
        (error) => {
          console.error("Error fetching AttivitaPerPrezzo:", error);
          resolve();
        }
      );
    });
  }

  private visualizzaListaAlloggi(limite: number): Promise<void> {
    return new Promise<void>((resolve) => {
      this.attivitaService.getAlloggi(limite).subscribe((result) => {
        const newAlloggi = result.data.filter((item: any) => !item.eliminata);
        this.alloggiList.push(...newAlloggi);

        this.idAlloggi = this.alloggiList.map((item: any) => {
          return {
            id: item.id
          }
        });

        console.log("REVIEWS FOR LODGING")
        this.idAlloggi.forEach((alloggio: any, index: number) => {
          this.recensioneService.visualizzaRecensioniPerAttivita(alloggio.id).subscribe((risposta: any) => {
            if (risposta.data && risposta.data.length > 0) {
              const sommaValutazioni = risposta.data.reduce((acc: any, recensione: any) => acc + recensione.valutazioneStelleEsperienza, 0);
              const mediaValutazioni = sommaValutazioni / risposta.data.length;
              console.log(`Average ratings for lodging ${alloggio.id}:`, mediaValutazioni);
              this.ratingAlloggi[index] = mediaValutazioni; // Assegnazione corretta
            } else {
              console.log(`No reviews available to calculate the average for lodging ${alloggio.id}.`);
            }
          });
        });

        console.log("MEDIA ALLOGGI MAMMT", this.ratingAlloggi);


        resolve();
      });
    });
  }

  private visualizzaListaAttivitaTuristiche(limite: number): Promise<void> {
    return new Promise<void>((resolve) => {
      this.attivitaService.getAttivitaTuristiche(limite).subscribe((result) => {
        const newAttivitaTuristiche = result.data.filter((item: any) => !item.eliminata);
        this.attivitaTuristicheList.push(...newAttivitaTuristiche);

        this.idAttivita = this.attivitaTuristicheList.map((item: any) => {
          return {
            id: item.id
          }
        });

        console.log("REVIEWS FOR ACTIVITIES")
        this.idAttivita.forEach((attivita: any, index: number) => {
          this.recensioneService.visualizzaRecensioniPerAttivita(attivita.id).subscribe((risposta: any) => {
            if (risposta.data && risposta.data.length > 0) {
              const sommaValutazioni = risposta.data.reduce((acc: any, recensione: any) => acc + recensione.valutazioneStelleEsperienza, 0);
              const mediaValutazioni = sommaValutazioni / risposta.data.length;
              console.log(`Average ratings for activity ${attivita.id}:`, mediaValutazioni);
              this.ratingAttivita[index] = mediaValutazioni; // Assegnazione corretta
            } else {
              console.log(`No reviews available to calculate the average for activity ${attivita.id}.`);
            }
          });
        });
      });

      resolve();
    });
  }

  private processMediaFiles(): Promise<void[]> {
    const allItems = [
      ...this.attivitaList,
      ...this.alloggiList,
      ...this.attivitaTuristicheList,
      ...this.attivitaPerPrezzoList
    ];

    const promises = allItems.map((item: any) => {
      return new Promise<void>((resolve) => {
        this.uploadService.elencaFileCaricati(item.media).subscribe((listaFiles) => {
          if (listaFiles.data.length > 0) {
            const fileNames = listaFiles.data;
            const imageUrlsForItem: string[] = [];

            const filePromises = fileNames.map((fileName: string) => {
              return new Promise<void>((fileResolve) => {
                this.uploadService.serviFile(item.media, fileName).subscribe((file) => {
                  let reader = new FileReader();
                  reader.onloadend = () => {
                    imageUrlsForItem.push(reader.result as string);
                    fileResolve();
                  };
                  reader.readAsDataURL(file);
                });
              });
            });

            Promise.all(filePromises).then(() => {
              this.imageUrls[item.id] = [...imageUrlsForItem];
              resolve();
            });
          } else {
            resolve();
          }
        });
      });
    });

    return Promise.all(promises);
  }

  private filterByPrezzo(): void {
    this.filteredAttivitaPerPrezzoList = this.attivitaPerPrezzoList.filter((item: { prezzo: number }) => {
      return item.prezzo < 300;
    });
    console.log("Filtered List for Price:", this.filteredAttivitaPerPrezzoList);
  }

  shuffleArray(array: any[]): any[] {
    const shuffledArray = [...array];
    for (let i = shuffledArray.length - 1; i > 0; i--) {
      const j = Math.floor(Math.random() * (i + 1));
      [shuffledArray[i], shuffledArray[j]] = [shuffledArray[j], shuffledArray[i]];
    }
    return shuffledArray;
  }

  shuffleReviews(array: any[]): any[] {
    const shuffledArray = [...array];
    for (let i = shuffledArray.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [shuffledArray[i], shuffledArray[j]] = [shuffledArray[j], shuffledArray[i]];
    }
    return shuffledArray;
}

  navigateToAttivita(id: number): void {
    this.router.navigate(['/attivita', id]);
  }

  logout() {
    this.userService.logout().subscribe(
      (response) => {
        console.log('Logout successful:', response);
        // Add additional logic here if necessary
      },
      (error) => {
        console.error('Error logging out:', error);
        // Handle error if necessary
      }
    );
  }

}