import { AfterViewInit, Component, OnInit } from '@angular/core';
import { RicercaService } from 'src/app/servizi/ricerca.service';

declare let L: any;

@Component({
  selector: 'app-risultati',
  templateUrl: './risultati.component.html',
  styleUrls: ['./risultati.component.css']
})
export class RisultatiComponent implements OnInit, AfterViewInit {

  greenIcon = new L.Icon({
    iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png',
    shadowUrl: 'https://cdnjs.cloudflare.com/ajax/libs/leaflet/0.7.7/images/marker-shadow.png',
    iconSize: [25, 41],
    iconAnchor: [12, 41],
    popupAnchor: [1, -34],
    shadowSize: [41, 41]
  });

  latitudine: number = 40.6824408;
  longitudine: number = 14.7680961;
  raggio: number = 50;

  showmap: boolean = false;
  markers: any[] = [
    { lat: 0, lng: 0, name: '' },
  ];
  map: any;

  constructor(private ricercaService: RicercaService) {
  }
  ngAfterViewInit(): void {
    this.visualizzaMappa();
  }

  ngOnInit(): void {
  }

  isValidLatitude(): boolean {
    const LATITUDE_REGEX = /^(-?[1-8]?\d(?:\.\d{1,18})?|90(?:\.0{1,18})?)$/;
    return LATITUDE_REGEX.test(this.latitudine!.toString());
  }

  isValidLongitude(): boolean {
    const LONGITUDE_REGEX = /^(-?(?:1[0-7]|[1-9])?\d(?:\.\d{1,18})?|180(?:\.0{1,18})?)$/;
    return LONGITUDE_REGEX.test(this.longitudine!.toString());
  }

  isValidRadius(): boolean {
    const RADIUS_REGEX = /^\d+(\.\d{1,18})?$/;
    return RADIUS_REGEX.test(this.raggio!.toString());
  }

  visualizzaMappa() {
    if (!this.map) {
      this.map = L.map('mappa').setView([42.833333 , 12.833333], 6);
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        maxZoom: 20,
        attribution: '© <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
      }).addTo(this.map);
    } else {
      this.map.flyTo([this.latitudine, this.longitudine], 7, { duration: 0.5 });
    }
  }

  addMarkersToMap() {
    this.map.eachLayer((layer: any) => {
      if (layer instanceof L.Marker) {
        this.map.removeLayer(layer);
      }
    });

    for (let marker of this.markers) {
      let m = L.marker([marker.lat, marker.lng], { icon: this.greenIcon }).addTo(this.map);
      m.bindPopup(`<a href="/attivita/${marker.id}" style="text-decoration: none; color: #32CD32; font-weight: bold">${marker.name}</a>`);
    }
  }


  onSubmit() {
    console.log("LATITUDINE", this.latitudine)
    console.log("LONGITUDINE", this.longitudine)
    console.log("RAGGIO", this.raggio)

    this.ricercaService.cercaPerPosizione(this.latitudine, this.longitudine, this.raggio! * 1000).subscribe((risposta) => {
      console.log("RISPOSTA", risposta.data)

      this.markers = risposta.data.map((item: any) => {
        console.log("lat", item.coordinate.x);
        console.log("lon", item.coordinate.y);
        console.log("nome", item.nome);

        return {
          id: item.id,
          lat: item.coordinate.x,
          lng: item.coordinate.y,
          name: item.nome
        };
      });

      this.addMarkersToMap();
      this.map.flyTo([this.latitudine, this.longitudine], 8, { duration: 0.5 });
    }, (error) => {
      console.error(error)
    });
  }

}
