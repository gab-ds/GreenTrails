import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CardAttivitaComponent } from './card-attivita.component';

describe('CardAttivitaComponent', () => {
  let component: CardAttivitaComponent;
  let fixture: ComponentFixture<CardAttivitaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CardAttivitaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CardAttivitaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
