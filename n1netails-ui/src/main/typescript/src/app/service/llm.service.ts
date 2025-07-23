import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LlmPromptRequest, LlmPromptResponse } from '../model/llm.model';
import { UiConfigService } from '../shared/util/ui-config.service';

@Injectable({
  providedIn: 'root'
})
export class LlmService {

  public openai = 'openai';
  public openAiModels = ['gpt-4.1'];
  
  public gemini = 'gemini';
  public geminiAiModels = [];

  host: string = '';
  private apiPath = '/ninetails/llm';

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService
  ) {}

  investigateTail(request: LlmPromptRequest): Observable<LlmPromptResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.post<LlmPromptResponse>(`${this.host}/investigate`, request);
  }

  sendPrompt(request: LlmPromptRequest): Observable<LlmPromptResponse> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.post<LlmPromptResponse>(`${this.host}/prompt`, request);
  }

  isOpenaiEnabled(): boolean {
    return this.uiConfigService.isOpenaiEnabled();
  }

  isGeminiEnabled(): boolean {
    return this.uiConfigService.isGeminiEnabled();
  }
}
