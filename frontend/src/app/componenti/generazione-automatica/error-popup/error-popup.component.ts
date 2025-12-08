import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { PopupComponent } from '../../gestione-attivita/gestione-valori/popup/popup.component';

@Component({
  selector: 'app-error-popup',
  templateUrl: './error-popup.component.html',
  styleUrls: ['./error-popup.component.css']
})
export class ErrorPopupComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<PopupComponent>, private router: Router) { }

  ngOnInit() {
  }

  onNoClick(): void {
    this.dialogRef.close();
    this.router.navigate(['/areaRiservata'])
  }

}
