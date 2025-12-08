import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupEliminazioneCategorieComponent } from './popup-eliminazione-categorie.component';

describe('PopupEliminazioneCategorieComponent', () => {
  let component: PopupEliminazioneCategorieComponent;
  let fixture: ComponentFixture<PopupEliminazioneCategorieComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopupEliminazioneCategorieComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopupEliminazioneCategorieComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
