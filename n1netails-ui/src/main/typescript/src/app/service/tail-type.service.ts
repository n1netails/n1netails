import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/ui-config.service';

export interface TailType {
  name: string;
  description: string;
}

export interface TailTypeResponse {
  id: number;
  name: string;
  description: string;
}

@Injectable({
  providedIn: 'root'
})
export class TailTypeService {

  host: string = '';
  private apiUrl = '/api/tail-type'; // Base URL for tail type operations

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService
  ) { 
    this.host = this.uiConfigService.getApiUrl();
    this.host = this.host + this.apiUrl;
  }

  createTailType(request: TailType): Observable<TailTypeResponse> {
    return this.http.post<TailTypeResponse>(this.host, request);
  }

  getTailTypes(): Observable<TailTypeResponse[]> {
    return this.http.get<TailTypeResponse[]>(this.host);
  }

  getTailTypeById(id: number): Observable<TailTypeResponse> {
    return this.http.get<TailTypeResponse>(`${this.host}/${id}`);
  }

  updateTailType(id: number, request: TailType): Observable<TailTypeResponse> {
    return this.http.put<TailTypeResponse>(`${this.host}/${id}`, request);
  }

  deleteTailType(id: number): Observable<void> {
    return this.http.delete<void>(`${this.host}/${id}`);
  }
}
