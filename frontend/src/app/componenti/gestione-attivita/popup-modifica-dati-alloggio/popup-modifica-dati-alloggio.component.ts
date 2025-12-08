import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormControl } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { AttivitaService } from 'src/app/servizi/attivita.service';
import { PopupConfermaModificaComponent } from '../popup-conferma-modifica/popup-conferma-modifica.component';
import { MyErrorStateMatcher, PopupModificaComponent } from '../popup-modifica-attivita-turistica/popup-modifica-attivita-turistica.component';
import { PopupModificaAlloggioComponent } from '../popup-modifica-alloggio/popup-modifica-alloggio.component';
import { PopupModificaCategorieComponent } from '../popup-modifica-categorie/popup-modifica-categorie.component';

@Component({
  selector: 'app-popup-modifica-dati-alloggio',
  templateUrl: './popup-modifica-dati-alloggio.component.html',
  styleUrls: ['./popup-modifica-dati-alloggio.component.css']
})
export class PopupModificaDatiAlloggioComponent implements OnInit {
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

  isNome: boolean;
  isTipoSelected!: boolean;
  isCategoriaSelected!: boolean;
  isIndirizzo: boolean;
  isCap: boolean;
  isCitta: boolean;
  isProvincia: boolean;
  isLatitudine: boolean;
  isLongitudine: boolean;
  isDescrizioneBreve: boolean;
  isDescrizioneLunga: boolean;

  constructor(
    private formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<PopupModificaComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private dialog: MatDialog,
    private attivitaService: AttivitaService
  ) {
    this.nome = this.data.nome;
    this.tipo = this.data.tipo;
    this.categoria = this.data.categoria;
    this.disponibilita = this.data.disponibilita;
    this.indirizzo = this.data.indirizzo;
    this.cap = this.data.cap;
    this.citta = this.data.citta;
    this.provincia = this.data.provincia;
    this.latitudine = this.data.latitudine;
    this.longitudine = this.data.longitudine;
    this.descrizioneBreve = this.data.descrizioneBreve;
    this.descrizioneLunga = this.data.descrizioneLunga;

    this.inserimento = this.formBuilder.group({
      nome: [this.nome],
      tipo: [Validators.required],
      categoria: [Validators.required],
      indirizzo: [this.indirizzo],
      cap: [this.cap, [Validators.maxLength(5), Validators.pattern(/^[0-9]+$/)]],
      citta: [this.citta],
      provincia: [this.provincia, Validators.maxLength(2)],
      latitudine: [this.latitudine, [Validators.pattern(/^[-]?([0-8]?[0-9]|90)\.[0-9]{1,15}$/)]],
      longitudine: [this.longitudine, [Validators.pattern(/^[-]?([0-8]?[0-9]|90)\.[0-9]{1,15}$/)]],
      descrizioneBreve: [this.descrizioneBreve],
      descrizioneLunga: [this.descrizioneLunga],
    });

    this.isNome = this.nome.trim().length > 0;
    this.isIndirizzo = this.indirizzo.trim().length > 0;
    this.isCap = this.cap.trim().length > 0 && this.cap.trim().length <= 5 && /^[0-9]+$/.test(this.cap);
    this.isCitta = this.citta.trim().length > 0;
    this.isProvincia = this.provincia.trim().length > 0 && this.provincia.trim().length <= 2;
    this.isLatitudine = this.latitudine.toString().trim().length > 0 && /^[-+]?([1-8]?\d(\.\d+)?|90(\.0+)?)$/.test(this.latitudine.toString());
    this.isLongitudine = this.longitudine.toString().trim().length > 0 && /^[-+]?(180(\.0+)?|((1[0-7]\d)|([1-9]?\d))(\.\d+)?)$/.test(this.longitudine.toString());
    this.isDescrizioneBreve = this.descrizioneBreve.trim().length > 0;
    this.isDescrizioneLunga = this.descrizioneLunga.trim().length > 0;

    console.log(this.data.prezzo);
  }

  checkValidity(): boolean {
    console.log('isTipoSelected:', this.isTipoSelected);
    console.log('isCategoriaSelected:', this.isCategoriaSelected);
    console.log('isNome:', this.isNome);
    console.log('isIndirizzo:', this.isIndirizzo);
    console.log('isCap:', this.isCap);
    console.log('isCitta:', this.isCitta);
    console.log('isProvincia:', this.isProvincia);
    console.log('isLatitudine:', this.isLatitudine);
    console.log('isLongitudine:', this.isLongitudine);
    console.log('isDescrizioneBreve:', this.isDescrizioneBreve);
    console.log('isDescrizioneLunga:', this.isDescrizioneLunga);

    return this.isTipoSelected && this.isCategoriaSelected && this.isNome && this.isIndirizzo && this.isCap && this.isCitta && this.isProvincia && this.isLatitudine && this.isLongitudine && this.isDescrizioneBreve && this.isDescrizioneLunga;
  }

  ngOnInit(): void {
  }

  categorieAll = [
    { value: 0, label: 'Hotel' },
    { value: 1, label: 'Bed & Breakfast' },
    { value: 2, label: 'Villaggio Turistico' },
    { value: 3, label: 'Ostello' },
  ];

  // categorieAtt = [
  //   { value: 0, label: 'All\'aperto' },
  //   { value: 1, label: 'Visite Culturali-Storiche' },
  //   { value: 2, label: 'Relax' },
  //   { value: 3, label: 'Gastronomia' }
  // ];

  isTipoFalse(): boolean {
    return this.inserimento.get('tipo')?.value === 'false';
  }

  toppings = new FormControl(false, [Validators.required]);
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

  openPopupCategoria(idAttivita: number): void {
    const dialogRef = this.dialog.open(PopupModificaCategorieComponent, {
      width: '60%',
      data: { idAttivita: idAttivita }
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
    console.log("categoriaAlloggio:", this.data.categoriaAlloggio)
    console.log("categoriaAttivitaTuristica:", this.data.categoriaAttivitaTuristica)

    const formData = new FormData();

    const nomeValue = this.inserimento.get('nome')?.value;
    console.log("NOME: ", nomeValue);
    formData.append('nome', nomeValue);


    formData.append('disponibilita', '0');

    const categoriaAlloggioValue = this.inserimento.get('categoria')?.value;
    console.log("CATEGORIA ALLOGGIO: ", categoriaAlloggioValue);
    formData.append('categoriaAlloggio', categoriaAlloggioValue);

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

    formData.append('prezzo', '0');

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
          this.openPopupAlloggio(idAttivita)
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
