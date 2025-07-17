import { TestBed } from '@angular/core/testing';

import { PageUtilService } from './page-util.service';

describe('PageUtilService', () => {
  let service: PageUtilService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PageUtilService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
