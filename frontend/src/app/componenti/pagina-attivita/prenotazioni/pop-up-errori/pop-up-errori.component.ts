
import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-pop-up-errori',
  templateUrl: './pop-up-errori.component.html',
  styleUrls: ['./pop-up-errori.component.css']
})
export class PopUpErroriComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<PopUpErroriComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit(): void {
  }
  onNoClick(): void {
    this.dialogRef.close();
  }
}
