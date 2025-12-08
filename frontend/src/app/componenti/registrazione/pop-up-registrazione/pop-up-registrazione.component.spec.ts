import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopUpRegistrazioneComponent } from './pop-up-registrazione.component';

describe('PopUpRegistrazioneComponent', () => {
  let component: PopUpRegistrazioneComponent;
  let fixture: ComponentFixture<PopUpRegistrazioneComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopUpRegistrazioneComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopUpRegistrazioneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
