import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupRecensioneComponent } from './popup-recensione.component';

describe('PopupRecensioneComponent', () => {
  let component: PopupRecensioneComponent;
  let fixture: ComponentFixture<PopupRecensioneComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopupRecensioneComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopupRecensioneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
