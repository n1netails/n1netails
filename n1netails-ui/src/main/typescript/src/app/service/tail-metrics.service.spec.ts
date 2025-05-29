import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TailMetricsService, TailAlertsPerHourResponse } from './tail-metrics.service';
import { UiConfigService } from '../shared/ui-config.service';
import { of } from 'rxjs';

describe('TailMetricsService', () => {
  let service: TailMetricsService;
  let httpMock: HttpTestingController;
  let uiConfigServiceMock: Partial<UiConfigService>;

  const mockApiUrl = 'http://localhost:8080';

  beforeEach(() => {
    uiConfigServiceMock = {
      getApiUrl: () => mockApiUrl
    };

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [
        TailMetricsService,
        { provide: UiConfigService, useValue: uiConfigServiceMock }
      ]
    });

    service = TestBed.inject(TailMetricsService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Make sure that there are no outstanding requests.
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('countTailAlertsToday', () => {
    it('should call httpClient.post with the correct URL and payload', () => {
      const testTimezone = 'America/Los_Angeles';
      const expectedUrl = `${mockApiUrl}/api/metrics/tails/today/count`;
      const expectedPayload = { timezone: testTimezone };
      const mockResponse = 123;

      service.countTailAlertsToday(testTimezone).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(expectedPayload);
      req.flush(mockResponse);
    });
  });

  describe('getTailAlertsHourly', () => {
    it('should call httpClient.post with the correct URL and payload', () => {
      const testTimezone = 'Europe/Berlin';
      const expectedUrl = `${mockApiUrl}/api/metrics/tails/hourly`;
      const expectedPayload = { timezone: testTimezone };
      const mockResponse: TailAlertsPerHourResponse = { labels: ['00:00'], data: [5] };

      service.getTailAlertsHourly(testTimezone).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(expectedUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(expectedPayload);
      req.flush(mockResponse);
    });
  });

  // Keep other existing tests for countTailAlertsResolved, countTailAlertsNotResolved, mttr if any
  // For example:
  it('countTailAlertsResolved should call http.get with the correct URL', () => {
    const mockCount = 10;
    service.countTailAlertsResolved().subscribe(count => expect(count).toBe(mockCount));
    const req = httpMock.expectOne(`${mockApiUrl}/api/metrics/tails/resolved/count`);
    expect(req.request.method).toBe('GET');
    req.flush(mockCount);
  });

  it('countTailAlertsNotResolved should call http.get with the correct URL', () => {
    const mockCount = 5;
    service.countTailAlertsNotResolved().subscribe(count => expect(count).toBe(mockCount));
    const req = httpMock.expectOne(`${mockApiUrl}/api/metrics/tails/not-resolved/count`);
    expect(req.request.method).toBe('GET');
    req.flush(mockCount);
  });

  it('mttr should call http.get with the correct URL', () => {
    const mockMttr = 3600;
    service.mttr().subscribe(mttr => expect(mttr).toBe(mockMttr));
    const req = httpMock.expectOne(`${mockApiUrl}/api/metrics/tails/mttr`);
    expect(req.request.method).toBe('GET');
    req.flush(mockMttr);
  });

});
