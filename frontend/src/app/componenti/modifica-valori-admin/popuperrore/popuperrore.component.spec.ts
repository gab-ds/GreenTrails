import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopuperroreComponent } from './popuperrore.component';

describe('PopuperroreComponent', () => {
  let component: PopuperroreComponent;
  let fixture: ComponentFixture<PopuperroreComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopuperroreComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopuperroreComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
