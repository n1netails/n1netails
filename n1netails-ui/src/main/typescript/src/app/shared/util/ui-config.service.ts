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

  async loadConfig(): Promise<void> { // Mark the method as async
    try {
      const config = await firstValueFrom(
        this.http.get<{ n1netailsApiUrl: string }>('/ui/n1netails-config/api-url')
      );
      console.log('Config loaded:', config);
      if (config) this.apiUrl = config.n1netailsApiUrl;
    } catch (error) {
      console.warn('Failed to load API URL from server, using fallback:', this.apiUrl);
    }

    try {
      this.openaiEnabled = await firstValueFrom(
        this.http.get<boolean>('/ui/n1netails-config/openai-enabled')
      );
      console.log('Openai Enabled:', this.openaiEnabled);
    } catch (error) {
      console.warn('Failed to check if openai enabled, using fallback:', this.openaiEnabled);
    }

    try {
      this.geminiEnabled = await firstValueFrom(
        this.http.get<boolean>('/ui/n1netails-config/gemini-enabled')
      );
      console.log('Gemini Enabled:', this.geminiEnabled);
    } catch (error) {
      console.warn('Failed to check if gemini enabled, using fallback:', this.geminiEnabled);
    }

    try {
      this.githubAuthEnabled = await firstValueFrom(
        this.http.get<boolean>('/ui/n1netails-config/github-auth-enabled')
      );
      console.log('Github Auth Enabled:', this.githubAuthEnabled);
    } catch (error) {
      console.warn('Failed to check if github auth enabled, using fallback:', this.githubAuthEnabled);
    }

    try {
      this.notificationsEnabled = await firstValueFrom(
        this.http.get<boolean>('/ui/n1netails-config/notifications-enabled')
      );
      console.log('Notifications Enabled:', this.notificationsEnabled);
    } catch (error) {
      console.warn('Failed to check if notifications enabled, using fallback:', this.notificationsEnabled);
    }
    
    try {
      this.notificationsEmailEnabled = await firstValueFrom(
        this.http.get<boolean>('/ui/n1netails-config/notifications-email-enabled')
      );
      console.log('Notifications Email Enabled:', this.notificationsEmailEnabled);
    } catch (error) {
      console.warn('Failed to check if notifications email enabled, using fallback:', this.notificationsEmailEnabled);
    }

    try {
      this.notificationsMsTeamsEnabled = await firstValueFrom(
        this.http.get<boolean>('/ui/n1netails-config/notifications-msteams-enabled')
      );
      console.log('Notifications Microsoft Teams Enabled:', this.notificationsMsTeamsEnabled);
    } catch (error) {
      console.warn('Failed to check if notifications microsoft teams enabled, using fallback:', this.notificationsMsTeamsEnabled);
    }

    try {
      this.notificationsSlackEnabled = await firstValueFrom(
        this.http.get<boolean>('/ui/n1netails-config/notifications-slack-enabled')
      );
      console.log('Notifications Slack Enabled:', this.notificationsSlackEnabled);
    } catch (error) {
      console.warn('Failed to check if notifications slack enabled, using fallback:', this.notificationsSlackEnabled);
    }

    try {
      this.notificationsDiscordEnabled = await firstValueFrom(
        this.http.get<boolean>('/ui/n1netails-config/notifications-discord-enabled')
      );
      console.log('Notifications Discord Enabled:', this.notificationsDiscordEnabled);
    } catch (error) {
      console.warn('Failed to check if notifications discord enabled, using fallback:', this.notificationsDiscordEnabled);
    }

    try {
      this.notificationsTelegramEnabled = await firstValueFrom(
        this.http.get<boolean>('/ui/n1netails-config/notifications-telegram-enabled')
      );
      console.log('Notifications Telegram Enabled:', this.notificationsTelegramEnabled);
    } catch (error) {
      console.warn('Failed to check if notifications telegram enabled, using fallback:', this.notificationsTelegramEnabled);
    }
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
