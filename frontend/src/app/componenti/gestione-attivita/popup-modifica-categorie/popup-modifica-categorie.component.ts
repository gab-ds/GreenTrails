import { Component, Inject, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef, MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CategoriaService } from 'src/app/servizi/categoria.service';
import { PopUpConfermaComponent } from '../../inserimento-attivita/pop-up-conferma/pop-up-conferma.component';
import { AttivitaService } from 'src/app/servizi/attivita.service';
import { PopupEliminazioneCategorieComponent } from '../popup-eliminazione-categorie/popup-eliminazione-categorie.component';

@Component({
  selector: 'app-popup-modifica-categorie',
  templateUrl: './popup-modifica-categorie.component.html',
  styleUrls: ['./popup-modifica-categorie.component.css']
})
export class PopupModificaCategorieComponent implements OnInit {


  idAttivita: any
  categorieSelezionate: string[] = [];
  formGroup: FormGroup
  listaCategoriePossedute: any;
  categoriePresenti!: boolean;

  categorie: { id: number, descrizione: string, nome: string }[] = [
    { id: 1, descrizione: 'Attività e alloggi legati al patrimonio culturale e storico. Include musei, siti archeologici, monumenti storici, o alloggi in edifici storici.', nome: 'Cultura e Storia' },
    { id: 2, descrizione: 'Esperienze culinarie locali o internazionali. Include ristoranti, degustazioni di vini, corsi di cucina, o alloggi con esperienze culinarie uniche.', nome: 'Enogastronomia' },
    { id: 3, descrizione: 'Attività e alloggi per godere della bellezza naturale. Include escursioni, parchi nazionali, oasi naturalistiche, o alloggi immersi nella natura.', nome: 'Natura e Riserve naturali' },
    { id: 4, descrizione: 'Attività sportive all\'\aperto. Include escursioni, ciclismo, arrampicata, o alloggi vicini a queste attività.', nome: 'Sport in spazio aperto' },
    { id: 5, descrizione: 'Attività e alloggi per il relax e il benessere. Include spa, centri benessere, o alloggi con servizi di spa e benessere.', nome: 'Relax e Benessere' },
    { id: 6, descrizione: 'Eventi in un determinato luogo. Include festival, concerti, fiere, o alloggi vicini a questi eventi.', nome: 'Eventi locali' },
    { id: 7, descrizione: 'Attività per esplorare la natura o i siti storici. Include escursioni per scoprire paesaggi mozzafiato, fauna selvatica o monumenti storici.', nome: 'Escursione' },
    { id: 8, descrizione: 'Luoghi per acquistare prodotti locali, artigianali o unici. Include negozi con una vasta gamma di articoli che riflettono la cultura locale.', nome: 'Negozio locale' },
    { id: 9, descrizione: 'Attività all\'\esterno per godere dell\'\ambiente naturale. Include sport, picnic, birdwatching o rilassarsi in un bel parco.', nome: 'Spazio aperto' },
    { id: 10, descrizione: 'Alloggi e attività vicino alla costa. Include opportunità per nuotare, prendere il sole, fare snorkeling o godersi la vista dell\'\oceano.', nome: 'Vicino al mare' }
  ];
  deletedCategoriaIds: number[] = [];


  constructor(public dialogRef: MatDialogRef<PopUpConfermaComponent>,
    private categoriaService: CategoriaService, private dialog: MatDialog,
    @Inject(MAT_DIALOG_DATA) public data: any, private formBuilder: FormBuilder,
    private attivitaService: AttivitaService) {
    this.formGroup = this.formBuilder.group({
      categoria: ['', Validators.required],
    })
  }

  ngOnInit(): void {
    console.log('Attivita creata:', this.data.id);
    console.log(this.data.id)
    this.idAttivita = this.data.idAttivita
    this.visualizzaCategoriePossedute();
  }


  aggiungiCategoria() {
    console.log(this.idAttivita)

    const categoriaSelezionata = this.formGroup.get('categoria')?.value;
    console.log('categoria', categoriaSelezionata)
    this.categoriaService.aggiungiCategoria(this.idAttivita, categoriaSelezionata)
      .subscribe((response) => {
        this.categorieSelezionate.push(categoriaSelezionata);

        const categoriaAggiunta = this.categorie.find(categoria => categoria.id === categoriaSelezionata);
        if (categoriaAggiunta) {
          this.listaCategoriePossedute.push(categoriaAggiunta);
        }
      })
  }


  openPopupConferma(message: string): void {
    const dialogRef = this.dialog.open(PopUpConfermaComponent, {
      width: '60%',
      data: { message },
      disableClose: true,

    });
  }

  onNoClick(): void {
    this.dialogRef.close();
    this.openPopupConferma('Attivita modificata con successo')
  }

  visualizzaCategoriePossedute() {
    this.attivitaService.visualizzaAttivita(this.idAttivita).subscribe((risposta) => {
      this.listaCategoriePossedute = risposta.data.categorie
      console.log("CATEGORIE POSSEDUTE: ", this.listaCategoriePossedute);
      this.categoriePresenti = this.listaCategoriePossedute.length > 0;
      console.log(this.categoriePresenti);
    })
  }

  delete(id: number, idAttivita: number) {
    this.categoriaService.visualizzaCategoria(id).subscribe((risposta) => {
      console.log("Categoria con id: " + id, risposta);

      const dialogRef = this.dialog.open(PopupEliminazioneCategorieComponent, {
        data: {
          message: 'Categoria eliminata con successo',
          id: id,
          idAttivita: idAttivita
        },
        disableClose: true
      });
      this.deletedCategoriaIds.push(id);
      dialogRef.afterClosed().subscribe(result => {
        if (result) {
        }
      }, (error) => {
        console.log(error);
      });
    });
  }

  isCategoriaPresente(categoriaId: number): boolean {
    return this.listaCategoriePossedute.map((c: any) => c.id).includes(categoriaId);
  }

}
