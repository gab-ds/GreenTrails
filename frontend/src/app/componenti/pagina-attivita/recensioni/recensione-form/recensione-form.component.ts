import { RecensioneService } from 'src/app/servizi/recensione.service';
import { Component, Inject, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { NgbRatingConfig } from '@ng-bootstrap/ng-bootstrap';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { AttivitaService } from 'src/app/servizi/attivita.service';
import { MatDialog } from '@angular/material/dialog';
import { PopupRecensioneComponent } from '../popup-recensione/popup-recensione.component';
import { ValoriEcosostenibilitaService } from 'src/app/servizi/valori-ecosostenibilita.service';
import { PopupRecensioneFailComponent } from '../popup-recensione-fail/popup-recensione-fail.component';
import { DOCUMENT } from '@angular/common';

@Component({
  selector: 'app-recensione-form',
  templateUrl: './recensione-form.component.html',
  styleUrls: ['./recensione-form.component.css'],
  providers: [NgbRatingConfig],
})
export class RecensioneFormComponent implements OnInit {

  idAttivita: number = 0;
  rating: number = 0;
  valoriEcosostenibilita: any;
  valutazioneDiscorsiva: string = '';

  idValori: number = 0;

  isTextAreaValid: boolean = true;
  isTextAreaLengthValid: boolean = true;
  isSubmitDisabled: boolean = true;

  valoriEcosostenibilitaRecensione: any;

  isRatingSelected: boolean = false;
  isQuestionnaireFilled: boolean = false;

  isFileValid: boolean = false;

  valori = [
    { label: '', selectedOption: '' }
  ];

  file!: File | null;

  constructor(
    config: NgbRatingConfig,
    private recensioneService: RecensioneService,
    private route: ActivatedRoute,
    private attivitaService: AttivitaService,
    private dialog: MatDialog,
    private valoriService: ValoriEcosostenibilitaService,
    @Inject(DOCUMENT) public document: Document
  ) {
    config.max = 5;
    config.readonly = false;
  }

  onFileSelected(event: any) {
    this.file = event.target.files[0];

    if (this.file!.size > 100 * 1024 * 1024) {
      this.openPopupFail('Il file è troppo grande! La dimensione massima del file è 100MB.');
      this.file = null;
      return;
    }

    const fileType = this.file!.type;
    if (!fileType.startsWith('image/') && !fileType.startsWith('video/')) {
      this.openPopupFail('Il formato del file non è corretto! Puoi caricare solo immagini o video.');
      this.file = null;
      return;
    }

    console.log("File selected: ", this.file);
    console.log('name: ', this.file!.name);
    console.log('size: ', this.file!.size);
    console.log('type: ', this.file!.type);
  }

  ngOnInit() {
    this.route.paramMap.subscribe((params: ParamMap) => {
      let id = parseInt(params.get('id')!);
      this.idAttivita = id;

      this.visualizzaDettagliAttivita();
    })
  }

  convertCamelCaseToReadable(camelCase: string): string {
    let result = camelCase.replace(/([A-Z])/g, ' $1');
    result = result.replace('C O2', 'CO2');
    return result.charAt(0).toUpperCase() + result.slice(1);
  }

  visualizzaDettagliAttivita(): void {
    this.attivitaService.visualizzaAttivita(this.idAttivita).subscribe((attivita) => {
      this.valoriEcosostenibilita = attivita.data.valoriEcosostenibilita;
      this.idValori = attivita.data.valoriEcosostenibilita.id;
      let valoriEcosostenibilitaTrue: string[] = Object.entries(attivita.data.valoriEcosostenibilita)
        .filter(([nomePolitica, valore]) => valore === true)
        .map(([nomePolitica, valore]) => this.convertCamelCaseToReadable(nomePolitica));

      this.valori = valoriEcosostenibilitaTrue.map((label) => ({
        label: label,
        selectedOption: ''
      }));
    }, (error) => {
      console.error(error);
    })
  }

  convertLabelToCamelCase(label: string): string {
    const words = label.split(' ');
    const camelCaseWords = words.map((word, index) => {
      if (index === 0) {
        return word.toLowerCase();
      } else {
        if (word === 'CO2') {
          return word;
        } else {
          return word.charAt(0).toUpperCase() + word.slice(1).toLowerCase();
        }
      }
    });
    return camelCaseWords.join('');
  }

  updateSubmitButton() {
    this.isRatingSelected = this.rating > 0;
    this.isQuestionnaireFilled = this.valori.every(item => item.selectedOption === 'sì' || item.selectedOption === 'no');

    this.isSubmitDisabled = !this.isRatingSelected || !this.isQuestionnaireFilled || !this.isTextAreaValid || !this.isTextAreaLengthValid;
  }

  selectOption(item: any, option: any) {
    item.selectedOption = option;
    const key = this.convertLabelToCamelCase(item.label);
    if (option === 'no') {
      console.log("CHIAVE:", key)
      this.valoriEcosostenibilita[key] = false;
      console.log(this.valoriEcosostenibilita[key])
    } else {
      console.log("CHIAVE:", key)
      this.valoriEcosostenibilita[key] = true;
      console.log(this.valoriEcosostenibilita[key])
    }

    this.updateSubmitButton();
  }

  validateTextArea() {
    const regex = /^$|^[A-Za-zÀ-ÖØ-öø-ÿ0-9][\s\S]*$/;
    this.isTextAreaValid = regex.test(this.valutazioneDiscorsiva);
    this.isTextAreaLengthValid = this.valutazioneDiscorsiva.length <= 255
    this.updateSubmitButton();
  }

  resetForm(form: NgForm) {
    form.reset();
    this.rating = 0;
    this.isSubmitDisabled = true;
    this.valori.forEach(item => item.selectedOption = '');
    this.valutazioneDiscorsiva = '';
  }

  openPopupSuccess(message: string): void {
    const dialogRef = this.dialog.open(PopupRecensioneComponent, {
      width: '250px',
      data: { message },
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('Il popup è stato chiuso');
    });
  }

  openPopupFail(message: string): void {
    const dialogRef = this.dialog.open(PopupRecensioneFailComponent, {
      width: '250px',
      data: { message },
      disableClose: true,
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('Il popup è stato chiuso');
    });
  }

  inviaRecensione() {

    if (!this.isSubmitDisabled) {

      this.valoriService.creaValoriEcosostenibilitaVisitatore(
        this.valoriEcosostenibilita.politicheAntispreco,
        this.valoriEcosostenibilita.prodottiLocali,
        this.valoriEcosostenibilita.energiaVerde,
        this.valoriEcosostenibilita.raccoltaDifferenziata,
        this.valoriEcosostenibilita.limiteEmissioneCO2,
        this.valoriEcosostenibilita.contattoConNatura,
      ).subscribe((valoreNew) => {

        this.idValori = valoreNew.data.id;

        this.recensioneService.creaRecensione(this.idAttivita, this.rating, this.valutazioneDiscorsiva, this.idValori, this.file!)
          .subscribe((risposta: any) => {
            console.log("Valori eco recensione: ", risposta);
            if (risposta?.status === 'success') {
              this.openPopupSuccess('Recensione inviata con successo!');
            } else {
              this.openPopupSuccess('Errore nell\'\invio della recensione!');
            }
          }, (error) => {
            console.error(error);
          });
      })
    } else {
      if (!this.isRatingSelected) {
        this.openPopupFail('Seleziona almeno una stella!')
      } else if (!this.isQuestionnaireFilled) {
        this.openPopupFail('Compila il questionario!')
      } else if (!this.isTextAreaValid) {
        this.openPopupFail('Il formato della valutazione discorsiva non è corretto!')
      } else if (!this.isTextAreaLengthValid) {
        this.openPopupFail('La lunghezza della valutazione discorsiva supera i 255 caratteri!')
      }
    }
  }
}
