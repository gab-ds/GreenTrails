import { UtenteService } from './../../servizi/utente.service';
import { Component } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { PopupErrorPassComponent } from './popup-errorPass/popup-errorPass.component';
import { MatDialog } from '@angular/material/dialog';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {

loginForm: FormGroup
hide = true;
email= new FormControl('')
password= new FormControl('')

onEnterKeyPressed(): void {
  this.onClick();
}
  constructor(private utenteService: UtenteService,
     private fb: FormBuilder, 
     private dialog: MatDialog, private router: Router, private cookieService: CookieService) {
    this.loginForm = this.fb.group({
      email: [''],
      password: ['']
    });
  }
  

  onSubmit() {}


  onClick() {
    if (this.loginForm.valid) {
      const { email, password } = this.loginForm.value;
      this.utenteService.login(email, password).subscribe(
        (response: any) => {
          console.log('Login avvenuto con successo:', response);
          this.router.navigate(['/']);
          if (response && response.status === 'success') {
          } else {
            console.error('La risposta dal servizio non è valida:', response);
            this.openPopup('Email o Password errate');
          }
        },
        (error: any) => {
          console.error('Errore durante il login:', error);
          if (error.status === 401) {
            this.openPopup('Email o password errate');
          }
        }
      );
    } else {
      this.openPopup('Email o password errate');
    }
  }



  onLogoutClick(): void {
    this.utenteService.logout().subscribe(
      (response) => {
        console.log('Logout successful:', response);
      },
      (error) => {
        console.error('Error during logout:', error);
      }
    );
  }
  openPopup(message: string): void {
    const dialogRef = this.dialog.open(PopupErrorPassComponent, {
      width: '250px',
      data: { message },
      disableClose: true,
    });
    dialogRef.afterClosed().subscribe(result => {
      console.log('Il popup è stato chiuso');
    });
  }
}




