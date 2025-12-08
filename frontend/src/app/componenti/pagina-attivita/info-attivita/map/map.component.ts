import { AttivitaService } from 'src/app/servizi/attivita.service';
import { AfterViewInit, Component, OnInit, Input } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { RicercaService } from 'src/app/servizi/ricerca.service';

declare let L: any;

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})
export class MapComponent implements OnInit, AfterViewInit {
  constructor(private attivitaService: AttivitaService, private route: ActivatedRoute, private ricercaService: RicercaService) { }

  idAttivita: number = 0;
  markers: any[] = [];
  map: any;

  mapInitialized = false;

  greenIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
  });

  goldIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-gold.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
  });

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    this.route.paramMap.subscribe((params: ParamMap) => {
      let idAttivita = parseInt(params.get('id')!);
      this.idAttivita = idAttivita;

      this.visualizzaDettagliAttivita();
    })
  }

  visualizzaDettagliAttivita(): void {
    this.attivitaService.visualizzaAttivita(this.idAttivita).subscribe((attivita) => {
      const nome = attivita.data.nome;
      const x = attivita.data.coordinate.x;
      const y = attivita.data.coordinate.y;

      if (!this.mapInitialized) {
        this.map = L.map('map').setView([x, y], 7, { animate: true });
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
          maxZoom: 19,
          attribution: '© <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
        }).addTo(this.map);
        this.mapInitialized = true;
      } else {
        this.map.flyTo([x, y], 7, { duration: 1 });
      }

      console.log("MAPPA INIZIALIZZATA?", this.mapInitialized)

      this.map.eachLayer((layer: any) => {
        if (layer instanceof L.Marker) {
          this.map.removeLayer(layer);
        }
      });

      let marker = L.marker([x, y], { icon: this.greenIcon }).addTo(this.map);
      marker.bindPopup(`<p style="color: #32CD32; font-weight: bold">${nome}</p>`)

      this.ricercaService.cercaPerPosizione(x, y, 50 * 1000).subscribe((risposta) => {
        this.markers = risposta.data.map((item: any) => {
          return {
            idAttivita: item.id,
            lat: item.coordinate.x,
            lng: item.coordinate.y,
            name: item.nome
          };
        });

        for (let marker of this.markers) {
          let icon = this.greenIcon;
          let color = '#32CD32';
          if (marker.lat !== x || marker.lng !== y) {
            icon = this.goldIcon;
            color = '#FFD700';
          }
          let m = L.marker([marker.lat, marker.lng], { icon: icon }).addTo(this.map);
          m.bindPopup(`<a href="/attivita/${marker.idAttivita}" style="text-decoration: none; color: ${color}; font-weight: bold">${marker.name}</a>`);
        }
      }, (error) => {
        console.error(error)
      })

    }, (error) => {
      console.error(error);
    })
  }
}
