import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UiConfigService } from '../shared/util/ui-config.service';
import { Observable } from 'rxjs';

export interface TailAlertsPerHourResponse {
  labels: string[];
  data: number[];
}

export interface TailDatasetMttrResponse {
  labels: string[];
  data: number[];
}

export interface TailMonthlySummaryResponse {
  labels: string[];
  datasets: TailDatasetResponse[];
}

export interface TailDatasetResponse {
  label: string;
  data: number[];
}

@Injectable({
  providedIn: 'root'
})
export class TailMetricsService {

  host: string = '';
  private apiPath = '/ninetails/metrics/tails'; // Base URL for tail metrics operations

  constructor(
    private http: HttpClient, 
    private uiConfigService: UiConfigService
  ) {}

  countTailAlertsToday(timezone: string): Observable<number> { // Added timezone parameter
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    const payload = { timezone: timezone };
    return this.http.post<number>(`${this.host}/today/count`, payload); // Changed to post, added payload
  }

  countTailAlertsResolved(): Observable<number> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.get<number>(`${this.host}/resolved/count`);
  }

  countTailAlertsNotResolved(): Observable<number> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.get<number>(`${this.host}/not-resolved/count`);
  }

  mttr(): Observable<number> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.get<number>(`${this.host}/mttr`);
  }

  mttrLast7Days(): Observable<TailDatasetMttrResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.get<TailDatasetMttrResponse>(`${this.host}/mttr/last-7-days`);
  }

  getTailAlertsHourly(timezone: string): Observable<TailAlertsPerHourResponse> { // Added timezone parameter
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    const payload = { timezone: timezone };
    return this.http.post<TailAlertsPerHourResponse>(`${this.host}/hourly`, payload); // Changed to post, added payload
  }

  getTailMonthlySummary(timezone: string): Observable<TailMonthlySummaryResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    const payload = { timezone: timezone }
    return this.http.post<TailMonthlySummaryResponse>(`${this.host}/monthly-summary`, payload);
  }
}