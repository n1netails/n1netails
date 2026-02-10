import { ComponentFixture, TestBed } from '@angular/core/testing';

import { N1TokenModalComponent } from './n1-token-modal.component';

describe('N1TokenModalComponent', () => {
  let component: N1TokenModalComponent;
  let fixture: ComponentFixture<N1TokenModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [N1TokenModalComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(N1TokenModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
