import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
@Component({
  selector: 'app-popup-conferma-modifica',
  templateUrl: './popup-conferma-modifica.component.html',
  styleUrls: ['./popup-conferma-modifica.component.css']
})
export class PopupConfermaModificaComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<PopupConfermaModificaComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit(): void {
  }

  onNoClick(): void {
    this.dialogRef.close();
    window.location.reload();
  }

}
