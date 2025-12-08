import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModificaValoriAdminComponent } from './modifica-valori-admin.component';

describe('ModificaValoriAdminComponent', () => {
  let component: ModificaValoriAdminComponent;
  let fixture: ComponentFixture<ModificaValoriAdminComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModificaValoriAdminComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ModificaValoriAdminComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
