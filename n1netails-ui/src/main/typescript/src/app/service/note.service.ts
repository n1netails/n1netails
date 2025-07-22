import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Note } from '../model/note.model';
import { UiConfigService } from '../shared/ui-config.service';

@Injectable({
  providedIn: 'root'
})
export class NoteService {

  host: string = '';
  private apiPath = '/ninetails/note';

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService
  ) {}

  getNotesByTailId(tailId: number): Observable<Note[]> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.get<Note[]>(`${this.host}/tail/${tailId}`);
  }

  saveNote(note: Note): Observable<Note> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.post<Note>(this.host, note);
  }

  getN1Note(tailId: number): Observable<Note> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.get<Note>(`${this.host}/tail/${tailId}/isN1`);
  }
}
