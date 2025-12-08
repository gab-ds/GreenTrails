import { TestBed } from '@angular/core/testing';

import { PrenotazioniAttivitaService } from './prenotazioni-attivita.service';

describe('PrenotazioniAttivitaService', () => {
  let service: PrenotazioniAttivitaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PrenotazioniAttivitaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
