export interface TailPageRequest {
  page: number;
  size: number;
  searchTerm?: string;
  filterByStatus?: string;
  filterByType?: string;
  filterByLevel?: string;
}

export interface TailPageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // current page number
}

// export interface Tail {
//   id: number;
//   title: string;
//   description?: string;
//   timestamp: string; // Assuming ISO date string
//   status: string;
//   type: string;
//   level: string;
//   assignedUserId?: number;
//   assignedUsername?: string;
//   selected: boolean;
// }
