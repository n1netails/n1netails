import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/ui-config.service';

export interface TailLevel {
  name: string;
  description: string;
  deletable: boolean;
}

export interface TailLevelResponse {
  id: number;
  name: string;
  description: string;
  deletable: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class TailLevelService {

  host: string = '';
  private apiUrl = '/ninetails/tail-level'; // Base URL for tail level operations

  constructor(
    private http: HttpClient, 
    private uiConfigService: UiConfigService
  ) {}

  createTailLevel(request: TailLevel): Observable<TailLevelResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.post<TailLevelResponse>(this.host, request);
  }

  getTailLevels(): Observable<TailLevelResponse[]> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.get<TailLevelResponse[]>(this.host);
  }

  getTailLevelById(id: number): Observable<TailLevelResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.get<TailLevelResponse>(`${this.host}/${id}`);
  }

  updateTailLevel(id: number, request: TailLevel): Observable<TailLevelResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.put<TailLevelResponse>(`${this.host}/${id}`, request);
  }

  deleteTailLevel(id: number): Observable<void> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.delete<void>(`${this.host}/${id}`);
  }
}
