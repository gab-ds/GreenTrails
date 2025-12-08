import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupEliminazioneComponent } from './popup-eliminazione-attivita.component';

describe('PopupEliminazioneComponent', () => {
  let component: PopupEliminazioneComponent;
  let fixture: ComponentFixture<PopupEliminazioneComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopupEliminazioneComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopupEliminazioneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
