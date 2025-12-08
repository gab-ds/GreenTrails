import { TestBed } from '@angular/core/testing';

import { ValoriEcosostenibilitaService } from './valori-ecosostenibilita.service';

describe('ValoriEcosostenibilitaService', () => {
  let service: ValoriEcosostenibilitaService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ValoriEcosostenibilitaService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
