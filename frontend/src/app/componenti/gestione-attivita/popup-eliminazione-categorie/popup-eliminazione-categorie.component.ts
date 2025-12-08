import { Component, Inject, OnInit } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CategoriaService } from 'src/app/servizi/categoria.service';

export interface DialogData {
  message: string;
}

@Component({
  selector: 'app-popup-eliminazione-categorie',
  templateUrl: './popup-eliminazione-categorie.component.html',
  styleUrls: ['./popup-eliminazione-categorie.component.css']
})
export class PopupEliminazioneCategorieComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<PopupEliminazioneCategorieComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private categoriaService: CategoriaService) {}

  ngOnInit(): void {
  }

  delete() {
    this.categoriaService.rimuoviCategoria(this.data.id, this.data.idAttivita).subscribe((risposta) => {
      console.log("Eliminazione categoria: " + this.data.id, risposta);
    })
  }

}
