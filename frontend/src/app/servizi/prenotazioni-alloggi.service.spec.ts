import { TestBed } from '@angular/core/testing';

import { PrenotazioniAlloggiService } from './prenotazioni-alloggi.service';

describe('PrenotazioniAlloggiService', () => {
  let service: PrenotazioniAlloggiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PrenotazioniAlloggiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
