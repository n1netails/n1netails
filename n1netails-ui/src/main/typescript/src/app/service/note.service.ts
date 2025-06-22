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
  private apiUrl = '/ninetails/note';

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService
  ) { 
    this.host = this.uiConfigService.getApiUrl();
    this.host = this.host + this.apiUrl;
  }

  getNotesByTailId(tailId: number): Observable<Note[]> {
    console.log(`NoteService: Fetching notes for tailId ${tailId}`);
    return this.http.get<Note[]>(`${this.host}/tail/${tailId}`);
  }

  saveNote(note: Note): Observable<Note> {
    console.log('NoteService: Saving note:', note);
    return this.http.post<Note>(this.host, note);
  }

  getN1Note(tailId: number): Observable<Note> {
    return this.http.get<Note>(`${this.host}/tail/${tailId}/isN1`);
  }
}
