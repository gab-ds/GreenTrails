import { ValoriEcosostenibilitaService } from 'src/app/servizi/valori-ecosostenibilita.service';
import { AttivitaService } from 'src/app/servizi/attivita.service';

import { HttpParams } from '@angular/common/http';
import { Component, Input, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { PopUpAlloggioComponent } from './pop-up-alloggio/pop-up-alloggio.component';
import { MatDialog } from '@angular/material/dialog';
import { PopUpConfermaComponent } from './pop-up-conferma/pop-up-conferma.component';
import { PopUpCategorieComponent } from './pop-up-categorie/pop-up-categorie.component';

export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}


@Component({
  selector: 'app-inserimento-attivita',
  templateUrl: './inserimento-attivita.component.html',
  styleUrls: ['./inserimento-attivita.component.css']
})
export class InserimentoAttivitaComponent implements OnInit {
  inserimento: FormGroup;
  matcher = new MyErrorStateMatcher();
  idPolitiche: any;
  prezzo: any;
  politica1: boolean = false;
  politica2: boolean = false;
  politica3: boolean = false;
  politica4: boolean = false;
  politica5: boolean = false;
  politica6: boolean = false;



  constructor( private formBuilder: FormBuilder,
     private attivitaService: AttivitaService, 
     private dialog: MatDialog,
     private valoriEcosostenibilitaService: ValoriEcosostenibilitaService) {
    this.inserimento = this.formBuilder.group({
      nome:['',[Validators.required,Validators.maxLength(50), Validators.pattern(/^[A-Za-z\s]*$/)]],
      tipo:['',Validators.required],
      categoria:[,Validators.required],
      disponibilita:[0, [Validators.required, Validators.pattern(/^[1-9]\d*$/)]],
      indirizzo: ['',[Validators.required, Validators.pattern(/^[A-Za-z0-9][\s\S]*$/)]],
      cap:['',[Validators.required,Validators.maxLength(5),Validators.pattern(/^[0-9]+$/)]],
      citta: ['',[Validators.required, Validators.pattern(/^[A-Za-z]*$/)]],
      provincia: ['',[Validators.required,Validators.maxLength(2), Validators.pattern(/^[A-Z]{2}$/)]],
      latitudine: ['',[Validators.required, Validators.pattern(/^[-]?([0-8]?[0-9]|90)\.[0-9]{1,15}$/)]],
      longitudine: ['', [Validators.required,Validators.pattern(/^[-]?([0-8]?[0-9]|90)\.[0-9]{1,15}$/)]],
      politicheAntispreco: false,   
      prodottiLocali: false,   
      energiaVerde: false,   
      raccoltaDifferenziata: false,   
      limiteEmissioneCO2: false,   
      contattoConNatura: false,  
      descrizioneBreve:['',[Validators.required,Validators.maxLength(140), Validators.pattern(/^[A-Za-z0-9][\s\S]*$/)]],
      costo:[0, [Validators.required, Validators.pattern(/^[0-9]+(\.[0-9]{1,2})?$/)]],
      descrizioneLunga:['',[Validators.required,Validators.maxLength(2000), Validators.pattern(/^[A-Za-z0-9][\s\S]*$/)]],
      file:['',Validators.required]

    });

  }


  categorieAll=[
    {value: 0, label: 'Hotel'},
    {value: 1, label: 'Bed & Breakfast'},
    {value: 2, label: 'Villaggio Turistico'},
    {value: 3, label: 'Ostello'},
    
  ]
  categorieAtt=[
    {value: 0, label: 'All\'\aperto'},
    {value: 1, label: 'Visite Culturali-Storichr'},
    {value: 2, label: 'Relax'},
    {value: 3, label:'Gastronomia'}
  ]

  ngOnInit(): void {  }

  isTipoFalse(): boolean {
    return this.inserimento.get('tipo')?.value === 'false';
  }

  toppings = new FormControl(false, [Validators.required]);

  //File
  file!: File | null;

