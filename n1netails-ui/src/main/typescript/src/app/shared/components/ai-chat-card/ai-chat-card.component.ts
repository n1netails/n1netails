import { Component, Input, OnInit, ViewChild, inject } from '@angular/core';
import { CdkFixedSizeVirtualScroll, CdkVirtualForOf, CdkVirtualScrollViewport, ScrollingModule } from '@angular/cdk/scrolling';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzListModule } from 'ng-zorro-antd/list';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzSpinModule } from 'ng-zorro-antd/spin';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { NzCommentModule } from 'ng-zorro-antd/comment';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzMessageService } from 'ng-zorro-antd/message';
import { MarkdownModule } from 'ngx-markdown';

import { TailResponse } from '../../../model/tail.model';
import { User } from '../../../model/user';
import { Note } from '../../../model/note.model';
import { NoteService } from '../../../service/note.service';
import { LlmService } from '../../../service/llm.service';
import { LlmPromptRequest } from '../../../model/llm.model';
import { NzSkeletonModule } from 'ng-zorro-antd/skeleton';
import { take } from 'rxjs';
import { NgZone } from '@angular/core';

interface ChatMessage extends Note {
  isLoading?: boolean; // For AI messages that are pending
}

@Component({
  selector: 'app-ai-chat-card',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NzCardModule,
    NzListModule,
    NzInputModule,
    NzButtonModule,
    NzSpinModule,
    NzAvatarModule,
    NzCommentModule,
    NzFormModule,
    NzSkeletonModule,
    CdkFixedSizeVirtualScroll,
    CdkVirtualForOf,
    ScrollingModule,
    MarkdownModule,
  ],
  templateUrl: './ai-chat-card.component.html',
  styleUrls: ['./ai-chat-card.component.less']
})
export class AiChatCardComponent implements OnInit {
  @Input() tail!: TailResponse;
  @Input() currentUser!: User;
  @Input() initialLlmResponse?: string | null;

  @ViewChild(CdkVirtualScrollViewport) viewport?: CdkVirtualScrollViewport;

  public notes: ChatMessage[] = [];
  public newNoteText: string = '';
  public isLoadingNotes: boolean = false;
  public isSendingMessage: boolean = false;

  private noteService = inject(NoteService);
  private llmService = inject(LlmService);
  private messageService = inject(NzMessageService);
  private ngZone = inject(NgZone);

  // Default LLM settings - could be made configurable
  llmEnabled = false;
  openaiEnabled = false;
  geminiEnabled = false;

  notesTitle = 'Inari Chat & Notes';

  constructor() {
    this.openaiEnabled = this.llmService.isOpenaiEnabled();
    this.geminiEnabled = this.llmService.isGeminiEnabled();

    this.llmEnabled = this.openaiEnabled || this.geminiEnabled;
    if (!this.llmEnabled) this.notesTitle = 'Notes';
  }

  ngOnInit(): void {
    this.loadNotes();
  }

  loadNotes(): void {
    if (!this.tail || !this.tail.id) {
      this.messageService.error('Tail ID is missing, cannot load notes.');
      return;
    }
    this.isLoadingNotes = true;
    this.noteService.getNotesByTailId(this.tail.id).subscribe({
      next: (loadedNotes) => {
        this.notes = [];
        loadedNotes.forEach(note => {
          this.notes.push(note as ChatMessage);
        });
        this.isLoadingNotes = false;
        console.log('NOTES SIZE: ', this.notes.length);
      },
      error: (err) => {
        this.messageService.error('Failed to load notes.');
        console.error('Error loading notes:', err);
        this.isLoadingNotes = false;
      }
    });
  }

  scrollToBottom(): void {
    if (this.viewport && this.notes.length > 0) {
      this.ngZone.onStable.pipe(take(1)).subscribe(() => {
        this.viewport!.scrollToIndex(this.notes.length - 1, 'smooth');
      });
    }
  }

  addNote(): void {
    if (!this.newNoteText.trim()) return;
    this.isSendingMessage = true;

    const note: Note = {
      tailId: this.tail.id,
      organizationId: this.tail.organizationId,
      userId: this.currentUser.id,
      username: this.currentUser.username,
      human: true,
      n1: false,
      createdAt: new Date(),
      content: this.newNoteText
    };

    this.noteService.saveNote(note).subscribe({
      next: (savedNote) => {

        // TODO FIGURE OUT A WAY TO REFRESH THE NOTES ON SCREEN
        const tempN = this.notes;
        this.notes = [];
        tempN.forEach(note => {
          this.notes.push(note);
        });

        this.notes.push(savedNote as ChatMessage);
        this.newNoteText = '';
        this.isSendingMessage = false;
        this.messageService.success('Note added successfully.');
        Promise.resolve().then(() => this.scrollToBottom());
      },
      error: (err) => {
        this.messageService.error('Failed to save note.');
        console.error('Error saving note:', err);
        this.isSendingMessage = false;
      }
    });
  }

  sendToLlm(): void {
    if (!this.newNoteText.trim()) return;
    this.isSendingMessage = true;

    // 1. Save user's prompt as a human note
    const userPromptNote: Note = {
      userId: this.currentUser.id,
      username: this.currentUser.username,
      human: true,
      tailId: this.tail.id,
      createdAt: new Date(), // Timestamp for user prompt
      content: this.newNoteText,
      organizationId: this.tail.organizationId,
      n1: false
    };

    this.noteService.saveNote(userPromptNote).subscribe({
      next: (savedUserPrompt) => {
        this.notes.push(savedUserPrompt as ChatMessage);
        const currentPromptText = this.newNoteText; // Capture before clearing
        this.newNoteText = ''; // Clear input after saving user prompt

        // 2. Prepare and send request to LLM
        const llmRequest: LlmPromptRequest = {
          // TODO GIVE USERS OPTION TO SELECT DIFFERENT LLM PROVIDERS AND MODELS
          provider: this.llmService.openai,
          model: this.llmService.openAiModels[0],
          prompt: currentPromptText,
          userId: this.currentUser.id,
          organizationId: this.tail.organizationId,
          tailId: this.tail.id
        };

        console.log("sending prompt request");
        this.llmService.sendPrompt(llmRequest).subscribe({
          next: (llmResponse) => {

            // 3. Save LLM's response as an AI note
            const aiResponseNote: Note = {
              userId: this.currentUser.id,
              username: this.currentUser.username,
              human: false,
              llmProvider: llmResponse.provider,
              llmModel: llmResponse.model,
              tailId: this.tail.id,
              createdAt: llmResponse.timestamp || new Date(), // Timestamp for AI response
              content: llmResponse.completion,
              organizationId: this.tail.organizationId,
              n1: false
            };

            // TODO FIGURE OUT A WAY TO REFRESH THE NOTES ON SCREEN
            const tempN = this.notes;
              this.notes = [];
              tempN.forEach(note => {
                this.notes.push(note);
            });

            this.notes.push(aiResponseNote as ChatMessage);
          },
          error: (err) => {
            // Remove loading message
            this.notes.pop();
            this.messageService.error('Failed to get response from LLM.');
            console.error('Error sending prompt to LLM:', err);
            this.isSendingMessage = false;
            // Restore user text if LLM call failed
            this.newNoteText = currentPromptText;
          }
        });
      },
      error: (err) => {
        this.messageService.error('Failed to save your prompt as a note before sending to LLM.');
        console.error('Error saving user prompt note:', err);
        this.isSendingMessage = false;
      }
    });
  }
}
