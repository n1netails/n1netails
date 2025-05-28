import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UiConfigService } from '../shared/ui-config.service';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TailMetricsService {

  host: string = '';
  private apiUrl = '/api/metrics/tails'; // Base URL for tail metrics operations

  constructor(
    private http: HttpClient, 
    private uiConfigService: UiConfigService
  ) {
    this.host = this.uiConfigService.getApiUrl();
    this.host = this.host + this.apiUrl;
  }

  countTailAlertsToday(): Observable<number> {
    return this.http.get<number>(`${this.host}/today/count`);
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
}