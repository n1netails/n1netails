import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/ui-config.service';

export interface CreateTokenRequest {
  userId: number;
  organizationId?: number; // Made optional
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
  // Assuming 'enabled' and 'revoked' fields might also be relevant from a typical token model
  // Add them if they are present in the backend N1neTokenResponse model and needed by the UI
  // enabled: boolean; 
  revoked: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class N1neTokenService {

  host: string = '';
  private apiUrl = '/api/n1ne-token';

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService
  ) { 
    this.host = this.uiConfigService.getApiUrl();
    this.host = this.host + this.apiUrl;
  }

  createToken(tokenRequest: CreateTokenRequest): Observable<N1neTokenResponse> {
    return this.http.post<N1neTokenResponse>(this.host, tokenRequest);
  }

  getAllTokens(): Observable<N1neTokenResponse[]> {
    return this.http.get<N1neTokenResponse[]>(this.host);
  }

  getTokenById(id: number): Observable<N1neTokenResponse> {
    return this.http.get<N1neTokenResponse>(`${this.host}/${id}`);
  }

  revokeToken(id: number): Observable<void> {
    return this.http.put<void>(`${this.host}/${id}/revoke`, {});
  }

  enableToken(id: number): Observable<void> {
    return this.http.put<void>(`${this.host}/${id}/enable`, {});
  }

  deleteToken(id: number): Observable<void> {
    return this.http.delete<void>(`${this.host}/${id}`);
  }
}