  errorMessage: string | null = null;
  onFileSelected(event: any) {
    this.file = event.target.files[0];

    if (this.file!.size > 100 * 1024 * 1024) {
      this.errorMessage = 'La dimensione del file è troppo grande! La dimensione massima del file è 100MB.';
      this.file = null;
      return;
    }    else{
      this.errorMessage = ''
    }

    const fileType = this.file!.type;
    if (!fileType.startsWith('image/') && !fileType.startsWith('video/')) {
      this.errorMessage = 'Puoi selezionare solo file di immagine.';
      this.file = null;
      return;
    }
    else{
      this.errorMessage = ''
    }
  }

  openPopupAlloggio(idAttivita: number):void{
    const dialogRef = this.dialog.open(PopUpAlloggioComponent, {
      width: '60%',
      data: { idAttivita: idAttivita}
    });
    dialogRef.disableClose = true;
  }

  openPopupConferma(message: string):void{
    const dialogRef = this.dialog.open(PopUpConfermaComponent, {
      width: '60%',
      data: { message },
      disableClose: true,
      

    });
    dialogRef.disableClose = true;
  }
  openPopupCategoria(idAttivita: number):void{
    const dialogRef = this.dialog.open(PopUpCategorieComponent, {
      width: '60%',
      data: { idAttivita: idAttivita}
    });
    dialogRef.disableClose = true;
  }

    onSubmit() {
 {     this.valoriEcosostenibilitaService.creaValoriEcosostenibilitaVisitatore(
        this.politica1 =  this.inserimento.get('politicheAntispreco')?.value, 
        this.politica2 =  this.inserimento.get('prodottiLocali')?.value,
        this.politica3 = this.inserimento.get('energiaVerde')?.value,
        this.politica4 = this.inserimento.get('raccoltaDifferenziata')?.value,
        this.politica5 = this.inserimento.get('limiteEmissioneCO2')?.value,
        this.politica6 = this.inserimento.get('contattoConNatura')?.value).subscribe((response) => {
    
    
          this.idPolitiche = response.data.id
    
      
    
     const formData = new FormData()
     formData.append('alloggio', this.inserimento.get('tipo')?.value);
     formData.append('nome', this.inserimento.get('nome')?.value);
     formData.append('disponibilita', this.inserimento.get('disponibilita')?.value)
     if(this.inserimento.get('tipo')?.value === 'true'){
      formData.append('categoriaAlloggio', this.inserimento.get('categoria')?.value);
     }else{
      formData.append('categoriaAttivitaTuristica', this.inserimento.get('categoria')?.value);
     }
     formData.append('indirizzo', this.inserimento.get('indirizzo')?.value);
     formData.append('cap',this.inserimento.get('cap')?.value);
     formData.append('citta', this.inserimento.get('citta')?.value);
     formData.append('provincia', this.inserimento.get('provincia')?.value);
     formData.append('latitudine', this.inserimento.get('latitudine')?.value);
     formData.append('longitudine', this.inserimento.get('longitudine')?.value);
     formData.append('descrizioneBreve', this.inserimento.get('descrizioneBreve')?.value);
     this.prezzo = formData.append('prezzo', this.inserimento.get('costo')?.value);
     formData.append('descrizioneLunga', this.inserimento.get('descrizioneLunga')?.value);
     formData.append('valori', this.idPolitiche);
     formData.append('immagine', this.file!);
     console.log(formData)



   this.attivitaService.inserimentoAttivita( formData)
     .subscribe((response) => {
       console.log('Dati inviati', response)
       if(this.inserimento.get('tipo')?.value === 'true' && response?.status === 'success'){ 
        const idAttivita = response.data.id;
        this.openPopupAlloggio(idAttivita)
            }else if (response?.status === 'success'){
              const idAttivita = response.data.id;
        this.openPopupCategoria(idAttivita)         
           }
           else{
            const errorMessage = response?.error?.message || 'Errore sconosciuto';
            this.openPopupConferma(errorMessage);
           }

             },
             (error) =>{
              this.openPopupConferma(error.error.data)
             });

            });
    
   }}
}