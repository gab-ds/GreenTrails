import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CookiedialogComponent } from './cookiedialog.component';

describe('CookiedialogComponent', () => {
  let component: CookiedialogComponent;
  let fixture: ComponentFixture<CookiedialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CookiedialogComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CookiedialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
