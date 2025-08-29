import { Injectable } from '@angular/core';
import { UiConfigService } from '../shared/util/ui-config.service';
import { HttpClient } from '@angular/common/http';
import { TailResponse } from '../model/tail.model';

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

  getUserTailBookmarks() {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.get<TailResponse[]>(`${this.host}`);
  }

  isTailBookmarkedByUser(tailId: number) {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.get<Map<string, boolean>>(`${this.host}/${tailId}/exists`);
  }
}
