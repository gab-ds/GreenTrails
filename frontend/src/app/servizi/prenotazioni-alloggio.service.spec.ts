import { TestBed } from '@angular/core/testing';

import { PrenotazioniAlloggioService } from './prenotazioni-alloggio.service';

describe('PrenotazioniAlloggioService', () => {
  let service: PrenotazioniAlloggioService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PrenotazioniAlloggioService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
