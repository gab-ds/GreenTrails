import { ViewportScroller } from '@angular/common';
import { Component, HostListener, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { CookieDialogComponent } from './componenti/cookiedialog/cookiedialog.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'GreenTrails';
  showButton: boolean = false;

  constructor(public router: Router, private viewportScroller: ViewportScroller, public dialog: MatDialog) {}

  ngOnInit(): void {
    const acceptedCookies = localStorage.getItem('acceptedCookies');
    if (!acceptedCookies) {
      setTimeout(() => this.openDialog(), 1000); // Apri il dialog dopo 1 secondo
    }
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(CookieDialogComponent, {
      width: '250px',
      disableClose: true, // L'utente non può chiudere il dialog cliccando fuori
      position: { bottom: '0px' } // Posiziona il dialog in basso
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        localStorage.setItem('acceptedCookies', 'true'); // Salva la scelta dell'utente
      } else {
        window.location.href = 'https://www.google.com'; // Reindirizza l'utente se rifiuta
      }
    });
  }

  @HostListener('window:scroll', [])
  onWindowScroll() {
    if ((window.pageYOffset || document.documentElement.scrollTop || document.body.scrollTop) > 20) {
      console.log("PULSANTE APPARE")
      this.showButton = true;
    } else {
      console.log("PULSANTE SCOMPARE")
      this.showButton = false;
    }
  }

  scrollToTop() {
    this.viewportScroller.scrollToPosition([0, 0]);
  }
}
