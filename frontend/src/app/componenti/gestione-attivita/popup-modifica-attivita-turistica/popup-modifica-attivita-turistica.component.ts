import { ChangeDetectorRef, Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { PopupModificaAlloggioComponent } from '../popup-modifica-alloggio/popup-modifica-alloggio.component';
import { AttivitaService } from 'src/app/servizi/attivita.service';
import { PopupConfermaModificaComponent } from '../popup-conferma-modifica/popup-conferma-modifica.component';
import { PopupModificaCategorieComponent } from '../popup-modifica-categorie/popup-modifica-categorie.component';

export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}

@Component({
  selector: 'app-popup-modifica',
  templateUrl: './popup-modifica-attivita-turistica.component.html',
  styleUrls: ['./popup-modifica-attivita-turistica.component.css']
})
export class PopupModificaComponent implements OnInit {

  inserimento: FormGroup;
  matcher = new MyErrorStateMatcher();

  nome: any
  tipo: any
  categoria: any
  disponibilita: any
  indirizzo: any
  cap: any
  citta: any
  provincia: any
  latitudine: any
  longitudine: any
  descrizioneBreve: any
  prezzo: any
  descrizioneLunga: any

  isTipoSelected!: boolean;
  isCategoriaSelected!: boolean;
  isNome: boolean;
  isIndirizzo: boolean;
  isCap: boolean;
  isCitta: boolean;
  isProvincia: boolean;
  isLatitudine: boolean;
  isLongitudine: boolean;
  isDescrizioneBreve: boolean;
  isDescrizioneLunga: boolean;
  isDisponibilita: boolean;
  isPrezzo: boolean;


  constructor(
    private formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<PopupModificaComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private dialog: MatDialog,
    private attivitaService: AttivitaService
  ) {
    this.nome = this.data.nome
    this.tipo = this.data.tipo
    this.categoria = this.data.categoria
    this.disponibilita = this.data.disponibilita
    this.indirizzo = this.data.indirizzo
    this.cap = this.data.cap
    this.citta = this.data.citta
    this.provincia = this.data.provincia
    this.latitudine = this.data.latitudine
    this.longitudine = this.data.longitudine
    this.descrizioneBreve = this.data.descrizioneBreve
    this.prezzo = this.data.prezzo
    this.descrizioneLunga = this.data.descrizioneLunga

    this.inserimento = this.formBuilder.group({
      nome: [this.nome],
      tipo: [],
      categoria: [],
      disponibilita: [this.disponibilita, [Validators.pattern(/^[0-9]+$/)]],
      indirizzo: [this.indirizzo],
      cap: [this.cap, [Validators.maxLength(5), Validators.pattern(/^[0-9]+$/)]],
      citta: [this.citta],
      provincia: [this.provincia, Validators.maxLength(2)],
      latitudine: [this.latitudine, [Validators.pattern(/^[-]?([0-8]?[0-9]|90)\.[0-9]{1,15}$/)]],
      longitudine: [this.longitudine, [Validators.pattern(/^[-]?([0-8]?[0-9]|90)\.[0-9]{1,15}$/)]],
      descrizioneBreve: [this.descrizioneBreve],
      costo: [this.prezzo, [Validators.pattern(/^[0-9]+(\.[0-9]{1,2})?$/)]],
      descrizioneLunga: [this.descrizioneLunga],
    });

    this.isNome = this.nome.trim().length > 0;
    this.isDisponibilita = this.disponibilita.toString().trim().length > 0 && /^[0-9]+$/.test(this.disponibilita.toString());
    this.isIndirizzo = this.indirizzo.trim().length > 0;
    this.isCap = this.cap.trim().length > 0 && this.cap.trim().length <= 5 && /^[0-9]+$/.test(this.cap);
    this.isCitta = this.citta.trim().length > 0;
    this.isProvincia = this.provincia.trim().length > 0 && this.provincia.trim().length <= 2;
    this.isLatitudine = this.latitudine.toString().trim().length > 0 && /^[-+]?([1-8]?\d(\.\d+)?|90(\.0+)?)$/.test(this.latitudine.toString());
    this.isLongitudine = this.longitudine.toString().trim().length > 0 && /^[-+]?(180(\.0+)?|((1[0-7]\d)|([1-9]?\d))(\.\d+)?)$/.test(this.longitudine.toString());
    this.isDescrizioneBreve = this.descrizioneBreve.trim().length > 0;
    this.isPrezzo = this.prezzo.toString().trim().length > 0 && /^[0-9]+(\.[0-9]{1,2})?$/.test(this.prezzo.toString());
    this.isDescrizioneLunga = this.descrizioneLunga.trim().length > 0;

  }

