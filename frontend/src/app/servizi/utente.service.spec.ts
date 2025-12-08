/* tslint:disable:no-unused-variable */

import { TestBed, async, inject } from '@angular/core/testing';
import { UtenteService } from './utente.service';

describe('Service: Utente', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [UtenteService]
    });
  });

  it('should ...', inject([UtenteService], (service: UtenteService) => {
    expect(service).toBeTruthy();
  }));
});
