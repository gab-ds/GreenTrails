/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { StoricoPrenotazioniService } from './storico-prenotazioni.service';

describe('Service: StoricoPrenotazioni', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [StoricoPrenotazioniService]
    });
  });

  it('should ...', inject([StoricoPrenotazioniService], (service: StoricoPrenotazioniService) => {
    expect(service).toBeTruthy();
  }));
});
