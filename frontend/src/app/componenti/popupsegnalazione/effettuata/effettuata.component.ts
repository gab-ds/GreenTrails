import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PopupRecensioneComponent } from '../../pagina-attivita/recensioni/popup-recensione/popup-recensione.component';

@Component({
  selector: 'app-effettuata',
  templateUrl: './effettuata.component.html',
  styleUrls: ['./effettuata.component.css']
})
export class EffettuataComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<PopupRecensioneComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) { }

  ngOnInit(): void {
  }

  onNoClick(): void {
    this.dialogRef.close();
    window.location.reload();
  }

}
