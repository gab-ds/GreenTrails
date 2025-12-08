import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-popuperrore',
  templateUrl: './popuperrore.component.html',
  styleUrls: ['./popuperrore.component.css']
})
export class PopuperroreComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<PopuperroreComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit(): void {
  }
  onNoClick(): void {
    this.dialogRef.close();
  }

}
