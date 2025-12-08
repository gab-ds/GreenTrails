import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListaSegnalazioniComponent } from './lista-segnalazioni.component';

describe('ListaSegnalazioniComponent', () => {
  let component: ListaSegnalazioniComponent;
  let fixture: ComponentFixture<ListaSegnalazioniComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ListaSegnalazioniComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ListaSegnalazioniComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
