export interface PageRequest {
  pageNumber: number;
  pageSize: number;
  sortDirection: string;
  sortBy: string;
  searchTerm?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // current page number
}
