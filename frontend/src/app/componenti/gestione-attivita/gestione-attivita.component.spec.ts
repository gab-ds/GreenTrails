import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestioneAttivitaComponent } from './gestione-attivita.component';

describe('GestioneAttivitaComponent', () => {
  let component: GestioneAttivitaComponent;
  let fixture: ComponentFixture<GestioneAttivitaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GestioneAttivitaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestioneAttivitaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
