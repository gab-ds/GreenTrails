/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';


import { GestionePrenotazioniAttiveComponent } from './gestione-prenotazioni-attive.component';

describe('GestionePrenotazioniAttiveComponent', () => {
  let component: GestionePrenotazioniAttiveComponent;
  let fixture: ComponentFixture<GestionePrenotazioniAttiveComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GestionePrenotazioniAttiveComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GestionePrenotazioniAttiveComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
