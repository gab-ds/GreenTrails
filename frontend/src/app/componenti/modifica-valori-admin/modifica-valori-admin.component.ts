import { PopuperroreComponent } from './popuperrore/popuperrore.component';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { AttivitaService } from 'src/app/servizi/attivita.service';
import { UploadService } from 'src/app/servizi/upload.service';
import { ValoriEcosostenibilitaService } from 'src/app/servizi/valori-ecosostenibilita.service';
import { PopupComponent } from '../gestione-attivita/gestione-valori/popup/popup.component';
import { Location } from '@angular/common';

@Component({
  selector: 'app-modifica-valori-admin',
  templateUrl: './modifica-valori-admin.component.html',
  styleUrls: ['./modifica-valori-admin.component.css']
})
export class ModificaValoriAdminComponent implements OnInit {
  isTextareaVisible: boolean = false;
  isTextareaFilled: boolean = false;

  imageUrl: string = '';
  id: number = 0;
  selectedValoreId: number = 0;
  valoriEcosostenibilita: string[] = [];
  //valoriEcosostenibilitaSelected: { [key: string]: string } = {};
  isGestoreAttivita: boolean = false;
  nome: any;
  //originalValoriEcosostenibilitaSelected: { [key: string]: string } = {};
  changesMade: boolean = false;
  idAttivita: any;

  constructor(
    private attivitaService: AttivitaService,
    private route: ActivatedRoute,
    private cookieService: CookieService,
    private valorieco: ValoriEcosostenibilitaService,
    private uploadService: UploadService,
    protected dialog: MatDialog
    , private router: Router,
    private location: Location
  ) { }

  valori = [
    { label: '', selectedOption: '' }
  ];

  val: any;

  isQuestionarioFilled: boolean = false;

  ngOnInit(): void {
    this.id = Number(this.getIdFromCookie());

    if (this.id) {
      this.visualizzaDettagliAttivita();
      this.isTextareaVisible = true;
    } else {
      console.error('Invalid ID ');
    }
  }

