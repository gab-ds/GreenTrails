import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopUpConfermaComponent } from './pop-up-conferma.component';

describe('PopUpConfermaComponent', () => {
  let component: PopUpConfermaComponent;
  let fixture: ComponentFixture<PopUpConfermaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopUpConfermaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopUpConfermaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
