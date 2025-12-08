import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ValutazioneAttivitaComponent } from './valutazione-attivita.component';

describe('ValutazioneAttivitaComponent', () => {
  let component: ValutazioneAttivitaComponent;
  let fixture: ComponentFixture<ValutazioneAttivitaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ValutazioneAttivitaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ValutazioneAttivitaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
