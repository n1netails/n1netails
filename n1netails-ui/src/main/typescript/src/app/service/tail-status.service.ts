import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/ui-config.service';

export interface TailStatus {
  name: string;
  deletable: boolean;
}

export interface TailStatusResponse {
  id: number;
  name: string;
  deletable: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class TailStatusService {

  host: string = '';
  private apiUrl = '/ninetails/tail-status'; // Base URL for tail status operations

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
