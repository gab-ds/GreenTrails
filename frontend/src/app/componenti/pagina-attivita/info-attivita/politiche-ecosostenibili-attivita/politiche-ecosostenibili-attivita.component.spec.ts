import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PoliticheEcosostenibiliAttivitaComponent } from './politiche-ecosostenibili-attivita.component';

describe('PoliticheEcosostenibiliAttivitaComponent', () => {
  let component: PoliticheEcosostenibiliAttivitaComponent;
  let fixture: ComponentFixture<PoliticheEcosostenibiliAttivitaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PoliticheEcosostenibiliAttivitaComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PoliticheEcosostenibiliAttivitaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
