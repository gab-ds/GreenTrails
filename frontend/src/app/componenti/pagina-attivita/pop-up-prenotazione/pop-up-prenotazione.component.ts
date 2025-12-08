import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-pop-up-prenotazione',
  templateUrl: './pop-up-prenotazione.component.html',
  styleUrls: ['./pop-up-prenotazione.component.css']
})
export class PopUpPrenotazioneComponent implements OnInit {


  constructor(public dialogRef: MatDialogRef<PopUpPrenotazioneComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit(): void {
  }
  onNoClick(): void {
    this.dialogRef.close();
    window.location.reload();
  }
}