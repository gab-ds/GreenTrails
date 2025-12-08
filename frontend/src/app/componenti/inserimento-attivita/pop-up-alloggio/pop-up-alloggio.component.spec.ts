import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopUpAlloggioComponent } from './pop-up-alloggio.component';

describe('PopUpAlloggioComponent', () => {
  let component: PopUpAlloggioComponent;
  let fixture: ComponentFixture<PopUpAlloggioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopUpAlloggioComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopUpAlloggioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
