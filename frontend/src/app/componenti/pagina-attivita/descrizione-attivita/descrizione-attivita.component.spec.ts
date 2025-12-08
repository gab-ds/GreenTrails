import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DescrizioneAttivitaComponent } from './descrizione-attivita.component';

describe('DescrizioneAttivitaComponent', () => {
  let component: DescrizioneAttivitaComponent;
  let fixture: ComponentFixture<DescrizioneAttivitaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DescrizioneAttivitaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DescrizioneAttivitaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
