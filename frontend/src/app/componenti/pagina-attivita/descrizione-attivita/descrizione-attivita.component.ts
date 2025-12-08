import { Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute, ParamMap, } from '@angular/router';
import { AttivitaService } from 'src/app/servizi/attivita.service';

@Component({
  selector: 'app-descrizione-attivita',
  templateUrl: './descrizione-attivita.component.html',
  styleUrls: ['./descrizione-attivita.component.css']
})
export class DescrizioneAttivitaComponent implements OnInit {

  idAttivita: any;
  attivita: any;

  constructor(private attivitaService: AttivitaService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      let id = parseInt(params.get('id')!);
      this.idAttivita = id;

      this.visualizzaDettagliAttivita();
    })
  }


  visualizzaDettagliAttivita() {
    this.attivitaService.visualizzaAttivita(this.idAttivita).subscribe((risposta) => {
      this.attivita = risposta.data;
      console.log("MAMMT", this.attivita)
    })
  }

}
