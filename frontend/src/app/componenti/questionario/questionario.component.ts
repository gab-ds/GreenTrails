import { UtenteService } from './../../servizi/utente.service';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PopUpQuestionarioComponent } from './pop-up-questionario/pop-up-questionario.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-questionario',
  templateUrl: './questionario.component.html',
  styleUrls: ['./questionario.component.css']
})
export class QuestionarioComponent implements OnInit {
  questionario: FormGroup
  mostraRisultati = false;

  constructor(private formBuilder: FormBuilder, private utenteService: UtenteService, private router: Router, private dialog: MatDialog) {
    this.questionario = this.formBuilder.group({
      viaggioPreferito: ['', Validators.required],
      alloggioPreferito: ['', Validators.required],
      attivitaPreferita: ['', Validators.required],
      preferenzaAlimentare: ['', Validators.required],
      animaleDomestico: ['', Validators.required],
      budgetPreferito: ['', Validators.required],
      souvenir: ['', Validators.required],
      stagioniPreferite: ['', Validators.required],



    });
  }

  ngOnInit(): void {
  }

  openPopupQuestionario(message: string): void {
    const dialogRef = this.dialog.open(PopUpQuestionarioComponent,
      {
        width: '250px',
        data: { message },
        disableClose: true,
      })
  }

  esci() {

    this.router.navigate(['/areaRiservata']);

  }
  
  invio() {
    const nonSelectedFields = [];
  
    if (!this.questionario.get('viaggioPreferito')?.value) {
      nonSelectedFields.push('prima domanda!');
    }
  
    if (!this.questionario.get('alloggioPreferito')?.value) {
      nonSelectedFields.push('seconda domanda!');
    }

    if (!this.questionario.get('preferenzaAlimentare')?.value) {
      nonSelectedFields.push('terza domanda!');
    }
  
    if (!this.questionario.get('attivitaPreferita')?.value) {
      nonSelectedFields.push('quarta domanda!');
    }
  
    if (!this.questionario.get('animaleDomestico')?.value) {
      nonSelectedFields.push('quinta domanda!');
    }
  
    if (!this.questionario.get('budgetPreferito')?.value) {
      nonSelectedFields.push('sesta domanda!');
    }
  
    if (!this.questionario.get('souvenir')?.value) {
      nonSelectedFields.push('penultima domanda!');
    }
  
    if (!this.questionario.get('stagioniPreferite')?.value) {
      nonSelectedFields.push('ultima domanda! ');
    }
  
    if (nonSelectedFields.length === 0) {
      if (this.questionario.valid) {
        this.utenteService.invioQuestionario(
          this.questionario.get('viaggioPreferito')?.value,
          this.questionario.get('alloggioPreferito')?.value,
          this.questionario.get('attivitaPreferita')?.value,
          this.questionario.get('preferenzaAlimentare')?.value,
          this.questionario.get('animaleDomestico')?.value,
          this.questionario.get('budgetPreferito')?.value,
          this.questionario.get('souvenir')?.value,
          this.questionario.get('stagioniPreferite')?.value,
        ).subscribe(
          (response) => {
            this.openPopupQuestionario('Preferenze inviate');
          },
          (error) => {
            this.openPopupQuestionario('Preferenze non inviate');
          }
        );
      } else {
        this.openPopupQuestionario('Compilare correttamente tutti i campi');
      }
    } else {
      let errorMessage;
      if (nonSelectedFields.length === 1) {
        errorMessage = `Il questionario non è stato salvato con successo perché il visitatore non ha indicato alcuna 
        preferenza alla ${nonSelectedFields.join(', ')}`;
      } else {
        errorMessage = 'Il questionario non è stato salvato con successo, perché non è stata indicata alcuna preferenza a più domande!';
      }
      this.openPopupQuestionario(errorMessage);
    }
    this.router.navigate(['/areaRiservata']);

  }
  

  invioA() {


    this.utenteService.invioQuestionario(
      this.questionario.get('viaggioPreferito')?.value,
      this.questionario.get('alloggioPreferito')?.value,
      this.questionario.get('attivitaPreferita')?.value,
      this.questionario.get('preferenzaAlimentare')?.value,
      this.questionario.get('animaleDomestico')?.value,
      this.questionario.get('budgetPreferito')?.value,
      this.questionario.get('souvenir')?.value,
      this.questionario.get('stagioniPreferite')?.value,
    ).subscribe(
      (response) => {
        this.openPopupQuestionario('Preferenze inviate')
      },
      (erorr) => {
        this.openPopupQuestionario('Preferenze non inviate')
      }
    )
  }

}
