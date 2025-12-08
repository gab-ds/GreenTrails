import { CamereService } from './../../../servizi/camere.service';
import { Component, OnInit, ViewChild } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, FormGroupDirective, NgForm, PatternValidator, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { PopUpPrenotazioneComponent } from '../pop-up-prenotazione/pop-up-prenotazione.component';
import { PrenotazioniAlloggioService } from 'src/app/servizi/prenotazioni-alloggio.service';
import { ItinerariService } from 'src/app/servizi/itinerari.service';
import { PopUpErroriComponent } from './pop-up-errori/pop-up-errori.component';
import { MatStepper } from '@angular/material/stepper';
export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}

@Component({
  selector: 'app-prenotazioni',
  templateUrl: './prenotazioni.component.html',
  styleUrls: ['./prenotazioni.component.css']
})


export class PrenotazioniComponent implements OnInit {

  @ViewChild('stepper')
  stepper!: MatStepper;
  
  camereOptions: { id: number, tipoCamera: string, capienza: any }[] = [];
  capienzaSelezione: number = 0;
  idAttivita: number = 0;

  selectFormControl = new FormControl('', [Validators.required]);
  nativeSelectFormControl = new FormControl('', [Validators.required]);
  matcher = new MyErrorStateMatcher();
  idItinerario: any;

  firstFormGroup: FormGroup;
  secondFormGroup = this._formBuilder.group({ secondCtrl: '', });
  isOptional = false;

  disponibilita: any;
  isDisponibile: boolean = false;

  capienza: any;
  isCapienza: boolean = false;

  siClicked = false;
  creaClicked = false;
  azioneEseguita = false;


  arrivo: any;
  partenza: any;
  adulti: any;
  bambini: any;
  numcamere: any;
  idcamera: any

  formValid: boolean = false;
  numAdultiValid: boolean = false;
numBambiniValid: boolean = false;
numCamereValid: boolean = false;
tipoCamereValid: boolean = false;

  private formatDate(date: Date): string {
    
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, '0');
    const day = date.getDate().toString().padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  constructor(private prenotazioniAlloggioService: PrenotazioniAlloggioService,
    private itinerariService: ItinerariService,
    private dialog: MatDialog,
    private camereService: CamereService,
    private _formBuilder: FormBuilder) {
    this.firstFormGroup = this._formBuilder.group({
      arrivo: [''],
      partenza: [''],
      numAdulti: [''],
      numBambini: [''],
      numCamere: [''],
      idCamera: ['',Validators.required]
    });
    this.idItinerario = this.itinerariService.getidItinerario();
  }
  ngOnInit(): void {
    this.idItinerario = localStorage.getItem('idItinerario');
    this.itinerariService.currentId.subscribe(id => {
      this.idAttivita = id;
      this.idItinerario = localStorage.getItem('idItinerario');
    });

    this.camereService.getCamereDisponibili(this.idAttivita).subscribe((data) => {

      if (data.data && Array.isArray(data.data)) {
        data.data.forEach((element: { id: any; tipoCamera: any; capienza: any }) => {
          const cameraId = element.id;
          const tipoCamera = element.tipoCamera;
          const capienzaCamera = element.capienza;

          this.camereOptions.push({ id: cameraId, tipoCamera: tipoCamera, capienza: capienzaCamera });
        });

      }
    });
  }

  //POP-UP CONFERMA
  openPopupPrenotazione(message: string): void {
    const dialogRef = this.dialog.open(PopUpPrenotazioneComponent, {
      width: '250px',
      data: { message },
      disableClose: true,
    });

  }

    //POP-UP EROORE
    openPopupErrori(message: string): void {
      const dialogRef = this.dialog.open(PopUpErroriComponent, {
        width: '250px',
        data: { message },
        disableClose: true,
      });

      dialogRef.afterClosed().subscribe(() => {
        // Riporta al primo step
        this.stepper.selectedIndex = 0;
      });
    
  
    }

    updateFormValid(){

      this.numAdultiValid = this.firstFormGroup.get('numAdulti')?.value > 0;
      this.numBambiniValid = this.firstFormGroup.get('numBambini')?.value >= 0;
      this.numCamereValid = this.firstFormGroup.get('numCamere')?.value > 0;
      this.tipoCamereValid = this.firstFormGroup.get('idCamera')!.valid

      return this.numAdultiValid && this.numBambiniValid && this.numCamereValid && this.tipoCamereValid
    }

