import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-toolbar',
  templateUrl: './toolbar.component.html',
  styleUrls: ['./toolbar.component.css']
})
export class ToolbarComponent implements OnInit {

  isLoggedIn = false;
  isGestore = false;
  isVisitatore = false;
  isAdmin = false;

  constructor(private cookieService: CookieService, public router: Router) { }

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
