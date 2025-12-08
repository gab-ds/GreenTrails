import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopUpErroriComponent } from './pop-up-errori.component';

describe('PopUpErroriComponent', () => {
  let component: PopUpErroriComponent;
  let fixture: ComponentFixture<PopUpErroriComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopUpErroriComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopUpErroriComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
