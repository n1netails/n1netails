import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UiConfigService {

  private apiUrl: string = environment.n1netailsApiUrl;
  private openaiEnabled: boolean = environment.openaiEnabled;
  private geminiEnabled: boolean = environment.geminiEnabled;
  private githubAuthEnabled: boolean = environment.githubAuthEnabled;

  private notificationsEnabled: boolean = environment.notificationsEnabled;
  private notificationsEmailEnabled: boolean = environment.notificationsEmailEnabled;
  private notificationsMsTeamsEnabled: boolean = environment.notificationsMsTeamsEnabled;
  private notificationsSlackEnabled: boolean = environment.notificationsSlackEnabled;
  private notificationsDiscordEnabled: boolean = environment.notificationsDiscordEnabled;
  private notificationsTelegramEnabled: boolean = environment.notificationsTelegramEnabled;

  constructor(private http: HttpClient) {}

  async loadConfig(): Promise<void> {
    const fetchBoolean = async (path: string, fallback: boolean): Promise<boolean> => {
      try {
        return await firstValueFrom(this.http.get<boolean>(path));
      } catch (err) {
        console.warn(`Failed to load ${path}, using fallback:`, fallback);
        return fallback;
      }
    };

    // load API url (single request, keep separate to preserve shape and logs)
    try {
      const config = await firstValueFrom(
        this.http.get<{ n1netailsApiUrl: string }>('/ui/n1netails-config/api-url')
      );
      console.log('Config loaded:', config);
      if (config?.n1netailsApiUrl) this.apiUrl = config.n1netailsApiUrl;
    } catch (error) {
      console.warn('Failed to load API URL from server, using fallback:', this.apiUrl);
    }

    // boolean feature flags - fetch in parallel
    const endpoints = [
      { prop: 'openaiEnabled', path: '/ui/n1netails-config/openai-enabled' },
      { prop: 'geminiEnabled', path: '/ui/n1netails-config/gemini-enabled' },
      { prop: 'githubAuthEnabled', path: '/ui/n1netails-config/github-auth-enabled' },
      { prop: 'notificationsEnabled', path: '/ui/n1netails-config/notifications-enabled' },
      { prop: 'notificationsEmailEnabled', path: '/ui/n1netails-config/notifications-email-enabled' },
      { prop: 'notificationsMsTeamsEnabled', path: '/ui/n1netails-config/notifications-msteams-enabled' },
      { prop: 'notificationsSlackEnabled', path: '/ui/n1netails-config/notifications-slack-enabled' },
      { prop: 'notificationsDiscordEnabled', path: '/ui/n1netails-config/notifications-discord-enabled' },
      { prop: 'notificationsTelegramEnabled', path: '/ui/n1netails-config/notifications-telegram-enabled' }
    ];

    const results = await Promise.all(
      endpoints.map(e => fetchBoolean(e.path, (this as any)[e.prop]))
    );

    endpoints.forEach((e, idx) => {
      (this as any)[e.prop] = results[idx];
      console.log(`${e.prop}:`, results[idx]);
    });
  }

  getApiUrl(): string {
    return this.apiUrl;
  }

  isOpenaiEnabled(): boolean {
    return this.openaiEnabled;
  }

  isGeminiEnabled(): boolean {
    return this.geminiEnabled;
  }

  isGithubAuthEnabled(): boolean {
    return this.githubAuthEnabled;
  }

  isNotificationsEnabled(): boolean {
    return this.notificationsEnabled;
  }

  isNotificationsEmailEnabled(): boolean {
    return this.notificationsEmailEnabled;
  }

  isNotificationsMsTeamsEnabled(): boolean {
    return this.notificationsMsTeamsEnabled;
  }

  isNotificationsSlackEnabled(): boolean {
    return this.notificationsSlackEnabled;
  }

  isNotificationsDiscordEnabled(): boolean {
    return this.notificationsDiscordEnabled;
  }

  isNotificationsTelegramEnabled(): boolean {
    return this.notificationsTelegramEnabled;
  }
}
