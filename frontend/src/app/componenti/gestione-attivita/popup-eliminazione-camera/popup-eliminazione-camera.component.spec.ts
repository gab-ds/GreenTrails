import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupEliminazioneCameraComponent } from './popup-eliminazione-camera.component';

describe('PopupEliminazioneCameraComponent', () => {
  let component: PopupEliminazioneCameraComponent;
  let fixture: ComponentFixture<PopupEliminazioneCameraComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopupEliminazioneCameraComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopupEliminazioneCameraComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
