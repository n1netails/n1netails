import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UiConfigService {

  private apiUrl: string = environment.n1netailsApiUrl;
  private openaiEnabled: boolean = environment.openaiEnabled;
  private geminiEnabled: boolean = environment.geminiEnabled;

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
}
