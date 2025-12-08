import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopUpPrenotazioneComponent } from './pop-up-prenotazione.component';

describe('PopUpPrenotazioneComponent', () => {
  let component: PopUpPrenotazioneComponent;
  let fixture: ComponentFixture<PopUpPrenotazioneComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopUpPrenotazioneComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopUpPrenotazioneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
