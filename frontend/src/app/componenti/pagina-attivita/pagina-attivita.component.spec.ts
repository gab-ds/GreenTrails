import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaginaAttivitaComponent } from './pagina-attivita.component';

describe('PaginaAttivitaComponent', () => {
  let component: PaginaAttivitaComponent;
  let fixture: ComponentFixture<PaginaAttivitaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PaginaAttivitaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PaginaAttivitaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
