import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';

@Component({
  selector: 'app-pop-up-registrazione',
  templateUrl: './pop-up-registrazione.component.html',
  styleUrls: ['./pop-up-registrazione.component.css']
})
export class PopUpRegistrazioneComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<PopUpRegistrazioneComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private router: Router) { }

  ngOnInit(): void {
  }
  onNoClick(): void {
    this.dialogRef.close();
    this.router.navigate(['/']);
  }
}