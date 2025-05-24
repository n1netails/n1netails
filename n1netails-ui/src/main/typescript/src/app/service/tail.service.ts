import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/ui-config.service';

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

export interface TailResponse {
  id: number;
  title: string;
  description: string;
  timestamp: string;
  resolvedTimestamp: string;
  assignedUserId: string;
  assignedUsername: string;
  details: string;
  level: string;
  type: string;
  status: string;
}

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // current page index
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

  getTails(page: number, size: number): Observable<Page<TailResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Page<TailResponse>>(this.host, { params });
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