  checkValidity(): boolean {
    console.log('isTipoSelected:', this.isTipoSelected);
    console.log('isCategoriaSelected:', this.isCategoriaSelected);
    console.log('isNome:', this.isNome);
    console.log('isDisponibilita:', this.isDisponibilita);
    console.log('isIndirizzo:', this.isIndirizzo);
    console.log('isCap:', this.isCap);
    console.log('isCitta:', this.isCitta);
    console.log('isProvincia:', this.isProvincia);
    console.log('isLatitudine:', this.isLatitudine);
    console.log('isLongitudine:', this.isLongitudine);
    console.log('isDescrizioneBreve:', this.isDescrizioneBreve);
    console.log('isPrezzo:', this.isPrezzo);
    console.log('isDescrizioneLunga:', this.isDescrizioneLunga);

    return this.isTipoSelected && this.isCategoriaSelected && this.isNome && this.isDisponibilita && this.isIndirizzo && this.isCap && this.isCitta && this.isProvincia && this.isLatitudine && this.isLongitudine && this.isDescrizioneBreve && this.isPrezzo && this.isDescrizioneLunga;
  }



  ngOnInit(): void {
  }

  // categorieAll = [
  //   { value: 0, label: 'Hotel' },
  //   { value: 1, label: 'Bed & Breakfast' },
  //   { value: 2, label: 'Villaggio Turistico' },
  //   { value: 3, label: 'Ostello' },
  // ];

  categorieAtt = [
    { value: 0, label: 'All\'aperto' },
    { value: 1, label: 'Visite Culturali-Storiche' },
    { value: 2, label: 'Relax' },
    { value: 3, label: 'Gastronomia' }
  ];

  isTipoFalse(): boolean {
    return this.inserimento.get('tipo')?.value === 'false';
  }

  toppings = new FormControl(false, []);
  errorMessage: string | null = null;

  openPopupAlloggio(idAttivita: number): void {
    const dialogRef = this.dialog.open(PopupModificaAlloggioComponent, {
      width: '60%',
      data: { idAttivita: idAttivita },
      disableClose: true
    });
  }

  openPopupConferma(message: string): void {
    const dialogRef = this.dialog.open(PopupConfermaModificaComponent, {
      width: '60%',
      data: { message },
      disableClose: true,
    });
  }

  openPopupCategoria(idAttivita: number):void{
    const dialogRef = this.dialog.open(PopupModificaCategorieComponent, {
      width: '60%',
      data: { idAttivita: idAttivita}
    });
  }

