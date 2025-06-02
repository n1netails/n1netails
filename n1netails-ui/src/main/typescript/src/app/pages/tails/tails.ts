import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TailDataService, Tail, TailPageRequest, TailPageResponse } from '../../service/tail-data';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { HeaderComponent } from '../../shared/template/header/header.component';
import { SidenavComponent } from '../../shared/template/sidenav/sidenav.component';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzSelectModule } from 'ng-zorro-antd/select';
import { NzCheckboxModule } from 'ng-zorro-antd/checkbox';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { TailLevelService } from '../../service/tail-level.service';
import { TailStatusService } from '../../service/tail-status.service';
import { TailTypeService } from '../../service/tail-type.service';
import { Router } from '@angular/router';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { TailUtilService } from '../../service/tail-util.service';

@Component({
  selector: 'app-tails',
  standalone: true, // Explicitly marking as standalone
  imports: [
    NzLayoutModule,
    NzGridModule,
    NzCardModule,
    NzTableModule,
    NzSelectModule,
    NzCheckboxModule,
    NzAvatarModule,
    NzTagModule,
    NzIconModule,
    CommonModule,
    FormsModule,
    HeaderComponent,
    SidenavComponent
  ],
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

  tailLevels: string[] = [];
  tailStatusList: string[] = [];
  tailTypes: string[] = [];

  constructor(
    public tailUtilService: TailUtilService,
    private tailDataService: TailDataService,
    private tailLevelService: TailLevelService,
    private tailStatusService: TailStatusService,
    private tailTypeService: TailTypeService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadTails();
    this.loadTailInfoData();
  }

  goToTail(id: number) {
    this.router.navigate(['/tail', id]);
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
        console.log('response content', response.content);
        response.content.forEach(tail => {
          tail.selected = false;
          this.tails.push(tail);
        });
        this.tails = response.content;
        // this.tails.forEach(tail => tail.selected = false);
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

  loadTailInfoData(): void {
    this.tailLevelService.getTailLevels().subscribe(result => {
      result.forEach(level => this.tailLevels.push(level.name));
      console.log('LEVELS', this.tailLevels);
    });

    this.tailStatusService.getTailStatusList().subscribe(result => {
      result.forEach(status => this.tailStatusList.push(status.name));
      console.log('STATUS', this.tailStatusList);
    });

    this.tailTypeService.getTailTypes().subscribe(result => {
      result.forEach(type => this.tailTypes.push(type.name));
      console.log('TYPE', this.tailTypes);
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

  onPageSizeChange(): void {
    this.currentPage = 0;
    this.loadTails();
  }
}
