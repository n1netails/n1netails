import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/ui-config.service';

// Define interfaces for TailStatus and TailStatusResponse based on Java models

export interface TailStatus {
  status: string; // e.g., 'RUNNING', 'STOPPED', 'INITIALIZING', 'ERROR'
  message?: string; // Optional message, e.g., for errors or detailed status
  tailId: number; // Foreign key to the parent Tail configuration
}

export interface TailStatusResponse {
  id: number;
  status: string;
  message?: string;
  tailId: number;
}

@Injectable({
  providedIn: 'root'
})
export class TailStatusService {

  host: string = '';
  private apiUrl = '/api/tail-status'; // Base URL for tail status operations

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService
  ) {
    this.host = this.uiConfigService.getApiUrl();
    this.host = this.host + this.apiUrl;
  }

  createTailStatus(request: TailStatus): Observable<TailStatusResponse> {
    return this.http.post<TailStatusResponse>(this.host, request);
  }

  getTailStatusList(): Observable<TailStatusResponse[]> {
    return this.http.get<TailStatusResponse[]>(this.host);
  }

  getTailStatusById(id: number): Observable<TailStatusResponse> {
    return this.http.get<TailStatusResponse>(`${this.host}/${id}`);
  }

  updateTailStatus(id: number, request: TailStatus): Observable<TailStatusResponse> {
    return this.http.put<TailStatusResponse>(`${this.host}/${id}`, request);
  }

  deleteTailStatus(id: number): Observable<void> {
    return this.http.delete<void>(`${this.host}/${id}`);
  }
}
