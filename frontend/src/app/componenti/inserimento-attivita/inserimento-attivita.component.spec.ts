import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InserimentoAttivitaComponent } from './inserimento-attivita.component';

describe('InserimentoAttivitaComponent', () => {
  let component: InserimentoAttivitaComponent;
  let fixture: ComponentFixture<InserimentoAttivitaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InserimentoAttivitaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InserimentoAttivitaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
