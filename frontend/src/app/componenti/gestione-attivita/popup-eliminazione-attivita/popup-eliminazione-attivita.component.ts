import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { AttivitaService } from 'src/app/servizi/attivita.service';

export interface DialogData {
  message: string;
}

@Component({
  selector: 'app-popup-eliminazione',
  templateUrl: './popup-eliminazione-attivita.component.html',
  styleUrls: ['./popup-eliminazione-attivita.component.css']
})
export class PopupEliminazioneComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<PopupEliminazioneComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private attivitaService: AttivitaService) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit(): void {
      
  }

  delete() {
    this.attivitaService.cancellaAttivita(this.data.id).subscribe((risposta) => {
      console.log("Eliminazione attivita: " + this.data.id, risposta);
      window.location.reload()
    })
  }

}
