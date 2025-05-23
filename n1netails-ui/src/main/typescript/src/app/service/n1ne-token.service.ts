import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

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
  name: string;
  lastUsedAt: string; // ISO 8601 format
  expiresAt?: string; // ISO 8601 format
  // Assuming 'enabled' and 'revoked' fields might also be relevant from a typical token model
  // Add them if they are present in the backend N1neTokenResponse model and needed by the UI
  // enabled: boolean; 
  // revoked: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class N1neTokenService {

  private apiUrl = '/api/n1ne-token';

  constructor(private http: HttpClient) { }

  createToken(tokenRequest: CreateTokenRequest): Observable<N1neTokenResponse> {
    return this.http.post<N1neTokenResponse>(this.apiUrl, tokenRequest);
  }

  getAllTokens(): Observable<N1neTokenResponse[]> {
    return this.http.get<N1neTokenResponse[]>(this.apiUrl);
  }

  getTokenById(id: number): Observable<N1neTokenResponse> {
    return this.http.get<N1neTokenResponse>(`${this.apiUrl}/${id}`);
  }

  revokeToken(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/revoke`, {});
  }

  enableToken(id: number): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${id}/enable`, {});
  }

  deleteToken(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
