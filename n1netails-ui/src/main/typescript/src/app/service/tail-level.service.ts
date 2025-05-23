import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Define interfaces for TailLevel and TailLevelResponse based on Java models

export interface TailLevel {
  levelName: string;
  matchString: string;
  tailId: number; // Foreign key to the parent Tail configuration
}

export interface TailLevelResponse {
  id: number;
  levelName: string;
  matchString: string;
  tailId: number;
}

@Injectable({
  providedIn: 'root'
})
export class TailLevelService {
  private apiUrl = '/api/tail-level'; // Base URL for tail level operations

  constructor(private http: HttpClient) { }

  createTailLevel(request: TailLevel): Observable<TailLevelResponse> {
    return this.http.post<TailLevelResponse>(this.apiUrl, request);
  }

  getTailLevels(): Observable<TailLevelResponse[]> {
    return this.http.get<TailLevelResponse[]>(this.apiUrl);
  }

  getTailLevelById(id: number): Observable<TailLevelResponse> {
    return this.http.get<TailLevelResponse>(`${this.apiUrl}/${id}`);
  }

  updateTailLevel(id: number, request: TailLevel): Observable<TailLevelResponse> {
    return this.http.put<TailLevelResponse>(`${this.apiUrl}/${id}`, request);
  }

  deleteTailLevel(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
