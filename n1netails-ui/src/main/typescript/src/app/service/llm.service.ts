import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LlmRequest, LlmResponse } from '../model/llm.model';
import { UiConfigService } from '../shared/ui-config.service';

@Injectable({
  providedIn: 'root'
})
export class LlmService {

  host: string = '';
  private apiUrl = '/ninetails/llm'; // Base URL for LLM operations

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService
  ) {
    this.host = this.uiConfigService.getApiUrl();
    this.host = this.host + this.apiUrl;
  }

  investigateTail(request: LlmRequest): Observable<LlmResponse> {
    // Assuming an HttpInterceptor handles the Authorization header
    return this.http.post<LlmResponse>(this.host, request);
  }
}
