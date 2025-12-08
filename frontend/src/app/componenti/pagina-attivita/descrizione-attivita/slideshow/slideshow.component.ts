import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { AttivitaService } from 'src/app/servizi/attivita.service';
import { UploadService } from 'src/app/servizi/upload.service';

@Component({
  selector: 'app-slideshow',
  templateUrl: './slideshow.component.html',
  styleUrls: ['./slideshow.component.css']
})
export class SlideshowComponent implements OnInit {

  idAttivita: number = 0;
  imageUrls: { name: string, caption: string }[] = [];
  directoryAttivita: string = '';

  constructor(private uploadService: UploadService, private attivitaService: AttivitaService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      let id = parseInt(params.get('id')!);
      this.idAttivita = id;

      this.visualizzaDettagliAttivita();
    })
  }

  visualizzaDettagliAttivita(): void {
    this.attivitaService.visualizzaAttivita(this.idAttivita).subscribe((attivita) => {
      this.directoryAttivita = attivita.data.media;
      console.log("PERCORSO MEDIA ATTIVITA", this.directoryAttivita);

      this.uploadService.elencaFileCaricati(this.directoryAttivita).subscribe((risposta) => {
        console.log("IMMAGINI ATTIVITA", risposta);
        
        this.imageUrls = risposta.data.map((fileName: string) => ({
          name: `${fileName}`,
          caption: 'File non trovato'
        }));

        this.imageUrls.forEach((imageUrl) => {
          this.uploadService.serviFile(this.directoryAttivita, imageUrl.name).subscribe((risposta) => {
            console.log("BLOB OTTENUTO: ", risposta);

            let reader = new FileReader();
            reader.onloadend = () => {
              imageUrl.name = reader.result as string;
            };
            reader.readAsDataURL(risposta);
          });
        });
      });
    }, (error) => {
      console.error(error);
    });
  }
}
