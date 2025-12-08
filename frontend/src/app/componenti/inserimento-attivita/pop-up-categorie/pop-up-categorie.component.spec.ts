import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopUpCategorieComponent } from './pop-up-categorie.component';

describe('PopUpCategorieComponent', () => {
  let component: PopUpCategorieComponent;
  let fixture: ComponentFixture<PopUpCategorieComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopUpCategorieComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopUpCategorieComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
