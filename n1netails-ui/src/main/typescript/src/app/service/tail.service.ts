import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/util/ui-config.service';
import { TailResponse, TailSummary, ResolveTailRequest } from '../model/tail.model';

export interface TailRequest {
  title: string;
  description: string;
  details: string;
  timestamp: string;
  resolvedTimestamp: string;
  assignedUserId: string;
  status: string;
  level: string;
  type: string;
  metadata: { [key: string]: string };
}

@Injectable({
  providedIn: 'root'
})
export class TailService {

  host: string = '';
  private apiPath = '/ninetails/tail'; // Base URL for tail operations

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService
  ) {}

  createTail(request: TailRequest): Observable<TailResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.post<TailResponse>(this.host, request);
  }

  getTails(): Observable<TailResponse[]> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.get<TailResponse[]>(this.host);
  }

  getTailById(id: number): Observable<TailResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.get<TailResponse>(`${this.host}/${id}`);
  }

  updateTail(id: number, request: TailRequest): Observable<TailResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.put<TailResponse>(`${this.host}/${id}`, request);
  }

  deleteTail(id: number): Observable<void> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.delete<void>(`${this.host}/${id}`);
  }

  getTop9NewestTails(): Observable<TailResponse[]> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.get<TailResponse[]>(`${this.host}/top9`);
  }

  markTailResolved(resolvedTailRequest: ResolveTailRequest): Observable<void> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.post<void>(`${this.host}/mark/resolved`, resolvedTailRequest);
  }

  updateTailStatus(updateTailRequest: ResolveTailRequest): Observable<void> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.post<void>(`${this.host}/mark/status`, updateTailRequest);
  }
}
