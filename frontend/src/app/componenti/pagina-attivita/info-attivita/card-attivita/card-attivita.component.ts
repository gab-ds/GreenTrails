import { Component, Input, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { PopupsegnalazioneComponent } from 'src/app/componenti/popupsegnalazione/popupsegnalazione.component';
import { AttivitaService } from 'src/app/servizi/attivita.service';
import { UploadService } from 'src/app/servizi/upload.service';

@Component({
  selector: 'app-card-attivita',
  templateUrl: './card-attivita.component.html',
  styleUrls: ['./card-attivita.component.css']
})
export class CardAttivitaComponent implements OnInit {

  idAttivita: number = 0;
  nomeAttivita: string = '';
  directoryAttivita: string = '';
  attivita: any;
  imageUrls: string[] = [];
  fileNames: string[] = [];
  mostraPopup: boolean = false;
  isVisitatore!: boolean;

  constructor(private attivitaService: AttivitaService, private route: ActivatedRoute, private uploadService: UploadService, private cookieService: CookieService, private dialog:MatDialog) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      let id = parseInt(params.get('id')!);
      this.idAttivita = id;

      this.visualizzaDettagliAttivita();
      this.isVisitatore = this.cookieService.get('ruolo') === 'ROLE_VISITATORE';
    });
  }

  visualizzaDettagliAttivita(): void {
    this.attivitaService.visualizzaAttivita(this.idAttivita).subscribe((attivita) => {
      this.attivita = attivita.data;
      this.nomeAttivita = attivita.data.nome;
      console.log("NOME: ", this.nomeAttivita);
      this.directoryAttivita = attivita.data.media;
      console.log("PERCORSO MEDIA ATTIVITA", this.directoryAttivita);

      this.uploadService.elencaFileCaricati(this.directoryAttivita).subscribe((risposta) => {
        console.log("IMMAGINI ATTIVITA", risposta);
        const fileName = risposta.data[0];
        console.log("FILENAME:", fileName);

        this.uploadService.serviFile(this.directoryAttivita, fileName).subscribe((risposta) => {
          console.log("BLOB OTTENUTO: ", risposta);
          this.fileNames.push(fileName);
          console.log(this.fileNames);

          let reader = new FileReader();
          reader.onloadend = () => {
            this.imageUrls[0] = reader.result as string;
          };
          reader.readAsDataURL(risposta);
        });
      });
    }, (error) => {
      console.error(error);
    });
  }

  apriSegnalazione(): void {
    const dialogRef = this.dialog.open(PopupsegnalazioneComponent, {
      data:{
        id: this.idAttivita
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('Dialog chiuso:', result);
    });
  }

  chiudiFormPopup(): void {
    this.mostraPopup = false;
  }

  handleFormSubmitted(): void {
    console.log('Form inviato con successo!');
  }
}
