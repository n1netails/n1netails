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
  organizationId: number;
}

export interface TailSummary {
  id: number;
  title: string;
  description: string;
  timestamp: string;
  resolvedTimestamp: string;
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
