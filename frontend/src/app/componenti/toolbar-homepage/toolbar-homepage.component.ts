import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { UtenteService } from 'src/app/servizi/utente.service';

@Component({
  selector: 'app-toolbar-homepage',
  templateUrl: './toolbar-homepage.component.html',
  styleUrls: ['./toolbar-homepage.component.css']
})
export class ToolbarHomepageComponent implements OnInit {

  isLoggedIn = false;
  isGestore = false;
  isAdmin = false;
  isVisitatore = false;

  constructor(private router: Router, private cookieService: CookieService, private utenteService: UtenteService) { }

  ngOnInit(): void {
    this.isLoggedIn = this.cookieService.get('user') !== ''
    console.log("isLoggedIn", this.isLoggedIn)
    this.isGestore = this.cookieService.get('ruolo') === 'ROLE_GESTORE_ATTIVITA'
    console.log("isGestore", this.isGestore)
    this.isVisitatore = this.cookieService.get('ruolo') === 'ROLE_VISITATORE'
    console.log("isVisitatore", this.isVisitatore)
    this.isAdmin = this.cookieService.get('ruolo') === 'ROLE_AMMINISTRATORE'
    console.log("isAdmin", this.isAdmin)
  }

  navigate() {
    if (this.isLoggedIn) this.router.navigate(['/areaRiservata'])
    else this.router.navigate(['/login'])
  }

}
