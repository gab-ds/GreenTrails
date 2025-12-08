import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupModificaAlloggioComponent } from './popup-modifica-alloggio.component';

describe('PopupModificaAlloggioComponent', () => {
  let component: PopupModificaAlloggioComponent;
  let fixture: ComponentFixture<PopupModificaAlloggioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopupModificaAlloggioComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopupModificaAlloggioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
