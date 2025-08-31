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
