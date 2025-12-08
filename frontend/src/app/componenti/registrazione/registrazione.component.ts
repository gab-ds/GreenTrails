import { UtenteService } from './../../servizi/utente.service';
import { HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import {  AbstractControl, FormControl, FormGroupDirective, NgForm, Validators } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PopUpRegistrazioneComponent } from './pop-up-registrazione/pop-up-registrazione.component';

export class MyErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}

@Component({
  selector: 'app-registrazione',
  templateUrl: './registrazione.component.html',
  styleUrls: ['./registrazione.component.css']
})

export class RegistrazioneComponent implements OnInit {

  hide = true;

  dataNascita = new FormControl();
  nome = new FormControl('', [Validators.required, Validators.maxLength(10)]);
  cognome = new FormControl('', [Validators.required, Validators.maxLength(15)]);
  email = new FormControl('', [Validators.required, Validators.email]);
  password = new FormControl('', [Validators.required, Validators.minLength(8), Validators.maxLength(16),
    Validators.pattern("^(?=.*[0-9])(?=.*[!@#$%^&*])[a-zA-Z0-9!@#$%^&*]+$"), ]);

    // GESTIONE ERRORI
    getErrorMessage(control: AbstractControl): string {
      if (control.hasError('required')) {
        return 'Campo obbligatorio';
      } else if(control.hasError('maxlength')){
        return 'Troppo lungo';
      } 
      else if (this.email.hasError('email')) {
      return this.email.hasError('email') ? 'Email non valida' : '';

      } else if (this.password.hasError('pattern')) {
        return 'Password non valida, inserire almeno un numero e un carattere speciale';
      } else { return this.password.hasError('minlength') ?
        'Password troppo piccola, minimo 8 caratteri' : ' ';
      }
    }
 
      //SELECT
  selected = new FormControl(false, [Validators.required]);
  selectFormControl = new FormControl('',[Validators.required]);
  nativeSelectFormControl = new FormControl('',[Validators.required ]);
  matcher = new MyErrorStateMatcher();

    // FORMATO DATA
    private formatDate(date: Date): string {
      const year = date.getFullYear();
      const month = (date.getMonth() + 1).toString().padStart(2, '0');
      const day = date.getDate().toString().padStart(2, '0');
      return `${year}-${month}-${day}`;
    }

    constructor(private UtenteService : UtenteService,  private dialog: MatDialog ) {}

    ngOnInit(): void { } 

    openPopupRegistrazione(message: string):void{
      const dialogRef = this.dialog.open(PopUpRegistrazioneComponent,
      {    width: '250px',
      data: { message },
      disableClose: true,})
    }
    

    onSubmit(){ 
      const isGestore: boolean = this.selected.value!
      const emailValue: string = this.email.value !== null ? this.email.value : '';
      const passwordValue: string = this.password.value !== null ? this.password.value : '';
  
      const formData = {
        dataNascita: this.formatDate(this.dataNascita.value),
        nome: this.nome.value,
        cognome: this.cognome.value,
        email: emailValue,
        password: passwordValue,
      };
    

      this.resetForm();

      //INVIO DATI
      const headers = new HttpHeaders({
        'Content-Type': 'application/json',
      });

      this.UtenteService.registerUser(isGestore, formData)
      .subscribe(
        (risposta) => {
          console.log('Risposta dal backend:', risposta);
          if (risposta.status === 'success') {
            // Set user information in a cookie upon successful registration
            this.UtenteService.login(formData.email, formData.password)
              .subscribe(
                (loginResponse) => {
                  console.log('Login successful after registration:', loginResponse);
                  this.openPopupRegistrazione('Utente registrato con successo');
                },
                (loginError) => {
                  console.error('Error during login after registration:', loginError);
                  this.openPopupRegistrazione('Errore durante il login dopo la registrazione');
                }
              );
          }
        },
        (errore: any) => {
          console.error('Errore durante la richiesta PUT:', errore);
          if (errore.status === 400) {
            this.openPopupRegistrazione('Utente già registrato');
          }
        }
      );
  }

    resetForm() {    //RESET CAMPI DEL FORM
      this.dataNascita.reset();
      this.nome.reset();
      this.cognome.reset();
      this.email.reset();
      this.password.reset();
      this.selected.reset();
        // AZZERA STATO "TOCCATO" E "SPORCO" DEL FORM
      this.dataNascita.markAsUntouched();
      this.nome.markAsUntouched();
      this.cognome.markAsUntouched();
      this.email.markAsUntouched();
      this.password.markAsUntouched();
      this.selected.markAsUntouched();
      this.dataNascita.markAsPristine();
      this.nome.markAsPristine();
      this.cognome.markAsPristine();
      this.email.markAsPristine();
      this.password.markAsPristine();
      this.selected.markAsPristine();
    }
}

