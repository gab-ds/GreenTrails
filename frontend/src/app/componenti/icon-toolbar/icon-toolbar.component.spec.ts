import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IconToolbarComponent } from './icon-toolbar.component';

describe('IconToolbarComponent', () => {
  let component: IconToolbarComponent;
  let fixture: ComponentFixture<IconToolbarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IconToolbarComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IconToolbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