  //VERIFICA DISPONIBILITA
  verificaDisponibilita() {
    const arrivoDate = this.firstFormGroup.get('arrivo')?.value;
const partenzaDate = this.firstFormGroup.get('partenza')?.value;

if (!arrivoDate || !partenzaDate) {
  this.openPopupErrori('Formato data non valido');
  return;
}

    const formData = {
      arrivo: this.formatDate(this.firstFormGroup.get('arrivo')?.value),
      partenza: this.formatDate(this.firstFormGroup.get('partenza')?.value),
      idCamera: this.firstFormGroup.get('idCamera')?.value,
      numCamere: this.firstFormGroup.get('numCamere')?.value,
      numBambini: this.firstFormGroup.get('numBambini')?.value,
      numAdulti: this.firstFormGroup.get('numAdulti')?.value
    };

    this.arrivo = formData.arrivo;
    this.partenza = formData.partenza;
    this.idcamera = formData.idCamera;
    this.numcamere = parseInt(formData.numCamere);
    this.bambini = parseInt(formData.numBambini);
    this.adulti = parseInt(formData.numAdulti);

    console.log("ARRIVO", this.arrivo)
    console.log("PARTENZA", this.partenza)
    console.log("IDCAMERA", this.idcamera)
    console.log("NUMCAMERE", this.numcamere)
    console.log("BAMBINI", this.bambini)
    console.log("ADULTI", formData.numAdulti)

    const oggi = new Date();


console.log('Numero adulti valido:',this.numAdultiValid)
    const cameraSelezionata = this.camereOptions.find(camera => camera.id === formData.idCamera);



    if(this.updateFormValid()
    &&arrivoDate && partenzaDate 
   && arrivoDate >= oggi
  && partenzaDate > arrivoDate)
    {
      
      
      this.prenotazioniAlloggioService.verificaDisponibilitaAlloggio(
      formData.idCamera,
      formData.arrivo,
      formData.partenza).subscribe(
        (response) => {

          console.log(response.data)
          console.log(this.firstFormGroup.get('numCamere')?.value)
          this.disponibilita = this.firstFormGroup.get('numCamere')?.value <= response.data ? 'Disponibile' : 'Non disponibile';
          this.isDisponibile = this.firstFormGroup.get('numCamere')?.value <= response.data ? true : false;
          if (cameraSelezionata) {
            this.capienzaSelezione = cameraSelezionata.capienza;
            console.log('Capienza', this.capienzaSelezione);
            console.log('Adulti',this.firstFormGroup.get('numAdulti')?.value);
            console.log('Bambini:',this.firstFormGroup.get('numBambini')?.value);
            console.log('Camere:',this.firstFormGroup.get('numCamere')?.value);

            console.log('Somma:', this.adulti  + this.bambini)

            console.log('Capienza totale:',this.capienzaSelezione * this.firstFormGroup.get('numCamere')?.value)
            console.log()
            this.capienza = this.adulti  + this.bambini <= this.capienzaSelezione * this.firstFormGroup.get('numCamere')?.value ? 'Capienza' : 'Non capienza';
            this.isCapienza = this.adulti  + this.bambini <= this.capienzaSelezione * this.firstFormGroup.get('numCamere')?.value ? true : false;

          }
        })}else if (arrivoDate && arrivoDate < oggi) {
          this.openPopupErrori('La data di arrivo non può essere precedente alla data odierna');
        } else if (arrivoDate && partenzaDate && partenzaDate < arrivoDate) {
          this.openPopupErrori('La data di partenza non può essere precedente alla data arrivo');
        } else if (!this.numAdultiValid) {
          this.openPopupErrori('Il numero di adulti deve essere almeno 1');
        } else if (!this.numBambiniValid) {
          this.openPopupErrori('Il numero di bambini non può essere inferiore a 0');
        } else if (!this.numCamereValid) {
          this.openPopupErrori('Il numero di camere non può essere inferiore a 1');
        } else if (!this.tipoCamereValid) {
          this.openPopupErrori('Selezionare almeno un tipo di camera');
        }

  }

  //AGGIUNGI ATTIVITA ALL'ITINERARIO
  aggiungiAllItinerario() {
    this.siClicked = false;
    this.creaClicked = true;
    this.azioneEseguita = true;
  }

  // CREAZIONE ITINERARIO
  creaItinerario() {
    this.itinerariService.creaItinerari().subscribe((response) => {
      const idItinerario = response.data.id;
      localStorage.setItem('idItinerario', idItinerario.toString());
      this.idItinerario = idItinerario;
      this.siClicked = false;
      this.creaClicked = true;
      this.azioneEseguita = true;
    });
  }

  // INVIO DEI DATI
  onSubmit1() {
    const formData = {
      arrivo: this.formatDate(this.firstFormGroup.get('arrivo')?.value),
      partenza: this.formatDate(this.firstFormGroup.get('partenza')?.value),
      numAdulti: this.firstFormGroup.get('numAdulti')?.value,
      numBambini: this.firstFormGroup.get('numBambini')?.value,
      idItinerario: this.idItinerario,
      numCamere: this.firstFormGroup.get('numCamere')?.value,
      idCamera: this.firstFormGroup.get('idCamera')?.value
    };

    this.prenotazioniAlloggioService.prenotazioneAlloggio(
      this.idItinerario,
      this.firstFormGroup.get('idCamera')?.value,
      formData.numAdulti,
      formData.numBambini,
      formData.numCamere,
      formData.arrivo,
      formData.partenza).subscribe(
        (response) => {
          console.log('Dati inviati', response);
          if (response?.status === 'success') {
            this.openPopupPrenotazione('Prenotazione inviata');
          } else {
            this.openPopupPrenotazione('Impossibile effettuare la prenotazione')
          }
        }
      )
  }

}