import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/util/ui-config.service';
import { PageRequest, PageResponse } from '../model/interface/page.interface';
import { PageUtilService } from '../shared/util/page-util.service';

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
  private apiPath = '/ninetails/tail-type'; // Base URL for tail type operations

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService,
    private pageUtilService: PageUtilService
  ) {}

  createTailType(request: TailType): Observable<TailTypeResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.post<TailTypeResponse>(this.host, request);
  }

  getTailTypes(pageRequest: PageRequest): Observable<PageResponse<TailTypeResponse>> {
    let params = this.pageUtilService.getPageRequestParams(pageRequest);
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.get<PageResponse<TailTypeResponse>>(this.host, { params });
  }

  getTailTypeById(id: number): Observable<TailTypeResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.get<TailTypeResponse>(`${this.host}/${id}`);
  }

  updateTailType(id: number, request: TailType): Observable<TailTypeResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.put<TailTypeResponse>(`${this.host}/${id}`, request);
  }

  deleteTailType(id: number): Observable<void> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.delete<void>(`${this.host}/${id}`);
  }
}
