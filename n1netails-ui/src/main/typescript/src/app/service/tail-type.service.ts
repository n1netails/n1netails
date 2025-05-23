import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Define interfaces for TailType and TailTypeResponse based on Java models

export interface TailType {
  typeName: string; // e.g., 'LOG_FILE', 'KUBERNETES_POD', 'SYSTEMD_JOURNAL'
  handlerIdentifier: string; // Identifier for the backend handler (e.g., Spring bean name or class name)
}

export interface TailTypeResponse {
  id: number;
  typeName: string;
  handlerIdentifier: string;
}

@Injectable({
  providedIn: 'root'
})
export class TailTypeService {
  private apiUrl = '/api/tail-type'; // Base URL for tail type operations

  constructor(private http: HttpClient) { }

  createTailType(request: TailType): Observable<TailTypeResponse> {
    return this.http.post<TailTypeResponse>(this.apiUrl, request);
  }

  getTailTypes(): Observable<TailTypeResponse[]> {
    return this.http.get<TailTypeResponse[]>(this.apiUrl);
  }

  getTailTypeById(id: number): Observable<TailTypeResponse> {
    return this.http.get<TailTypeResponse>(`${this.apiUrl}/${id}`);
  }

  updateTailType(id: number, request: TailType): Observable<TailTypeResponse> {
    return this.http.put<TailTypeResponse>(`${this.apiUrl}/${id}`, request);
  }

  deleteTailType(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
