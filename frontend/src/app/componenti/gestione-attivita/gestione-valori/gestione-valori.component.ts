import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { AttivitaService } from 'src/app/servizi/attivita.service';
import { UploadService } from 'src/app/servizi/upload.service';
import { ValoriEcosostenibilitaService } from 'src/app/servizi/valori-ecosostenibilita.service';
import { PopupComponent } from './popup/popup.component';

@Component({
  selector: 'app-gestione-valori',
  templateUrl: './gestione-valori.component.html',
  styleUrls: ['./gestione-valori.component.css']
})
export class GestioneValoriComponent implements OnInit {

  imageUrl: string = '';
  id: number = 0;
  selectedValoreId: number = 0;
  valoriEcosostenibilita: string[] = [];
  valoriEcosostenibilitaSelected: { [key: string]: string } = {};
  isGestoreAttivita: boolean = false;
  nome: any;
  originalValoriEcosostenibilitaSelected: { [key: string]: string } = {};
  changesMade: boolean = false;

  constructor(
    private attivitaService: AttivitaService,
    private route: ActivatedRoute,
    private cookieService: CookieService,
    private valorieco: ValoriEcosostenibilitaService,
    private uploadService: UploadService,
    protected dialog: MatDialog,
    private router: Router
  ) { }

  valori = [
    { label: '', selezionato: '' }
  ];

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.id = +params['id'];
      console.log("ID ATTIVITA", this.id)
    });

      this.visualizzaDettagliAttivita();
  }

  visualizzaDettagliAttivita(): void {
    this.attivitaService.visualizzaAttivita(this.id).subscribe(
      (attivita) => {    
        this.selectedValoreId = attivita.data.valoriEcosostenibilita.id;
        console.log('valori dichiarati dall\'attivita: ', attivita.data.valoriEcosostenibilita);

        this.originalValoriEcosostenibilitaSelected = {};
        this.valoriEcosostenibilitaSelected = {};
        this.valoriEcosostenibilita = Object.keys(attivita.data.valoriEcosostenibilita)
          .filter(key => key !== 'id');

        this.valoriEcosostenibilita.forEach(valore => {
          const valoreId = attivita.data.valoriEcosostenibilita[valore].id;
          this.originalValoriEcosostenibilitaSelected[valore] = attivita.data.valoriEcosostenibilita[valore] ? 'true' : 'false';
          this.valoriEcosostenibilitaSelected[valore] = this.originalValoriEcosostenibilitaSelected[valore];
        });

        // Call the function to display the image for the activity
        this.getImmagineUrl(attivita.data.media);
        
        // Assign the name from the activity object
        this.nome = attivita.data.nome; // Assuming there is a 'nome' property in the activity object
      },
      (error) => {
        console.error(error);
      }
    );
  }
  async getImmagineUrl(media: any): Promise<void> {
    try {
      const listaFiles = await this.uploadService.elencaFileCaricati(media).toPromise();
      if (listaFiles.data.length > 0) {
        const fileName = listaFiles.data[0];
        const file = await this.uploadService.serviFile(media, fileName).toPromise();

        // Read the file and set the image URL
        let reader = new FileReader();
        reader.onloadend = () => {
          this.imageUrl = reader.result as string;
        };
        reader.readAsDataURL(file);
      }
    } catch (error) {
      console.error('Errore durante il recupero dell\'URL dell\'immagine', error);
    }
  }
  
// Gestione del cambiamento dell'opzione selezionata
selezionatoRadio(valore: string, option: string) {
  this.valoriEcosostenibilitaSelected[valore] = option;
  this.changesMade = true; // Track changes
  this.updateSubmit();
}

// Inizializzazione dell'array valoriEcosostenibilitaSelected
inizializzaValoriEcosostenibilitaSelected() {
  this.valoriEcosostenibilita.forEach(valore => {
      this.valoriEcosostenibilitaSelected[valore] = '';  // Inizializza tutti a vuoti
  });
}

// Aggiornamento della sottomissione
updateSubmit() {
  const isValoriInseriti = this.valori.every(item => item.selezionato === 'sì' || item.selezionato === 'no');
}


  convertLabelToCamelCase(label: string): string {
    const words = label.split(' ');
    const camelCaseWords = words.map((word, index) =>{
      if(index === 0){
        return word.toLowerCase();
      } else {
        if (word === 'CO2'){
          return word;
        } else {
          return word.charAt(0).toUpperCase() + word.slice(1).toLocaleLowerCase();
        }
      }
    });
    return camelCaseWords.join('');
  }

  convertCamelCaseToReadable(camelCase: string): string {
    let result = camelCase.replace(/([A-Z])/g, ' $1');
    result = result.replace('C O2', 'CO2');
    return result.charAt(0).toUpperCase() + result.slice(1);
  }

  getRuoloFromCookie(): string {
    return this.cookieService.get('ruolo');
  }
  
  submitForm(): void {
    if (!this.selectedValoreId) {
      console.error('ID dei valori di ecosostenibilità non valido.');
      return;
    }
    if (!this.changesMade) {
      this.openPopup('Nessuna modifica effettuata.');
      return;  // Stop further execution
    }
  
    const politicheAntispreco = this.valoriEcosostenibilitaSelected['politicheAntispreco'] === 'true';
    const prodottiLocali = this.valoriEcosostenibilitaSelected['prodottiLocali'] === 'true';
    const energiaVerde = this.valoriEcosostenibilitaSelected['energiaVerde'] === 'true';
    const raccoltaDifferenziata = this.valoriEcosostenibilitaSelected['raccoltaDifferenziata'] === 'true';
    const limiteEmissioneCO2 = this.valoriEcosostenibilitaSelected['limiteEmissioneCO2'] === 'true';
    const contattoConNatura = this.valoriEcosostenibilitaSelected['contattoConNatura'] === 'true';
  
    // Assicurati di chiamare il servizio di modifica qui
    this.valorieco.modificaValoriEcosostenibilita(
      this.selectedValoreId,
      politicheAntispreco,
      prodottiLocali,
      energiaVerde,
      raccoltaDifferenziata,
      limiteEmissioneCO2,
      contattoConNatura
    ).subscribe(
      (response) => {
        // Gestisci la risposta in base alle tue esigenze
        console.log('Modifica effettuata con successo', response);
        this.openPopup("Modifica effettuata con successo");

        
      },
      (error) => {
        console.error('Errore durante la modifica', error);
      }
    );
  }

 rollbackChanges(): void {
  const changesDetected = Object.keys(this.valoriEcosostenibilitaSelected).some(valore => 
    this.valoriEcosostenibilitaSelected[valore] !== this.originalValoriEcosostenibilitaSelected[valore]
  );

  if (changesDetected) {
    this.valoriEcosostenibilitaSelected = { ...this.originalValoriEcosostenibilitaSelected };
    this.changesMade = false;
  }

  this.router.navigate(['/mieAttivita']);
}

  //i popup
  openPopup(message: string): void {
    const dialogRef = this.dialog.open(PopupComponent, {
      width: '250px',
      data: { message },
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('Il popup è stato chiuso');
    });
  }

}
