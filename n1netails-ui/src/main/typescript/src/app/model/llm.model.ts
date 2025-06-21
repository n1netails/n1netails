export interface LlmRequest {
  provider: string;
  model: string;
  tailId: number;
  userId: number;
  organizationId: number;
}

export interface LlmResponse {
  promptCompletionResponse: string;
  tailId: number;
}

// Request for general LLM prompting
export interface LlmPromptRequest {
  provider: string; // e.g., 'openai', 'gemini'
  model: string;    // e.g., 'gpt-4.1', 'gemini-pro'
  prompt: string;
  userId: string | number;
  organizationId: number;
  tailId?: number; // Optional, if the prompt is related to a specific tail
}

// Response for general LLM prompting
export interface LlmPromptResponse {
  provider: string;
  model: string;
  completion: string;
  timestamp: Date | string;
  userId?: string | number; // User who initiated, if applicable
  organizationId?: number;
  tailId?: number;
}
