import { Component, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-video-dialog',
  template: `
    <video width="100%" height="100%" controls>
      <source [src]="data.video" type="video/mp4">
      Your browser does not support the video tag.
    </video>
  `,
})
export class VideoDialogComponent {
  constructor(
    public dialogRef: MatDialogRef<VideoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }
}