import { TestBed } from '@angular/core/testing';

import { RegistrazioneService } from './registrazione.service';

describe('RegistrazioneService', () => {
  let service: RegistrazioneService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RegistrazioneService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
