import { Injectable } from '@angular/core';
import { UiConfigService } from '../shared/util/ui-config.service';
import { HttpClient } from '@angular/common/http';
import { TailResponse } from '../model/tail.model';
import { Observable } from 'rxjs';
import { TailPageRequest, TailPageResponse } from '../model/interface/tail-page.interface';

export interface IsBookmarkedResponse {
  bookmarked: boolean;
}

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
export class BookmarkService {

  host: string = '';
  private apiPath = '/ninetails/bookmarks'

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService
  ) {}

  bookmarkTail(tailId: number) {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.post<void>(`${this.host}/${tailId}`, {});
  }

  removeBookmark(tailId: number) {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.delete<void>(`${this.host}/${tailId}`);
  }

  getUserTailBookmarks(request: TailPageRequest): Observable<TailPageResponse<TailResponse>> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.post<TailPageResponse<TailResponse>>(`${this.host}`, request);
  }

  isTailBookmarkedByUser(tailId: number) {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.get<IsBookmarkedResponse>(`${this.host}/${tailId}/exists`);
  }
}
