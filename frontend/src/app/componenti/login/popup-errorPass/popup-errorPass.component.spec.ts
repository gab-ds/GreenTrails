/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { DebugElement } from '@angular/core';

import { PopupErrorPassComponent } from './popup-errorPass.component';

describe('PopupErrorPassComponent', () => {
  let component: PopupErrorPassComponent;
  let fixture: ComponentFixture<PopupErrorPassComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PopupErrorPassComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PopupErrorPassComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