  onSubmit() {

    console.log("id:", this.data.id)
    console.log("isAlloggio", this.data.tipo)
    console.log("nome:", this.data.nome)
    console.log("indirizzo:", this.data.indirizzo)
    console.log("cap:", this.data.cap)
    console.log("citta:", this.data.citta)
    console.log("provincia:", this.data.provincia)
    console.log("latitudine:", this.data.latitudine)
    console.log("longitudine:", this.data.longitudine)
    console.log("descrizioneBreve:", this.data.descrizioneBreve)
    console.log("descrizioneLunga:", this.data.descrizioneLunga)
    console.log("valori:", this.data.valori)
    console.log("prezzo:", this.data.prezzo)
    console.log("disponibilita:", this.data.disponibilita)
    // console.log("categoriaAlloggio:", this.data.categoriaAlloggio)
    console.log("categoriaAttivitaTuristica:", this.data.categoriaAttivitaTuristica)

    const formData = new FormData();

    const nomeValue = this.inserimento.get('nome')?.value;
    console.log("NOME: ", nomeValue);
    formData.append('nome', nomeValue);

    const disponibilitaValue = this.inserimento.get('disponibilita')?.value;
    console.log("DISPONIBILITA: ", disponibilitaValue);
    formData.append('disponibilita', disponibilitaValue);

    // const tipoValue = this.inserimento.get('tipo')?.value;
    // if (tipoValue === 'true') {
    //   const categoriaAlloggioValue = this.inserimento.get('categoria')?.value;
    //   console.log("CATEGORIA ALLOGGIO: ", categoriaAlloggioValue);
    //   formData.append('categoriaAlloggio', categoriaAlloggioValue);
    // } else {
    //   const categoriaAttivitaTuristicaValue = this.inserimento.get('categoria')?.value;
    //   console.log("CATEGORIA ATTIVITA TURISTICA: ", categoriaAttivitaTuristicaValue);
    //   formData.append('categoriaAttivitaTuristica', categoriaAttivitaTuristicaValue);
    // }

    const categoriaAttivitaTuristicaValue = this.inserimento.get('categoria')?.value;
    console.log("CATEGORIA ATTIVITA TURISTICA: ", categoriaAttivitaTuristicaValue);
    formData.append('categoriaAttivitaTuristica', categoriaAttivitaTuristicaValue);

    const indirizzoValue = this.inserimento.get('indirizzo')?.value;
    console.log("INDIRIZZO: ", indirizzoValue);
    formData.append('indirizzo', indirizzoValue);

    const capValue = this.inserimento.get('cap')?.value;
    console.log("CAP: ", capValue);
    formData.append('cap', capValue);

    const cittaValue = this.inserimento.get('citta')?.value;
    console.log("CITTA: ", cittaValue);
    formData.append('citta', cittaValue);

    const provinciaValue = this.inserimento.get('provincia')?.value;
    console.log("PROVINCIA: ", provinciaValue);
    formData.append('provincia', provinciaValue);

    const latitudineValue = this.inserimento.get('latitudine')?.value;
    console.log("LATITUDINE: ", latitudineValue);
    formData.append('latitudine', latitudineValue);

    const longitudineValue = this.inserimento.get('longitudine')?.value;
    console.log("LONGITUDINE: ", longitudineValue);
    formData.append('longitudine', longitudineValue);

    const descrizioneBreveValue = this.inserimento.get('descrizioneBreve')?.value;
    console.log("DESCRIZIONE BREVE: ", descrizioneBreveValue);
    formData.append('descrizioneBreve', descrizioneBreveValue);

    const prezzoValue = this.inserimento.get('costo')?.value;
    console.log("PREZZO: ", prezzoValue);
    formData.append('prezzo', prezzoValue);

    const descrizioneLungaValue = this.inserimento.get('descrizioneLunga')?.value;
    console.log("DESCRIZIONE LUNGA: ", descrizioneLungaValue);
    formData.append('descrizioneLunga', descrizioneLungaValue);

    const valori = this.data.valori;
    formData.append('valori', valori);

    console.log(formData);


    this.attivitaService.modificaAttivita(this.data.id, formData)
      .subscribe((risposta) => {
        console.log(risposta);

        if (this.inserimento.get('tipo')?.value === 'true' && risposta?.status === 'success') {
          const idAttivita = risposta.data.id;
          // this.openPopupAlloggio(idAttivita)
        } else if (risposta?.status === 'success') {
          this.openPopupConferma('Attivita modificata con successo')
        }
        else {
          const errorMessage = risposta?.error?.message || 'Errore sconosciuto';
          this.openPopupConferma(errorMessage);
        }
      }, (error) => {
        console.error(error);
      })


  }
}
