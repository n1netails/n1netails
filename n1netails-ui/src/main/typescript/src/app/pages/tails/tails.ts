import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TailDataService, Tail, TailPageRequest, TailPageResponse } from '../../services/tail-data';

@Component({
  selector: 'app-tails',
  standalone: true, // Explicitly marking as standalone
  imports: [CommonModule, FormsModule], // CommonModule for *ngFor, *ngIf, etc., FormsModule for [(ngModel)]
  templateUrl: './tails.html',
  styleUrl: './tails.less'
})
export class TailsComponent implements OnInit { // Renamed class to follow Angular style guide (TailsComponent)
  tails: Tail[] = [];
  currentPage: number = 0;
  pageSize: number = 10;
  totalElements: number = 0;
  totalPages: number = 0;

  searchTerm: string = '';
  selectedStatus: string = ''; // Bound to status dropdown
  selectedType: string = '';   // Bound to type dropdown
  selectedLevel: string = '';  // Bound to level dropdown

  selectedTails: Set<Tail> = new Set();

  constructor(private tailDataService: TailDataService) {}

  ngOnInit(): void {
    this.loadTails();
  }

  loadTails(): void {
    const request: TailPageRequest = {
      page: this.currentPage,
      size: this.pageSize,
      searchTerm: this.searchTerm,
      // Send filter values only if they are not empty, otherwise undefined (or backend handles empty string as "no filter")
      filterByStatus: this.selectedStatus || undefined,
      filterByType: this.selectedType || undefined,
      filterByLevel: this.selectedLevel || undefined
    };

    this.tailDataService.getTails(request).subscribe({
      next: (response: TailPageResponse<Tail>) => {
        this.tails = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        // Adjust currentPage if it's out of bounds (e.g., after deleting last item on a page)
        if (this.currentPage >= this.totalPages && this.totalPages > 0) {
          this.currentPage = this.totalPages - 1;
        }
      },
      error: (err) => {
        console.error('Error loading tails:', err);
        // Potentially display user-friendly error message
        this.tails = []; // Clear tails on error
        this.totalElements = 0;
        this.totalPages = 0;
      }
    });
  }

  onSearchTermChange(): void {
    this.currentPage = 0; // Reset to first page on new search
    this.loadTails();
  }

  onFilterChange(): void {
    this.currentPage = 0; // Reset to first page on filter change
    this.loadTails();
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadTails();
    }
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadTails();
    }
  }

  toggleSelection(tail: Tail): void {
    if (this.selectedTails.has(tail)) {
      this.selectedTails.delete(tail);
    } else {
      this.selectedTails.add(tail);
    }
  }

  isSelected(tail: Tail): boolean {
    return this.selectedTails.has(tail);
  }

  // Helper to get current page display number (1-based)
  get displayedCurrentPage(): number {
    return this.currentPage + 1;
  }
}
