import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/util/ui-config.service';

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

  host: string = '';
  private apiPath = '/ninetails/notifications';

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService
  ) {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
  }

  getConfigurations(tokenId: number): Observable<NotificationConfig[]> {
    return this.http.get<NotificationConfig[]>(`${this.host}/save/config/${tokenId}`);
  }

  saveConfigurations(tokenId: number, configs: NotificationConfig[]): Observable<void> {
    return this.http.post<void>(`${this.host}/save/config/${tokenId}`, configs);
  }

  getUserNotificationPreferences(userId: number): Observable<string[]> {
    return this.http.get<string[]>(`${this.host}/user/${userId}/preferences`);
  }

  saveUserNotificationPreferences(userId: number, notificationPreferences: string[]): Observable<void> {
    return this.http.post<void>(`${this.host}/user/${userId}/preferences`, notificationPreferences);
  }
}
