import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Router } from '@angular/router';

@Component({
  selector: 'app-pop-up-questionario',
  templateUrl: './pop-up-questionario.component.html',
  styleUrls: ['./pop-up-questionario.component.css']
})
export class PopUpQuestionarioComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<PopUpQuestionarioComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, private router: Router) { }

  ngOnInit(): void {
  }
  
  onNoClick(): void {
    this.router.navigate(['/areaRiservata']);
    this.dialogRef.close();
  }

}
