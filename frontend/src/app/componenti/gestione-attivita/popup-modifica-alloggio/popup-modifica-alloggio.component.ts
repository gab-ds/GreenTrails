import { AfterContentChecked, AfterContentInit, AfterViewChecked, AfterViewInit, Component, DoCheck, Inject, OnChanges, OnDestroy, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { CamereService } from 'src/app/servizi/camere.service';
import { PopupConfermaModificaComponent } from '../popup-conferma-modifica/popup-conferma-modifica.component';
import { PopupEliminazioneCameraComponent } from '../popup-eliminazione-camera/popup-eliminazione-camera.component';

@Component({
  selector: 'app-popup-modifica-alloggio',
  templateUrl: './popup-modifica-alloggio.component.html',
  styleUrls: ['./popup-modifica-alloggio.component.css']
})
export class PopupModificaAlloggioComponent implements OnInit {

  camere: FormGroup
  id: any
  camereInserite: any[] = []
  eliminazioneConfermata: boolean = false;

  isCapienza!: boolean
  isPrezzo!: boolean;
  isCategoria!: boolean;
  isDescrizione!: boolean;
  isDisponibilita!: boolean;

  deletedCameraIds: number[] = [];

  constructor(public dialogRef: MatDialogRef<PopupModificaAlloggioComponent>, @Inject(MAT_DIALOG_DATA) public data: any,
    private formBuilder: FormBuilder,
    private camereService: CamereService, private dialog: MatDialog) {
    this.camere = this.formBuilder.group({
      capienza: ['', [Validators.required, Validators.pattern(/^[0-9]+$/)]],
      prezzo: ['', [Validators.required, Validators.pattern(/^[0-9]+(\.[0-9]{1,2})?$/)]],
      tipoCamera: ['', Validators.required],
      descrizione: ['', Validators.required],
      disponibilita: ['', [Validators.required, Validators.pattern(/^[0-9]+$/)]],

    });

  }

  checkValidity(): boolean {
    const capienzaPattern = /^[0-9]+$/;
    const prezzoPattern = /^[0-9]+(\.[0-9]{1,2})?$/;
    const disponibilitaPattern = /^[0-9]+$/;

    this.isCapienza = this.camere.get('capienza')!.value.trim().length > 0 && capienzaPattern.test(this.camere.get('capienza')!.value);
    this.isPrezzo = this.camere.get('prezzo')!.value.trim().length > 0 && prezzoPattern.test(this.camere.get('prezzo')!.value);
    this.isCategoria = this.camere.get('tipoCamera')!.value.trim().length > 0;
    this.isDescrizione = this.camere.get('descrizione')!.value.trim().length > 0;
    this.isDisponibilita = this.camere.get('disponibilita')!.value.trim().length > 0 && disponibilitaPattern.test(this.camere.get('disponibilita')!.value);

    return this.isCapienza && this.isPrezzo && this.isCategoria && this.isDescrizione && this.isDisponibilita;
  }


  ngOnInit(): void {
    console.log('Attivita creata:', this.data.idAttivita);
    console.log(this.data.idAttivita)
    this.id = this.data.idAttivita
    this.visualizzaCamerePerAlloggio();
  }

  onNoClick(): void {
    this.dialogRef.close();
    window.location.reload();
  }

  openPopupConferma(message: string): void {
    const dialogRef = this.dialog.open(PopupConfermaModificaComponent, {
      width: '60%',
      data: { message },
      disableClose: true,

    });
  }

  aggiungiCamera() {
    const camera = {
      categoria: this.camere.get('tipoCamera')!.value,
      disponibilita: this.camere.get('disponibilita')?.value,
      descrizione: this.camere.get('descrizione')?.value,
      capienza: this.camere.get('capienza')?.value,
      prezzo: this.camere.get('prezzo')?.value
    };

    this.camereInserite.push(camera);

    this.camereService.inserimentoCamere(
      this.id,
      this.camere.get('tipoCamera')?.value,
      this.camere.get('disponibilita')?.value,
      this.camere.get('descrizione')?.value,
      this.camere.get('capienza')?.value,
      this.camere.get('prezzo')?.value


    ).subscribe((response) => {
    })
  }

  inviaCamere() {
    if (this.camereInserite.length > 0) {
      this.openPopupConferma('Alloggio modificato con successo');
    } else {

    }
  }

  visualizzaCamerePerAlloggio() {
    this.camereService.visualizzaCamerePerAlloggio(this.id).subscribe((risposta) => {
      console.log("Camere per alloggio n." + this.id, risposta);

      risposta.data.forEach((item: any) => {
        this.camereInserite.push(item);
        console.log(item)
      });
    })
  }

  delete(id: number): void {

    this.camereService.visualizzaCamera(id).subscribe((risposta) => {
      console.log("Camera con id: " + id, risposta);

      const dialogRef = this.dialog.open(PopupEliminazioneCameraComponent, {
        data: {
          message: 'Camera eliminata con successo',
          id: id,
          capienza: risposta.data.capienza,
          prezzo: risposta.data.prezzo,
          categoria: risposta.data.tipoCamera,
          descirzione: risposta.data.descrizione,
          disponibilita: risposta.data.disponibilita
        },
        disableClose: true
      });
      this.deletedCameraIds.push(id);
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
        }
      }, (error) => {
        console.log(error);
      });
    });
  }

}
