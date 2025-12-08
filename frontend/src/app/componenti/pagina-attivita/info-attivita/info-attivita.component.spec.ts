import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InfoAttivitaComponent } from './info-attivita.component';

describe('InfoAttivitaComponent', () => {
  let component: InfoAttivitaComponent;
  let fixture: ComponentFixture<InfoAttivitaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InfoAttivitaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InfoAttivitaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
