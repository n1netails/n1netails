import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TailDataService } from '../../service/tail-data';
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
import { NzToolTipModule } from 'ng-zorro-antd/tooltip';
import { TailUtilService } from '../../shared/util/tail-util.service';
import { ResolveTailModalComponent } from '../../shared/components/resolve-tail-modal/resolve-tail-modal.component';
import { User } from '../../model/user';
import { AuthenticationService } from '../../service/authentication.service';
import { TailService } from '../../service/tail.service';
import { NzMessageService } from 'ng-zorro-antd/message';
import { ResolveTailRequest, TailResponse, TailSummary } from '../../model/tail.model';
import { PageRequest } from '../../model/interface/page.interface';
import { PageUtilService } from '../../shared/util/page-util.service';
import { BookmarkService } from '../../service/bookmark.service';
import { TailPageRequest, TailPageResponse } from '../../model/interface/tail-page.interface';

@Component({
  selector: 'app-tails',
  standalone: true,
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
    NzToolTipModule,
    CommonModule,
    FormsModule,
    HeaderComponent,
    SidenavComponent,
    ResolveTailModalComponent
  ],
  templateUrl: './tails.html',
  styleUrl: './tails.less'
})
export class TailsComponent implements OnInit {

  bookmarksActive = false;

  tails: TailResponse[] = [];
  currentPage: number = 0;
  pageSize: number = 10;
  totalElements: number = 0;
  totalPages: number = 0;

  searchTerm: string = '';
  selectedStatus: string = ''; // Bound to status dropdown
  selectedType: string = '';   // Bound to type dropdown
  selectedLevel: string = '';  // Bound to level dropdown

  selectedTails: Set<TailResponse> = new Set();
  newTailCount: number = 0;

  tailLevels: string[] = [];
  tailStatusList: string[] = [];
  tailTypes: string[] = [];

  // Modal properties
  resolveModalVisible: boolean = false;
  currentUser: User;
  selectedTail: any = null;

  constructor(
    public tailUtilService: TailUtilService,
    private tailDataService: TailDataService,
    private tailLevelService: TailLevelService,
    private tailStatusService: TailStatusService,
    private tailTypeService: TailTypeService,
    private tailService: TailService,
    private bookmarkService: BookmarkService,
    private authenticationService: AuthenticationService,
    private messageService: NzMessageService,
    private router: Router,
    private pageUtilService: PageUtilService
  ) {
    this.currentUser = this.authenticationService.getUserFromLocalCache();
  }

  ngOnInit(): void {
    this.loadTails();
    this.loadTailInfoData();
    this.loadNewTailCount();
  }

  goToTail(id: number) {
    this.router.navigate(['/tail', id]);
  }

  setBookmarkActive() {
    this.bookmarksActive = !this.bookmarksActive
    this.currentPage = 0;
    this.loadTails();
  }

  loadTailsDefault(): void {
    const request: TailPageRequest = {
      page: this.currentPage,
      size: this.pageSize,
      searchTerm: this.searchTerm,
      filterByStatus: this.selectedStatus || undefined,
      filterByType: this.selectedType || undefined,
      filterByLevel: this.selectedLevel || undefined
    };

    this.tailDataService.getTails(request).subscribe({
      next: (response: TailPageResponse<TailResponse>) => {
        response.content.forEach(tail => {
          tail.selected = false;
          this.tails.push(tail);
        });
        this.tails = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        if (this.currentPage >= this.totalPages && this.totalPages > 0) {
          this.currentPage = this.totalPages - 1;
        }
      },
      error: (err) => {
        console.error('Error loading tails:', err);
        this.tails = [];
        this.totalElements = 0;
        this.totalPages = 0;
      }
    });
  }

  loadTails() {
    if (!this.bookmarksActive) {
      this.loadTailsDefault();
    } else {
      const request: TailPageRequest = {
        page: this.currentPage,
        size: this.pageSize,
        searchTerm: this.searchTerm,
        filterByStatus: this.selectedStatus || undefined,
        filterByType: this.selectedType || undefined,
        filterByLevel: this.selectedLevel || undefined
      };

      this.bookmarkService.getUserTailBookmarks(request).subscribe({
        next: (response: TailPageResponse<TailResponse>) => {
          response.content.forEach(tail => {
            tail.selected = false;
            this.tails.push(tail);
          });
          this.tails = response.content;
          this.totalElements = response.totalElements;
          this.totalPages = response.totalPages;
          if (this.currentPage >= this.totalPages && this.totalPages > 0) {
            this.currentPage = this.totalPages - 1;
          }
        },
        error: (err) => {
          console.log('Error loading bookmarked tails:', err);
          this.tails = [];
          this.totalElements = 0;
          this.totalPages = 0;
          this.bookmarksActive = false;
        }
      });
    }
  }

