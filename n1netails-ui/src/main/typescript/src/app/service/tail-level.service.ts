import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/ui-config.service';

export interface TailLevel {
  name: string;
  description: string;
}

export interface TailLevelResponse {
  id: number;
  name: string;
  description: string;
}

@Injectable({
  providedIn: 'root'
})
export class TailLevelService {

  host: string = '';
  private apiUrl = '/api/tail-level'; // Base URL for tail level operations

  constructor(
    private http: HttpClient, 
    private uiConfigService: UiConfigService
  ) {
    this.host = this.uiConfigService.getApiUrl();
    this.host = this.host + this.apiUrl;
  }

  createTailLevel(request: TailLevel): Observable<TailLevelResponse> {
    return this.http.post<TailLevelResponse>(this.host, request);
  }

  getTailLevels(): Observable<TailLevelResponse[]> {
    return this.http.get<TailLevelResponse[]>(this.host);
  }

  getTailLevelById(id: number): Observable<TailLevelResponse> {
    return this.http.get<TailLevelResponse>(`${this.host}/${id}`);
  }

  updateTailLevel(id: number, request: TailLevel): Observable<TailLevelResponse> {
    return this.http.put<TailLevelResponse>(`${this.host}/${id}`, request);
  }

  deleteTailLevel(id: number): Observable<void> {
    return this.http.delete<void>(`${this.host}/${id}`);
  }
}
