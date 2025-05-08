import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UiConfigService {

  private apiUrl: string = environment.n1netailsApiUrl;

  constructor(private http: HttpClient) {}

  async loadConfig(): Promise<void> { // Mark the method as async
    try {
      const config = await firstValueFrom(
        this.http.get<{ n1netailsApiUrl: string }>('/ui/n1netails-config/api-url')
      );
      if (config) this.apiUrl = config.n1netailsApiUrl;
    } catch (error) {
      console.warn('Failed to load API URL from server, using fallback:', this.apiUrl);
    }
  }

  getApiUrl(): string {
    return this.apiUrl;
  }
}
