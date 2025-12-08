import { EffettuataComponent } from '../popupsegnalazione/effettuata/effettuata.component';
import { CookieService } from 'ngx-cookie-service';
import { SegnalazioneService } from './../../servizi/segnalazione.service';
import { NgbRatingConfig } from '@ng-bootstrap/ng-bootstrap';
import { Component, ElementRef, EventEmitter, Inject, OnInit, Output, ViewChild } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { DomSanitizer, SafeUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-popupsegnalazione',
  templateUrl: './popupsegnalazione.component.html',
  styleUrls: ['./popupsegnalazione.component.css'],
  providers: [NgbRatingConfig],
})
export class PopupsegnalazioneComponent implements OnInit {

  @Output() formSottomesso = new EventEmitter<void>();
  @Output() chiudiPopup = new EventEmitter<void>();

  idAttivita!: number;
  immagine: File | null = null;
  descrizione: string = '';


  constructor(
    private segnelazioneService :SegnalazioneService, 
    private cookie: CookieService,
    public dialogRef: MatDialogRef<PopupsegnalazioneComponent>, 
    private dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: any
    ) { }

    ngOnInit(): void {
      this.idAttivita = this.data.id;
    }


    @ViewChild('fileInput') fileInput!: ElementRef<HTMLInputElement>;
    fileNames: string[] = [];
    files: File[] = [];
    filesWithPreview: { name: string; url: SafeUrl }[] = [];

        
    onFileSelected(event: any): void {
      
      const selectedFiles: FileList | null = event.target.files;
      if (selectedFiles && selectedFiles.length > 0) {
        for (let i = 0; i < selectedFiles.length; i++) {
          const file = selectedFiles[i];
          this.files.push(file);
          this.fileNames.push(file.name);
        }
      }
    }
  
    removeFile(index: number): void {
      this.files.splice(index, 1);
      this.fileNames.splice(index, 1);
    }

    openPopup(message: string): void{
      const dialogRef = this.dialog.open(EffettuataComponent, {
        width: '250px',
        data: {message},
        disableClose: true,
      });
    }
  

  submitForm(): void {
    const fileArray = this.files;
    const fileInput = this.fileInput.nativeElement;
    const dataTransfer = new DataTransfer();

    fileArray.forEach(file => {
    dataTransfer.items.add(file);
  });

  fileInput.files = dataTransfer.files;

  this.fileNames = [];
  fileArray.forEach(file => {
    const reader = new FileReader();
    reader.onload = () => {
      this.fileNames.push(file.name);
    };
    reader.readAsDataURL(file);
  });
  
  this.segnelazioneService.mandaDatiSegnalazione(this.descrizione, fileInput.files, this.idAttivita)
  .subscribe((risposta: any) => {
    console.log(risposta);
    if (risposta?.status === 'success') {
      this.chiudiPopup.emit();
      this.formSottomesso.emit();
      this.dialogRef.close();
      this.openPopup('Segnalazione inviata con successo!');

    } else {
      this.openPopup('Errore nell\'\invio della segnalazione');
    }
  }, (error) => {
    console.log('Dettagli richiesta API:', error);
  });
}

  closePopup(){
    this.chiudiPopup.emit();
    this.dialogRef.close();
  }
}