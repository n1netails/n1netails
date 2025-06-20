import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { Note } from '../model/note.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class NoteService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/notes`; // Assuming /api/notes endpoint

  constructor() { }

  getNotesByTailId(tailId: number): Observable<Note[]> {
    // TODO: Replace with actual API call
    console.log(`NoteService: Fetching notes for tailId ${tailId}`);
    // Mock response for now
    // return of([
    //   { id: '1', userId: 'user1', username: 'Human User', isHuman: true, tailId: tailId, timestamp: new Date(), noteText: 'This is a human note.', organizationId: 1 },
    //   { id: '2', userId: 'ai', username: 'AI Assistant', isHuman: false, llmProvider: 'openai', llmModel: 'gpt-4.1', tailId: tailId, timestamp: new Date(), noteText: 'This is an AI response.', organizationId: 1 }
    // ]);
    return this.http.get<Note[]>(`${this.apiUrl}/tail/${tailId}`);
  }

  saveNote(note: Note): Observable<Note> {
    // TODO: Replace with actual API call
    console.log('NoteService: Saving note:', note);
    // Mock response for now
    // return of({ ...note, id: 'mock-' + Math.random().toString(36).substr(2, 9) });
    return this.http.post<Note>(this.apiUrl, note);
  }
}
