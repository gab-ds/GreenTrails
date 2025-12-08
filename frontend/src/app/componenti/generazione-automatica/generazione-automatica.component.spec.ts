import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerazioneAutomaticaComponent } from './generazione-automatica.component';

describe('GenerazioneAutomaticaComponent', () => {
  let component: GenerazioneAutomaticaComponent;
  let fixture: ComponentFixture<GenerazioneAutomaticaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ GenerazioneAutomaticaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GenerazioneAutomaticaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
