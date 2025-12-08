import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PoliticheEcoComponent } from './politiche-eco.component';

describe('PoliticheEcoComponent', () => {
  let component: PoliticheEcoComponent;
  let fixture: ComponentFixture<PoliticheEcoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PoliticheEcoComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PoliticheEcoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
