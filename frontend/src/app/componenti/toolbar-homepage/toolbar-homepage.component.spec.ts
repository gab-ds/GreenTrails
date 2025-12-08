import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ToolbarHomepageComponent } from './toolbar-homepage.component';

describe('ToolbarHomepageComponent', () => {
  let component: ToolbarHomepageComponent;
  let fixture: ComponentFixture<ToolbarHomepageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ToolbarHomepageComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ToolbarHomepageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
