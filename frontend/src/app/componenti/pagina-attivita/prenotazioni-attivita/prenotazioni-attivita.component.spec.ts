import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PrenotazioniAttivitaComponent } from './prenotazioni-attivita.component';

describe('PrenotazioniAttivitaComponent', () => {
  let component: PrenotazioniAttivitaComponent;
  let fixture: ComponentFixture<PrenotazioniAttivitaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PrenotazioniAttivitaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PrenotazioniAttivitaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
