import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { NgbRatingConfig } from '@ng-bootstrap/ng-bootstrap';
import { RecensioneService } from 'src/app/servizi/recensione.service';
import { UploadService } from 'src/app/servizi/upload.service';
import { GalleryDialogComponent } from './gallery-dialog/gallery-dialog.component';
import { VideoDialogComponent } from './video-dialog/video-dialog.component';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-recensioni',
  templateUrl: './recensioni.component.html',
  styleUrls: ['./recensioni.component.css'],
  providers: [NgbRatingConfig],
})
export class RecensioniComponent implements OnInit {

  constructor(public dialog: MatDialog, config: NgbRatingConfig, private recensioneService: RecensioneService, private route: ActivatedRoute,
    private uploadService: UploadService, private cookieService: CookieService) {
    config.max = 5;
    config.readonly = true;
  }

  idAttivita: number = 0;
  hasRecensione: boolean = false;
  isVisitatore: boolean = false;
  imageUrls: string[] = [];
  fileNames: string[] = [];
  recensioniConMedia: any[] = [];
  recensioniSenzaMedia: any[] = [];
  recensioni: any;

  ngOnInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      let id = parseInt(params.get('id')!);
      this.idAttivita = id;

      this.visualizzaListaRecensioni();
      this.isVisitatore = this.cookieService.get('ruolo') === 'ROLE_VISITATORE'
    })
  }

  visualizzaListaRecensioni(): void {

    this.recensioniConMedia = [];
    this.recensioniSenzaMedia = [];

    this.recensioneService.visualizzaRecensioniPerAttivita(this.idAttivita).subscribe((listaRecensioni) => {
      this.recensioni = listaRecensioni.data;

      this.recensioni.forEach((recensione: any, index: number) => {
        if (recensione.media !== null) {
          this.uploadService.elencaFileCaricati(recensione.media).subscribe((listaFiles) => {
            if (listaFiles.data.length > 0) {
              const fileName = listaFiles.data[0];
              this.uploadService.serviFile(recensione.media, fileName).subscribe((file) => {
                let reader = new FileReader();
                reader.onloadend = () => {
                  this.imageUrls[index] = reader.result as string;
                  this.recensioniConMedia.push({
                    ...recensione,
                    image: this.imageUrls[index],
                    fileName: fileName
                  });
                };
                reader.readAsDataURL(file);
              });
            }
          });
        } else {
          this.recensioniSenzaMedia.push(recensione);
        }
      });

      console.log("RECENSIONI CON MEDIA: ", this.recensioniConMedia);
      console.log("RECENSIONI SENZA MEDIA: ", this.recensioniSenzaMedia)

      this.hasRecensione = this.recensioni.some((item: any) => item.visitatore.email === this.cookieService.get('email').replace(/"/g, ''));
    });
  }


  isImage(file: string): boolean {
    return file.toLowerCase().endsWith('.jpg') || file.endsWith('.jpeg') || file.toLowerCase().endsWith('.png');
  }

  isVideo(file: string): boolean {
    return file.toLowerCase().endsWith('.mp4') || file.toLowerCase().endsWith('.avi') || file.toLowerCase().endsWith('.mov');
  }

  openDialogue(index: number) {
    const recensione = this.recensioniConMedia[index];
    const fileName = recensione.fileName;
    if (this.isImage(fileName)) {
      const dialogRef = this.dialog.open(GalleryDialogComponent, {
        data: {
          image: recensione.image
        }
      });
    } else if (this.isVideo(fileName)) {
      const dialogRef = this.dialog.open(VideoDialogComponent, {
        data: {
          video: recensione.image
        }
      });
    } else {
      console.error(`Unsupported file type: ${fileName}`);
    }
  }
}
