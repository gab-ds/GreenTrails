import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CalendariopopupComponent } from './calendariopopup.component';

describe('CalendariopopupComponent', () => {
  let component: CalendariopopupComponent;
  let fixture: ComponentFixture<CalendariopopupComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CalendariopopupComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CalendariopopupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
