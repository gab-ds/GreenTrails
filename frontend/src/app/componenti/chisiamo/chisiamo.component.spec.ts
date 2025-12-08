import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChisiamoComponent } from './chisiamo.component';

describe('ChisiamoComponent', () => {
  let component: ChisiamoComponent;
  let fixture: ComponentFixture<ChisiamoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChisiamoComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChisiamoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
