import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-pop-up-conferma',
  templateUrl: './pop-up-conferma.component.html',
  styleUrls: ['./pop-up-conferma.component.css']
})
export class PopUpConfermaComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<PopUpConfermaComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private router: Router, private cookieService: CookieService) { }

  ruolo: any;

  ngOnInit(): void {
    this.ruolo = this.cookieService.get('ruolo')
  }
  onNoClick(): void {
    if (this.ruolo === "ROLE_GESTORE_ATTIVITA") {
      this.dialogRef.close();
      window.location.reload();
    }
    else if (this.ruolo === "ROLE_VISITATORE") {
      this.dialogRef.close();
      this.router.navigate(['/tabellaPrenotazioni'])
    }
  }



}