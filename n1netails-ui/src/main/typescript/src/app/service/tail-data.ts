import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/util/ui-config.service';
import { TailPageRequest, TailPageResponse } from '../model/interface/tail-page.interface';
import { TailResponse } from '../model/tail.model';

// export interface TailPageRequest {
//   page: number;
//   size: number;
//   searchTerm?: string;
//   filterByStatus?: string;
//   filterByType?: string;
//   filterByLevel?: string;
// }

// export interface TailPageResponse<T> {
//   content: T[];
//   totalPages: number;
//   totalElements: number;
//   size: number;
//   number: number; // current page number
// }

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

@Injectable({
  providedIn: 'root'
})
export class TailDataService {

  host: string = '';
  private apiPath = '/ninetails/tail'; // Backend API endpoint

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService
  ) {}

  getTails(request: TailPageRequest): Observable<TailPageResponse<TailResponse>> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.post<TailPageResponse<TailResponse>>(`${this.host}/page`, request);
  }
}
