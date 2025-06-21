export interface Note {
  id?: number;
  // TODO USER ID IS ONLY A NUMBER
  userId: string | number;
  username: string;
  isHuman: boolean;
  llmProvider?: string; // e.g., 'openai', 'gemini'
  llmModel?: string;    // e.g., 'gpt-4.1'
  tailId: number;
  createdAt: Date | string; // ISO string or Date object
  content: string;
  organizationId: number;
  n1: boolean;
}
