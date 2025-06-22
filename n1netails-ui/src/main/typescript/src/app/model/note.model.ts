export interface Note {
  id?: number;
  tailId: number;
  organizationId: number;
  userId: number;
  username: string;
  human: boolean;
  n1: boolean;
  llmProvider?: string; // e.g., 'openai', 'gemini'
  llmModel?: string;    // e.g., 'gpt-4.1'
  createdAt: Date | string; // ISO string or Date object
  content: string;
}
