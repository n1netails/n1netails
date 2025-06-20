import { Component, Input, OnInit, inject } from '@angular/core';
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

import { TailResponse } from '../../../model/tail.model'; // Adjust path as needed
import { User } from '../../../model/user'; // Adjust path as needed
import { Note } from '../../../model/note.model';
import { NoteService } from '../../../service/note.service';
import { LlmService } from '../../../service/llm.service';
import { LlmPromptRequest, LlmPromptResponse } from '../../../model/llm.model';
import { UiConfigService } from '../../ui-config.service'; // For LLM provider/model defaults

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
    MarkdownModule,
  ],
  templateUrl: './ai-chat-card.component.html',
  styleUrls: ['./ai-chat-card.component.less']
})
export class AiChatCardComponent implements OnInit {
  @Input() tail!: TailResponse;
  @Input() currentUser!: User;
  @Input() initialLlmResponse?: string | null;

  public notes: ChatMessage[] = [];
  public newNoteText: string = '';
  public isLoadingNotes: boolean = false;
  public isSendingMessage: boolean = false;

  private noteService = inject(NoteService);
  private llmService = inject(LlmService);
  private messageService = inject(NzMessageService);
  private uiConfigService = inject(UiConfigService); // To get default LLM provider/model

  // Default LLM settings - could be made configurable
  private defaultLlmProvider: string = 'openai';
  private defaultLlmModel: string = 'gpt-4.1'; // Or fetch from uiConfigService if available

  constructor() {
    // Initialize default LLM settings from uiConfigService if they exist
    if (this.uiConfigService.isOpenaiEnabled()) {
        this.defaultLlmProvider = 'openai';
        // Potentially more specific model from config if available
    } else if (this.uiConfigService.isGeminiEnabled()) {
        this.defaultLlmProvider = 'gemini';
        // Potentially more specific model from config if available
    }
  }

  ngOnInit(): void {
    if (this.initialLlmResponse) {
      const aiResponseNote: ChatMessage = {
        isHuman: false,
        noteText: this.initialLlmResponse,
        timestamp: new Date(),
        userId: 'AI', // Or a more specific AI identifier
        username: 'LLM Investigator',
        llmProvider: this.defaultLlmProvider, // This might need to come from the actual response
        llmModel: this.defaultLlmModel,   // This might need to come from the actual response
        tailId: this.tail.id,
        organizationId: this.tail.organizationId
      };
      this.notes.push(aiResponseNote);
    }
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
        // Add loaded notes, ensuring no duplicates with initialLlmResponse if it were also a note
        const existingNoteTexts = this.notes.map(n => n.noteText);
        loadedNotes.forEach(note => {
            if (!this.initialLlmResponse || note.noteText !== this.initialLlmResponse) {
                 this.notes.push(note as ChatMessage);
            } else if (this.initialLlmResponse && note.noteText === this.initialLlmResponse && !existingNoteTexts.includes(note.noteText)) {
                // If initialLlmResponse was somehow also saved as a note and not yet added
                this.notes.push(note as ChatMessage);
            }
        });
        this.notes.sort((a, b) => new Date(a.timestamp).getTime() - new Date(b.timestamp).getTime());
        this.isLoadingNotes = false;
      },
      error: (err) => {
        this.messageService.error('Failed to load notes.');
        console.error('Error loading notes:', err);
        this.isLoadingNotes = false;
      }
    });
  }

  addNote(): void {
    if (!this.newNoteText.trim()) return;
    this.isSendingMessage = true;

    const note: Note = {
      userId: this.currentUser.id,
      username: this.currentUser.username,
      isHuman: true,
      tailId: this.tail.id,
      timestamp: new Date(),
      noteText: this.newNoteText,
      organizationId: this.tail.organizationId
    };

    this.noteService.saveNote(note).subscribe({
      next: (savedNote) => {
        this.notes.push(savedNote as ChatMessage);
        this.newNoteText = '';
        this.isSendingMessage = false;
        this.messageService.success('Note added successfully.');
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
      isHuman: true,
      tailId: this.tail.id,
      timestamp: new Date(), // Timestamp for user prompt
      noteText: this.newNoteText,
      organizationId: this.tail.organizationId,
    };

    this.noteService.saveNote(userPromptNote).subscribe({
      next: (savedUserPrompt) => {
        this.notes.push(savedUserPrompt as ChatMessage);
        const currentPromptText = this.newNoteText; // Capture before clearing
        this.newNoteText = ''; // Clear input after saving user prompt

        // 2. Prepare and send request to LLM
        const llmRequest: LlmPromptRequest = {
          provider: this.defaultLlmProvider, // Or let user choose
          model: this.defaultLlmModel,       // Or let user choose
          prompt: currentPromptText,
          userId: this.currentUser.id,
          organizationId: this.tail.organizationId,
          tailId: this.tail.id
        };

        // Add a temporary loading message for AI response
        const loadingAiMessage: ChatMessage = {
            userId: 'AI_temp', username: 'AI Assistant', isHuman: false,
            tailId: this.tail.id, timestamp: new Date(), noteText: '...',
            organizationId: this.tail.organizationId,
            isLoading: true, llmProvider: llmRequest.provider, llmModel: llmRequest.model
        };
        this.notes.push(loadingAiMessage);

        this.llmService.sendPrompt(llmRequest).subscribe({
          next: (llmResponse) => {
            // Remove loading message
            this.notes.pop();

            // 3. Save LLM's response as an AI note
            const aiResponseNote: Note = {
              userId: 'AI', // Or a more specific AI identifier from response if available
              username: 'AI Assistant', // Or more specific
              isHuman: false,
              llmProvider: llmResponse.provider,
              llmModel: llmResponse.model,
              tailId: this.tail.id,
              timestamp: llmResponse.timestamp || new Date(), // Timestamp for AI response
              noteText: llmResponse.completion,
              organizationId: this.tail.organizationId
            };

            this.noteService.saveNote(aiResponseNote).subscribe({
              next: (savedAiResponse) => {
                this.notes.push(savedAiResponse as ChatMessage);
                this.isSendingMessage = false;
                this.messageService.success('LLM response received and saved.');
              },
              error: (err) => {
                this.messageService.error('Failed to save LLM response as note.');
                console.error('Error saving AI note:', err);
                // Still add to local display even if save fails for now
                this.notes.push(aiResponseNote as ChatMessage);
                this.isSendingMessage = false;
              }
            });
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
