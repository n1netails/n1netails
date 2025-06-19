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
