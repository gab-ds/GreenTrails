import { TestBed } from '@angular/core/testing';

import { PrenotazioniAttivitaTuristicheService } from './prenotazioni-attivita-turistiche.service';

describe('PrenotazioniAttivitaTuristicheService', () => {
  let service: PrenotazioniAttivitaTuristicheService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PrenotazioniAttivitaTuristicheService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
