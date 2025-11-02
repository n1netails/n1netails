import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type NotificationPlatform = 'email' | 'msteams' | 'slack' | 'discord' | 'telegram';

export interface NotificationConfig {
  tokenId: number;
  platform: NotificationPlatform;
  details: any;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = '/api/notifications';

  constructor(private http: HttpClient) {}

  getConfigurations(tokenId: number): Observable<NotificationConfig[]> {
    return this.http.get<NotificationConfig[]>(`${this.apiUrl}/${tokenId}`);
  }

  saveConfigurations(tokenId: number, configs: NotificationConfig[]): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${tokenId}`, configs);
  }
}
