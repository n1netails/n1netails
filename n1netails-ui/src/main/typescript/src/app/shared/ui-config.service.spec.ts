import { TestBed } from '@angular/core/testing';

import { UiConfigService } from './ui-config.service';

describe('UiConfigService', () => {
  let service: UiConfigService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UiConfigService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
