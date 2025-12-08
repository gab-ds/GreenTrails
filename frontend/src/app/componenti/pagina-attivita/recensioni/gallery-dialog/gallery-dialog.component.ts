import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-gallery-dialog',
  template: '<img [src]="data.image" alt="Image" class="dialog-image">',
  styles: [`
    .dialog-image {
      max-width: 100%;
      max-height: 100%;
    }
  `]
})
export class GalleryDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<GalleryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }
}