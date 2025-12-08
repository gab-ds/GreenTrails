import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CamereService } from 'src/app/servizi/camere.service';

export interface DialogData {
  message: string;
}

@Component({
  selector: 'app-popup-eliminazione-camera',
  templateUrl: './popup-eliminazione-camera.component.html',
  styleUrls: ['./popup-eliminazione-camera.component.css']
})
export class PopupEliminazioneCameraComponent implements OnInit {
  
  constructor(
    public dialogRef: MatDialogRef<PopupEliminazioneCameraComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private camereService: CamereService) {}

  ngOnInit(): void {
  }

  delete() {
    this.camereService.cancellaCamera(this.data.id).subscribe((risposta) => {
      console.log("Eliminazione camera: " + this.data.id, risposta);
    })
  }

}
