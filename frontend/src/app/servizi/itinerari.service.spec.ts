import { TestBed } from '@angular/core/testing';

import { ItinerariService } from './itinerari.service';

describe('ItinerariService', () => {
  let service: ItinerariService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ItinerariService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
