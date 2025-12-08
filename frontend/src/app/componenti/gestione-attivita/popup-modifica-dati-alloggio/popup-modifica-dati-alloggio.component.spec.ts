import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupModificaDatiAlloggioComponent } from './popup-modifica-dati-alloggio.component';

describe('PopupModificaDatiAlloggioComponent', () => {
  let component: PopupModificaDatiAlloggioComponent;
  let fixture: ComponentFixture<PopupModificaDatiAlloggioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopupModificaDatiAlloggioComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopupModificaDatiAlloggioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
