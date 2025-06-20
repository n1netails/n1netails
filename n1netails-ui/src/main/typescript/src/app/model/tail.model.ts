import { Organization } from './organization'; // Assuming Organization is in the same model folder

export interface TailResponse {
  id: number;
  title: string;
  description: string;
  timestamp: string;
  resolvedTimestamp: string;
  assignedUserId: string;
  assignedUsername: string;
  details: string;
  level: string;
  type: string;
  status: string;
  metadata: { [key: string]: string };
  organizationId: number; // This implies Organization might be relevant or part of what organizationId refers to.
}

export interface TailSummary {
  id: number;
  title: string;
  description: string;
  timestamp: string;
  resolvedtimestamp: string; // Note: Typo in original? 'resolvedTimestamp' vs 'resolvedtimestamp'
  assignedUserId: number;
  level: string;
  type: string;
  status: string;
}

export interface ResolveTailRequest {
  userId: number,
  tailSummary: TailSummary;
  note: string;
}
