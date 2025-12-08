import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EffettuataComponent } from './effettuata.component';

describe('EffettuataComponent', () => {
  let component: EffettuataComponent;
  let fixture: ComponentFixture<EffettuataComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EffettuataComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EffettuataComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
