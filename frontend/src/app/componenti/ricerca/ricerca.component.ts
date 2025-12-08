import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Params, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { AttivitaService } from 'src/app/servizi/attivita.service';
import { UploadService } from 'src/app/servizi/upload.service';

@Component({
  selector: 'app-ricerca',
  templateUrl: './ricerca.component.html',
  styleUrls: ['./ricerca.component.css']
})
export class RicercaComponent implements OnInit {

  listaAttivita: { nome: string, citta: string, categorie: string[] }[] = [];
  filterTerm!: string;
  showresultcontainer = false;
  paramsSubscription!: Subscription;

  filters = {
    restaurant: false,
    excursions: false,
    nearSea: false,
    localShops: false
  };

  constructor(private attivitaService: AttivitaService, private uploadService: UploadService, private router: Router, private route: ActivatedRoute) {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        console.log('NavigationEnd:', event.url);
      }
    });
  }

  ngOnInit(): void {
    this.getAll();
  }

  getAll(): void {
    this.attivitaService.findAll().subscribe((risposta) => {
      console.log("LISTA ATTIVITA: ", risposta);

      this.listaAttivita = risposta.data.filter((attivita: any) => !attivita.eliminata).
      map((item: any, index: number) => {
        return {
          nome: item.nome,
          citta: item.citta,
          id: item.id,
          categorie: item.categorie.map((categoria: any) => categoria.nome)
        };
      });
    });
  }

  navigateToAttivita(id: number): void {
    this.router.navigate(['/attivita', id]);
    this.showresultcontainer = false;
    this.filterTerm = '';
  }

  onSearch(): void {
    this.showresultcontainer = this.filterTerm.length > 0;
  }

}
