import { Component, DestroyRef, Input, OnInit, ViewChild, inject } from '@angular/core';
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

import { TailResponse } from '../../../model/tail.model'; // Adjust path as needed
import { User } from '../../../model/user'; // Adjust path as needed
import { Note } from '../../../model/note.model';
import { NoteService } from '../../../service/note.service';
import { LlmService } from '../../../service/llm.service';
import { LlmPromptRequest, LlmPromptResponse } from '../../../model/llm.model';
import { UiConfigService } from '../../ui-config.service'; // For LLM provider/model defaults
import { NzSkeletonModule } from 'ng-zorro-antd/skeleton';
import { HttpClient } from '@angular/common/http';
import { CollectionViewer, DataSource } from '@angular/cdk/collections';
import { BehaviorSubject, catchError, Observable, of, Subject, take, takeUntil } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
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
  private uiConfigService = inject(UiConfigService); // To get default LLM provider/model
  private ngZone = inject(NgZone);

  // Default LLM settings - could be made configurable
  private defaultLlmProvider: string = 'openai';
  private defaultLlmModel: string = 'gpt-4.1'; // Or fetch from uiConfigService if available

  llmEnabled = false;
  openaiEnabled = false;
  geminiEnabled = false;

  notesTitle = 'Inari Chat & Notes';

  exampleLlmResponse = `**Incident Report: Analysis of RuntimeException**
  
  **Summary:**  
  An uncaught \`java.lang.RuntimeException\` was triggered in the \`N1netailsSandboxApplication\` running on host \`shahid-pc\`. The exception message and the code structure indicate a deliberate throw to exercise or test an exception handler mechanism.
  
  **Root Cause Analysis:**  
  - The exception \`java.lang.RuntimeException: This will trigger the handler\` was thrown at line 284 within the method \`runKudaExceptionHandler\` of the class \`N1netailsSandboxApplication\`.
  - The stack trace shows this exception is thrown explicitly as part of a runnable (threaded) context.
  - The method name and the exception message ("This will trigger the handler") strongly suggest this exception is purposely thrown, likely to test or invoke global exception handling logic configured elsewhere in the application.
  - No underlying application error, network interruption, or system resource issue is indicated in the provided details.
  
  **Supporting Details:**
  - OS: Windows 11 (version 10.0), Java: 21.0.2, 64-bit architecture.
  - The exception propagated to the thread's run loop, indicating it was not caught within business logic or exception handling blocks upstream.
  - Thread state was RUNNABLE at the time of exception, signifying normal operation until the forced RuntimeException.
  
  **Conclusion:**  
  This is a controlled exception, likely executed to validate or trigger custom exception handling code. There is no evidence of a system fault or unintentional application error. The event appears intentional for development or operational testing purposes.
  
  **Recommended Actions:**  
  - No immediate remediation is needed unless this exception was unintentionally left in production code.
  - If this is a test artifact, consider removing or conditioning it to avoid confusion in future alerting/monitoring.
  - Verify the intended exception handling path successfully processed this event as designed.
  `;

  constructor() {
    // Initialize default LLM settings from uiConfigService if they exist
    if (this.uiConfigService.isOpenaiEnabled()) {
        this.defaultLlmProvider = 'openai';
        // Potentially more specific model from config if available
    } else if (this.uiConfigService.isGeminiEnabled()) {
        this.defaultLlmProvider = 'gemini';
        // Potentially more specific model from config if available
    }

    this.openaiEnabled = this.uiConfigService.isOpenaiEnabled();
    this.geminiEnabled = this.uiConfigService.isGeminiEnabled();

    this.llmEnabled = this.openaiEnabled || this.geminiEnabled;
    if (!this.llmEnabled) this.notesTitle = 'Notes';
  }

  ngOnInit(): void {
    // temp placeholder
    this.initialLlmResponse = this.exampleLlmResponse;
    
    this.getN1Note();
    this.loadNotes();
  }

  // todo update this to get the n1 note
  getN1Note() {
    const aiResponseNote: ChatMessage = {
        human: false,
        content: this.initialLlmResponse,
        // noteText: this.initialLlmResponse,
        createdAt: new Date(),
        userId: this.currentUser.id, // Or a more specific AI identifier
        username: this.currentUser.username,
        llmProvider: this.defaultLlmProvider, // This might need to come from the actual response
        llmModel: this.defaultLlmModel,   // This might need to come from the actual response
        tailId: this.tail.id,
        organizationId: this.tail.organizationId,
        n1: true
      };
      this.notes.push(aiResponseNote);
      console.log('init notes', this.notes);
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
        this.getN1Note();
        loadedNotes.forEach(note => {
            if (!this.initialLlmResponse || note.content !== this.initialLlmResponse) {
                 this.notes.push(note as ChatMessage);
            } else if (this.initialLlmResponse && note.content === this.initialLlmResponse) {
                // this.notes.push(note as ChatMessage);
            }
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
          provider: this.defaultLlmProvider, // Or let user choose
          model: this.defaultLlmModel,       // Or let user choose
          prompt: currentPromptText,
          userId: this.currentUser.id,
          organizationId: this.tail.organizationId,
          tailId: this.tail.id
        };

        // Add a temporary loading message for AI response
        const loadingAiMessage: ChatMessage = {
            userId: this.currentUser.id, 
            username: this.currentUser.username, 
            human: false,
            tailId: this.tail.id, 
            createdAt: new Date(), 
            content: '...',
            organizationId: this.tail.organizationId,
            n1: false,
            isLoading: true, 
            llmProvider: llmRequest.provider, 
            llmModel: llmRequest.model
        };
        this.notes.push(loadingAiMessage);

        this.llmService.sendPrompt(llmRequest).subscribe({
          next: (llmResponse) => {
            // Remove loading message
            this.notes.pop();

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
