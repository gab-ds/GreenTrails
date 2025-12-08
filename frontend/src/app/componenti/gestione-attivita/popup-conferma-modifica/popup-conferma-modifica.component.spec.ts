import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupConfermaModificaComponent } from './popup-conferma-modifica.component';

describe('PopupConfermaModificaComponent', () => {
  let component: PopupConfermaModificaComponent;
  let fixture: ComponentFixture<PopupConfermaModificaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopupConfermaModificaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopupConfermaModificaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
