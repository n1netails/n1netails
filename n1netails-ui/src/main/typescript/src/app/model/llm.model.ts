// Request for general LLM prompting
export interface LlmPromptRequest {
  provider: string; // e.g., 'openai', 'gemini'
  model: string;    // e.g., 'gpt-4.1', 'gemini-pro'
  prompt: string;
  userId: number;
  organizationId: number;
  tailId?: number; // Optional, if the prompt is related to a specific tail
}

// Response for general LLM prompting
export interface LlmPromptResponse {
  provider: string;
  model: string;
  completion: string;
  timestamp: Date | string;
  userId?: number; // User who initiated, if applicable
  organizationId?: number;
  tailId?: number;
}
