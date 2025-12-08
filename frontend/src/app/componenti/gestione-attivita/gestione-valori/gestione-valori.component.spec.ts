import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GestioneValoriComponent } from './gestione-valori.component';

describe('GestioneValoriComponent', () => {
  let component: GestioneValoriComponent;
  let fixture: ComponentFixture<GestioneValoriComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GestioneValoriComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GestioneValoriComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