  visualizzaDettagliAttivita(): void {
    this.attivitaService.visualizzaAttivita(this.id).subscribe(
      (attivita) => {
        this.idAttivita = attivita.data.id;
        this.selectedValoreId = attivita.data.valoriEcosostenibilita.id;
        this.val = attivita.data.valoriEcosostenibilita;
        console.log('valori dichiarati dall\'attivita: ', this.val);


        let valoriEcosostenibilitaTrue: string[] = Object.entries(attivita.data.valoriEcosostenibilita)
        .filter(([nomePolitica, valore]) => valore === true)
        .map(([nomePolitica, valore]) => this.convertCamelCaseToReadable(nomePolitica));

      this.valori = valoriEcosostenibilitaTrue.map((label) => ({
        label: label,
        selectedOption: ''
      }));

      console.log('VALORI VERI:',this.valori)
        
  
        // this.originalValoriEcosostenibilitaSelected = {};
        // this.valoriEcosostenibilitaSelected = {};
        // this.valoriEcosostenibilita = Object.keys(attivita.data.valoriEcosostenibilita)
        //   .filter(key => key !== 'id')
        //   .filter(valore => attivita.data.valoriEcosostenibilita[valore] === true); // Filtra solo i valori impostati a true
  
        // this.valoriEcosostenibilita.forEach(valore => {
        //   const valoreId = attivita.data.valoriEcosostenibilita[valore].id;
        //   this.originalValoriEcosostenibilitaSelected[valore] = attivita.data.valoriEcosostenibilita[valore] ? 'true' : 'false';
        //   this.valoriEcosostenibilitaSelected[valore] = this.originalValoriEcosostenibilitaSelected[valore];
        // });
        
  
        this.getImmagineUrl(attivita.data.media);
  
        this.nome = attivita.data.nome;
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
  // selezionatoRadio(valore: string, option: string) {
  //   this.valoriEcosostenibilitaSelected[valore] = option;
  //   this.changesMade = true;
  //   //this.updateSubmit();
  // }

  selectOption(item: any, option: any) {
    item.selectedOption = option;
    const key = this.convertLabelToCamelCase(item.label);
    if (option === 'no') {
      console.log("CHIAVE:", key)
      this.val[key] = false;
      console.log(this.val[key])
    } else {
      console.log("CHIAVE:", key)
      this.val[key] = true;
      console.log(this.val[key])
    }

    //this.updateSubmitButton();
  }

  // Aggiornamento della sottomissione
  //updateSubmit() {
    //const isValoriInseriti = this.valori.every(item => item.selezionato === 'sì' || item.selezionato === 'no');
  //}


  convertLabelToCamelCase(label: string): string {
    const words = label.split(' ');
    const camelCaseWords = words.map((word, index) => {
      if (index === 0) {
        return word.toLowerCase();
      } else {
        if (word === 'CO2') {
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

  getIdFromCookie(): string {
    return this.cookieService.get('idAttivita');
  }

  isChiarimentiValido: boolean = false;
  isChiarimentiLengthValid: boolean = false;
  chiarimenti: string= '';

  openPopupErrore(message: string): void {
      const dialogRef = this.dialog.open(PopuperroreComponent, {
        width: '250px',
        data: { message },
        disableClose: true,
      });
  }

  submitForm(): void {
    console.log(this.chiarimenti);
    if (!this.selectedValoreId) {
      console.error('ID dei valori di ecosostenibilità non valido.');
      return;
    }

    console.log('validate radio:',this.validateRadio());

    
    if(this.validateChiarimenti() && this.validateRadio()){
    
    this.valorieco.modificaValoriEcosostenibilita(
      this.selectedValoreId,
      this.val.politicheAntispreco,
        this.val.prodottiLocali,
        this.val.energiaVerde,
        this.val.raccoltaDifferenziata,
        this.val.limiteEmissioneCO2,
        this.val.contattoConNatura,
    ).subscribe(
      (response) => {
        console.log('Modifica effettuata con successo', response);
        if (response?.status === 'success') {
          
            this.openPopup('Modifica effettuata');
          
        } else {
          this.openPopup('Impossibile effettuare la modifica')
        } 
      },
      (error) => {
        console.error('Errore durante la modifica', error);
      }
    );
     } else if(!this.isChiarimentiLengthValid){
       this.openPopupErrore('La lunghezza chiarimenti eccessiva');
    } else if(!this.isChiarimentiValido) {
      this.openPopupErrore('Il formato dei chiarimenti non è valido');
    } else if(!this.validateRadio()){
       this.openPopupErrore('Non sono stati selezionati tutti i campi');
    }
  }

  // rollbackChanges(): void {
  //   const changesDetected = Object.keys(this.valoriEcosostenibilitaSelected).some(valore =>
  //     this.valoriEcosostenibilitaSelected[valore] !== this.originalValoriEcosostenibilitaSelected[valore]
  //   );

  //   if (changesDetected) {
  //     this.valoriEcosostenibilitaSelected = { ...this.originalValoriEcosostenibilitaSelected };
  //     this.changesMade = false;
  //   }
  //   this.location.back();
  // }
  
  onTextareaChange(): void {
    const textareaValue = (document.getElementById('descrizione') as HTMLTextAreaElement).value;
    this.isTextareaFilled = textareaValue.trim() !== '';
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

  validateChiarimenti(): boolean{
    const regex = /^$|^[A-Za-zÀ-ÖØ-öø-ÿ0-9][\s\S]*$/;
    this.isChiarimentiValido = regex.test((document.getElementById('descrizione') as HTMLTextAreaElement).value);
    this.isChiarimentiLengthValid = (document.getElementById('descrizione') as HTMLTextAreaElement).value.length<=1000;
    return this.isChiarimentiLengthValid && this.isChiarimentiValido;
  }

  validateRadio() {
   return this.valori.every((item: any) =>{ 
    console.log('ITEMMMMMM', item, item.selectedOption);
    return item.selectedOption === 'sì' || item.selectedOption === 'no';
  });
}

goBack(){
  this.location.back();
}

}
