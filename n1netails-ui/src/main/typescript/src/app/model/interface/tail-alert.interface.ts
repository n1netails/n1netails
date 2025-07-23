export interface TailAlert {
  title?: string;
  description?: string;
  details?: string;
  level?: string;
  type?: string;
  metadata?: { [key: string]: string };
}
