import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-popup-errorPass',
  templateUrl: './popup-errorPass.component.html',
  styleUrls: ['./popup-errorPass.component.css']
})

export class PopupErrorPassComponent implements OnInit {
  constructor(public dialogRef: MatDialogRef<PopupErrorPassComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit(): void {
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
