import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Define interfaces for request and response objects based on TailRequest and TailResponse in Java

export interface TailRequest {
  name: string;
  filePath: string;
  filter?: string; // Optional filter
}

export interface TailResponse {
  id: number;
  name: string;
  filePath: string;
  filter?: string;
  status: string; // e.g., 'RUNNING', 'STOPPED', 'ERROR'
}

@Injectable({
  providedIn: 'root'
})
export class TailService {
  private apiUrl = '/api/tail'; // Base URL for tail operations

  constructor(private http: HttpClient) { }

  createTail(request: TailRequest): Observable<TailResponse> {
    return this.http.post<TailResponse>(this.apiUrl, request);
  }

  getTails(): Observable<TailResponse[]> {
    return this.http.get<TailResponse[]>(this.apiUrl);
  }

  getTailById(id: number): Observable<TailResponse> {
    return this.http.get<TailResponse>(`${this.apiUrl}/${id}`);
  }

  updateTail(id: number, request: TailRequest): Observable<TailResponse> {
    return this.http.put<TailResponse>(`${this.apiUrl}/${id}`, request);
  }

  deleteTail(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
