import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/util/ui-config.service';
import { PageResponse, PageRequest } from '../model/interface/page.interface';
import { PageUtilService } from '../shared/util/page-util.service';

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
  providedIn: 'root',
})
export class TailLevelService {
  host: string = '';
  private apiPath = '/ninetails/tail-level'; // Base URL for tail level operations

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService,
    private pageUtilService: PageUtilService
  ) {}

  createTailLevel(request: TailLevel): Observable<TailLevelResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.post<TailLevelResponse>(this.host, request);
  }

  getTailLevels(pageRequest: PageRequest): Observable<PageResponse<TailLevelResponse>> {
    let params = this.pageUtilService.getPageRequestParams(pageRequest);
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.get<PageResponse<TailLevelResponse>>(this.host, { params });
  }

  getTailLevelById(id: number): Observable<TailLevelResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.get<TailLevelResponse>(`${this.host}/${id}`);
  }

  updateTailLevel(id: number, request: TailLevel): Observable<TailLevelResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.put<TailLevelResponse>(`${this.host}/${id}`, request);
  }

  deleteTailLevel(id: number): Observable<void> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.delete<void>(`${this.host}/${id}`);
  }
}
