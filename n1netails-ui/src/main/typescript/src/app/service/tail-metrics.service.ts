import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UiConfigService } from '../shared/ui-config.service';
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
  private apiUrl = '/ninetails/metrics/tails'; // Base URL for tail metrics operations

  constructor(
    private http: HttpClient, 
    private uiConfigService: UiConfigService
  ) {
    this.host = this.uiConfigService.getApiUrl();
    this.host = this.host + this.apiUrl;
  }

  countTailAlertsToday(timezone: string): Observable<number> { // Added timezone parameter
    const payload = { timezone: timezone };
    return this.http.post<number>(`${this.host}/today/count`, payload); // Changed to post, added payload
  }

  countTailAlertsResolved(): Observable<number> {
    return this.http.get<number>(`${this.host}/resolved/count`);
  }

  countTailAlertsNotResolved(): Observable<number> {
    return this.http.get<number>(`${this.host}/not-resolved/count`);
  }

  mttr(): Observable<number> {
    return this.http.get<number>(`${this.host}/mttr`);
  }

  mttrLast7Days(): Observable<TailDatasetMttrResponse> {
    return this.http.get<TailDatasetMttrResponse>(`${this.host}/mttr/last-7-days`);
  }

  getTailAlertsHourly(timezone: string): Observable<TailAlertsPerHourResponse> { // Added timezone parameter
    const payload = { timezone: timezone };
    return this.http.post<TailAlertsPerHourResponse>(`${this.host}/hourly`, payload); // Changed to post, added payload
  }

  getTailMonthlySummary(timezone: string): Observable<TailMonthlySummaryResponse> {
    const payload = { timezone: timezone }
    return this.http.post<TailMonthlySummaryResponse>(`${this.host}/monthly-summary`, payload);
  }
}