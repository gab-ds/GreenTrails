import { TestBed } from '@angular/core/testing';

import { CamereService } from './camere.service';

describe('CamereService', () => {
  let service: CamereService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CamereService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
