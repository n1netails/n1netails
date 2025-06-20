export interface Note {
  id?: string | number; // Optional: Will be assigned by the backend
  userId: string | number;
  username: string;
  isHuman: boolean;
  llmProvider?: string; // e.g., 'openai', 'gemini'
  llmModel?: string;    // e.g., 'gpt-4.1'
  tailId: number;
  timestamp: Date | string; // ISO string or Date object
  noteText: string;
  organizationId: number;
}
