import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-popupDeleteConferma',
  templateUrl: './popupDeleteConferma.component.html',
  styleUrls: ['./popupDeleteConferma.component.css']
})
export class PopupDeleteConfermaComponent implements OnInit {
  constructor(public dialogRef: MatDialogRef<PopupDeleteConfermaComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit(): void {
  }

  onNoClick(): void {
    this.dialogRef.close();
    window.location.reload();
  }

}
