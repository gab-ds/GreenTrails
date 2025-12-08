import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopupModificaCategorieComponent } from './popup-modifica-categorie.component';

describe('PopupModificaCategorieComponent', () => {
  let component: PopupModificaCategorieComponent;
  let fixture: ComponentFixture<PopupModificaCategorieComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopupModificaCategorieComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopupModificaCategorieComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
