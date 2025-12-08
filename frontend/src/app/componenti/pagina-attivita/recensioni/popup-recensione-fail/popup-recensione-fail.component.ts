import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { PopupRecensioneComponent } from '../popup-recensione/popup-recensione.component';

@Component({
  selector: 'app-popup-recensione-fail',
  templateUrl: './popup-recensione-fail.component.html',
  styleUrls: ['./popup-recensione-fail.component.css']
})
export class PopupRecensioneFailComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<PopupRecensioneFailComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

}
