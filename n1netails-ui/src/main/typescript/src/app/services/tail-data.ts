import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Define an interface for the TailPageRequest equivalent for strong typing
export interface TailPageRequest {
  page: number;
  size: number;
  searchTerm?: string;
  filterByStatus?: string;
  filterByType?: string;
  filterByLevel?: string;
}

// Define an interface for the expected Page response (can be refined later)
export interface TailPageResponse<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // current page number
  // Add other pagination properties if needed
}

// Define an interface for a single Tail item (can be refined later)
export interface Tail {
  id: number;
  title: string;
  description?: string;
  timestamp: string; // Assuming ISO date string
  status: string;
  type: string;
  level: string;
  assignedUserId?: number;
  assignedUsername?: string;
  // Add other tail properties as needed
}


@Injectable({
  providedIn: 'root'
})
export class TailDataService {

  private apiUrl = '/api/tail/page'; // Backend API endpoint

  constructor(private http: HttpClient) { }

  getTails(request: TailPageRequest): Observable<TailPageResponse<Tail>> {
    return this.http.post<TailPageResponse<Tail>>(this.apiUrl, request);
  }
}
