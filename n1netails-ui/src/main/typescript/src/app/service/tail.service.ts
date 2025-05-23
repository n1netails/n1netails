import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/ui-config.service';

// Define interfaces for request and response objects based on TailRequest and TailResponse in Java

export interface TailRequest {
  name: string;
  filePath: string;
  filter?: string; // Optional filter
}

export interface TailResponse {
  id: number;
  name: string;
  filePath: string;
  filter?: string;
  status: string; // e.g., 'RUNNING', 'STOPPED', 'ERROR'
}

@Injectable({
  providedIn: 'root'
})
export class TailService {

  host: string = '';
  private apiUrl = '/api/tail'; // Base URL for tail operations

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
}
