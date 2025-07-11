import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/ui-config.service';

export interface TailType {
  name: string;
  description: string;
  deletable: boolean;
}

export interface TailTypeResponse {
  id: number;
  name: string;
  description: string;
  deletable: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class TailTypeService {

  host: string = '';
  private apiUrl = '/ninetails/tail-type'; // Base URL for tail type operations

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService
  ) {}

  createTailType(request: TailType): Observable<TailTypeResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.post<TailTypeResponse>(this.host, request);
  }

  getTailTypes(): Observable<TailTypeResponse[]> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.get<TailTypeResponse[]>(this.host);
  }

  getTailTypeById(id: number): Observable<TailTypeResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.get<TailTypeResponse>(`${this.host}/${id}`);
  }

  updateTailType(id: number, request: TailType): Observable<TailTypeResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.put<TailTypeResponse>(`${this.host}/${id}`, request);
  }

  deleteTailType(id: number): Observable<void> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.delete<void>(`${this.host}/${id}`);
  }
}
