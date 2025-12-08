import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PopUpQuestionarioComponent } from './pop-up-questionario.component';

describe('PopUpQuestionarioComponent', () => {
  let component: PopUpQuestionarioComponent;
  let fixture: ComponentFixture<PopUpQuestionarioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PopUpQuestionarioComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PopUpQuestionarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
