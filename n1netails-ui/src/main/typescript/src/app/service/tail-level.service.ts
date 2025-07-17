import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/ui-config.service';
import { PageResponse, PageRequest } from '../model/interface/page.interface';

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

  getTailLevels(pageRequest: PageRequest): Observable<PageResponse<TailLevelResponse>> {

    let params = new HttpParams()
      .set('pageNumber', pageRequest.pageNumber)
      .set('pageSize', pageRequest.pageSize)
      .set('sortDirection', pageRequest.sortDirection)
      .set('sortBy', pageRequest.sortBy);

    if (pageRequest.searchTerm) {
      params = params.set('searchTerm', pageRequest.searchTerm);
    }

    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.get<PageResponse<TailLevelResponse>>(this.host, { params });
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
