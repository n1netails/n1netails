import { TestBed } from '@angular/core/testing';

import { TailService } from './tail.service';

describe('TailService', () => {
  let service: TailService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TailService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
