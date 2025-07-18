import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/ui-config.service';
import { PageUtilService } from '../shared/page-util.service';
import { PageRequest, PageResponse } from '../model/interface/page.interface';

export interface CreateTokenRequest {
  userId: number;
  organizationId?: number;
  name: string;
  expiresAt?: string; // ISO 8601 format
}

export interface N1neTokenResponse {
  id: number;
  userId: number;
  organizationId: number;
  token: string;
  name: string;
  lastUsedAt: string; // ISO 8601 format
  expiresAt?: string; // ISO 8601 format
  createdAt?: string; // ISO 8601 format
  revoked: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class N1neTokenService {

  host: string = '';
  private apiUrl = '/ninetails/n1ne-token';

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService,
    private pageUtilService: PageUtilService
  ) {}

  createToken(tokenRequest: CreateTokenRequest): Observable<N1neTokenResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.post<N1neTokenResponse>(this.host, tokenRequest);
  }

  // getAllTokens(): Observable<N1neTokenResponse[]> {
  //   this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
  //   return this.http.get<N1neTokenResponse[]>(this.host);
  // }

  getAllTokensByUserId(userId: number, pageRequest: PageRequest): Observable<PageResponse<N1neTokenResponse>> {
    let params = this.pageUtilService.getPageRequestParams(pageRequest);
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.get<PageResponse<N1neTokenResponse>>(`${this.host}/user-tokens/${userId}`, { params });
  }

  getTokenById(id: number): Observable<N1neTokenResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.get<N1neTokenResponse>(`${this.host}/${id}`);
  }

  revokeToken(id: number): Observable<void> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.put<void>(`${this.host}/revoke/${id}`, {});
  }

  enableToken(id: number): Observable<void> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.put<void>(`${this.host}/enable/${id}`, {});
  }

  deleteToken(id: number): Observable<void> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.delete<void>(`${this.host}/${id}`);
  }
}
