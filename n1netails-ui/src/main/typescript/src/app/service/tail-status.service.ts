import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/ui-config.service';
import { PageRequest, PageResponse } from '../model/interface/page.interface';
import { PageUtilService } from '../shared/page-util.service';

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
    private uiConfigService: UiConfigService,
    private pageUtilService: PageUtilService
  ) {}

  createTailStatus(request: TailStatus): Observable<TailStatusResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.post<TailStatusResponse>(this.host, request);
  }

  getTailStatusList(pageRequest: PageRequest): Observable<PageResponse<TailStatusResponse>> {
    let params = this.pageUtilService.getPageRequestParams(pageRequest);
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.get<PageResponse<TailStatusResponse>>(this.host, { params });
  }

  getTailStatusById(id: number): Observable<TailStatusResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.get<TailStatusResponse>(`${this.host}/${id}`);
  }

  updateTailStatus(id: number, request: TailStatus): Observable<TailStatusResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.put<TailStatusResponse>(`${this.host}/${id}`, request);
  }

  deleteTailStatus(id: number): Observable<void> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.delete<void>(`${this.host}/${id}`);
  }
}
