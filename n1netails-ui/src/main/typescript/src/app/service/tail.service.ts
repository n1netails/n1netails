import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/ui-config.service';
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
  private apiUrl = '/ninetails/tail'; // Base URL for tail operations

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService
  ) { 
    this.host = this.uiConfigService.getApiUrl();
    this.host = this.host + this.apiUrl;
  }

  createTail(request: TailRequest): Observable<TailResponse> {
    return this.http.post<TailResponse>(this.host, request);
  }

  getTails(): Observable<TailResponse[]> {
    return this.http.get<TailResponse[]>(this.host);
  }

  getTailById(id: number): Observable<TailResponse> {
    return this.http.get<TailResponse>(`${this.host}/${id}`);
  }

  updateTail(id: number, request: TailRequest): Observable<TailResponse> {
    return this.http.put<TailResponse>(`${this.host}/${id}`, request);
  }

  deleteTail(id: number): Observable<void> {
    return this.http.delete<void>(`${this.host}/${id}`);
  }

  getTop9NewestTails(): Observable<TailResponse[]> {
    return this.http.get<TailResponse[]>(`${this.host}/top9`);
  }

  markTailResolved(resolvedTailRequest: ResolveTailRequest): Observable<void> {
    return this.http.post<void>(`${this.host}/mark/resolved`, resolvedTailRequest);
  }
}
