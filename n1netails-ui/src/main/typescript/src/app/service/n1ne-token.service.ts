import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/ui-config.service';

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
    private uiConfigService: UiConfigService
  ) {}

  createToken(tokenRequest: CreateTokenRequest): Observable<N1neTokenResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.post<N1neTokenResponse>(this.host, tokenRequest);
  }

  getAllTokens(): Observable<N1neTokenResponse[]> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.get<N1neTokenResponse[]>(this.host);
  }

  getAllTokensByUserId(userId: number): Observable<N1neTokenResponse[]> {
    this.host = this.uiConfigService.getApiUrl() + this.apiUrl;
    return this.http.get<N1neTokenResponse[]>(`${this.host}/user-tokens/${userId}`);
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
