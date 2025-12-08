import { Component } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-cookiedialog',
  templateUrl: './cookiedialog.component.html',
  styleUrls: ['./cookiedialog.component.css']
})
export class CookieDialogComponent {
  constructor(public dialogRef: MatDialogRef<CookieDialogComponent>) {}

  onAccept(): void {
    this.dialogRef.close(true);
  }

  onDecline(): void {
    this.dialogRef.close(false);
  }
}
