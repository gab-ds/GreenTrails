import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IntroduzioneComponent } from './introduzione.component';

describe('IntroduzioneComponent', () => {
  let component: IntroduzioneComponent;
  let fixture: ComponentFixture<IntroduzioneComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IntroduzioneComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(IntroduzioneComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