  loadNewTailCount(): void {
    this.tailService.getNewTailCount().subscribe({
      next: (count: number) => {
        this.newTailCount = count;
      },
      error: (err) => {
        console.error('Error loading new tail count:', err);
      }
    });
  }

  loadTailInfoData(): void {
    const pageRequest: PageRequest = this.pageUtilService.setDefaultPageRequest();

    this.tailLevelService.getTailLevels(pageRequest).subscribe(result => {
      result.content.forEach(level => this.tailLevels.push(level.name));
    });

    this.tailStatusService.getTailStatusList(pageRequest).subscribe(result => {
      result.content.forEach(status => this.tailStatusList.push(status.name));
    });

    this.tailTypeService.getTailTypes(pageRequest).subscribe(result => {
      result.content.forEach(type => this.tailTypes.push(type.name));
    });
  }

  onStatusSearch(term: string): void {
    const pageRequest: PageRequest = this.pageUtilService.setDefaultPageRequestWithSearch(term);
    this.tailStatusService.getTailStatusList(pageRequest).subscribe(result => {
      this.tailStatusList = [];
      result.content.forEach(status => this.tailStatusList.push(status.name));
    });
  } 

  onTypeSearch(term: string): void {
    const pageRequest: PageRequest = this.pageUtilService.setDefaultPageRequestWithSearch(term);
    this.tailTypeService.getTailTypes(pageRequest).subscribe(result => {
      this.tailTypes = [];
      result.content.forEach(type => this.tailTypes.push(type.name));
    });
  } 

  onLevelSearch(term: string): void {
    const pageRequest: PageRequest = this.pageUtilService.setDefaultPageRequestWithSearch(term);
    this.tailLevelService.getTailLevels(pageRequest).subscribe(result => {
      this.tailLevels = [];
      result.content.forEach(level => this.tailLevels.push(level.name));
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

  toggleSelection(tail: TailResponse): void {
    if (this.selectedTails.has(tail)) {
      this.selectedTails.delete(tail);
    } else {
      this.selectedTails.add(tail);
    }
  }

  isSelected(tail: TailResponse): boolean {
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

  resolve(item: any): void {
    this.selectedTail = item;
    this.resolveModalVisible = true;
  }

  resolveAll(): void {
    this.tailService.resolveAllTails().subscribe({
      next: () => {
        this.messageService.success('All NEW tails have been resolved.');
        this.loadTails();
        this.loadNewTailCount();
      },
      error: (err) => {
        this.messageService.error(`Unable to resolve all tails. Error: ${err.message || err}`);
      }
    });
  }

  openResolveModal(): void {
    if (!this.selectedTail) return;
    this.resolveModalVisible = true;
  }

  handleResolveCancel(): void {
    this.resolveModalVisible = false;
  }

  handleResolveOk(note: string): void {
    if (!this.selectedTail || !this.currentUser) {
      this.messageService.error('Cannot resolve tail: Missing tail data or user information.');
      return;
    }

    const tailSummary: TailSummary = {
      id: this.selectedTail.id,
      title: this.selectedTail.title,
      description: this.selectedTail.description,
      timestamp: this.selectedTail.timestamp,
      resolvedTimestamp: this.selectedTail.resolvedTimestamp,
      assignedUserId: this.currentUser.id,
      level: this.selectedTail.level,
      type: this.selectedTail.type,
      status: this.selectedTail.status
    };

    const tailResolveRequest: ResolveTailRequest = {
      userId: this.currentUser.id,
      tailSummary: tailSummary,
      note: note,
    };

    this.tailService.markTailResolved(tailResolveRequest).subscribe({
      next: (result) => {
        this.messageService.success(`Resolved "${this.selectedTail.title}"`);
        this.resolveModalVisible = false;
        this.selectedTail = null;
        note = '';
        this.loadTails();
      }, 
      error: (err) => {
        this.messageService.error(`Unable to mark tail "${this.selectedTail.title}" as resolved. Error: ${err.message || err}`);
      },
    });
  }
}
