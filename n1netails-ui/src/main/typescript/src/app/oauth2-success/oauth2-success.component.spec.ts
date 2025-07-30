import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Oauth2SuccessComponent } from './oauth2-success.component';

describe('Oauth2SuccessComponent', () => {
  let component: Oauth2SuccessComponent;
  let fixture: ComponentFixture<Oauth2SuccessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Oauth2SuccessComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Oauth2SuccessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
