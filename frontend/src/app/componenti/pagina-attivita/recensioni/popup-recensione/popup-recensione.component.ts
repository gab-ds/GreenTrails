import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-popup-recensione',
  templateUrl: './popup-recensione.component.html',
  styleUrls: ['./popup-recensione.component.css']
})
export class PopupRecensioneComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<PopupRecensioneComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit(): void {
  }

  onNoClick(): void {
    this.dialogRef.close();
    window.location.reload();
  }
}