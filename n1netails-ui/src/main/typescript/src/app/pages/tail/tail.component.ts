import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TailService, TailResponse, ResolveTailRequest, TailSummary } from '../../service/tail.service';
import { CommonModule } from '@angular/common';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { NzSpinModule } from 'ng-zorro-antd/spin';
import { NzAlertModule } from 'ng-zorro-antd/alert';
import { NzEmptyModule } from 'ng-zorro-antd/empty';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { SidenavComponent } from '../../shared/template/sidenav/sidenav.component';
import { HeaderComponent } from '../../shared/template/header/header.component';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { TailUtilService } from '../../service/tail-util.service';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzMessageService } from 'ng-zorro-antd/message';
import { AuthenticationService } from '../../service/authentication.service';
import { User } from '../../model/user';
import { ResolveTailModalComponent } from '../../shared/components/resolve-tail-modal/resolve-tail-modal.component';

@Component({
  selector: 'app-tail',
  standalone: true,
  imports: [
    CommonModule,
    NzTagModule,
    NzSpinModule,
    NzAlertModule,
    NzEmptyModule,
    NzLayoutModule,
    NzGridModule,
    NzCardModule,
    NzAvatarModule,
    NzButtonModule,
    HeaderComponent,
    SidenavComponent,
    ResolveTailModalComponent
  ],
  templateUrl: './tail.component.html',
  styleUrl: './tail.component.less'
})
export class TailComponent implements OnInit {
  objectKeys = Object.keys;

  tail: TailResponse | null = null;
  metadataKeys: string[] = [];
  error: string | null = null;
  isLoading: boolean = true;
  showMetadata = false;

  // Modal properties
  resolveModalVisible: boolean = false;
  currentUser: User;

  public tailUtilService = inject(TailUtilService);
  private tailService = inject(TailService);
  private route = inject(ActivatedRoute);
  private messageService = inject(NzMessageService);
  private authService = inject(AuthenticationService);

  constructor() {
    this.currentUser = this.authService.getUserFromLocalCache();
  }

  ngOnInit(): void {
    this.isLoading = true;
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = +idParam;
      if (isNaN(id)) {
        this.error = 'Invalid Tail ID in URL.';
        this.isLoading = false;
        return;
      }
      this.loadTailData(id);
    } else {
      this.error = 'No Tail ID found in URL.';
      this.isLoading = false;
    }
  }

  loadTailData(id: number): void {
    this.isLoading = true;
    this.tailService.getTailById(id).subscribe({
      next: (data) => {
        this.tail = data;
        if (this.tail && this.tail.metadata) {
          this.metadataKeys = Object.keys(this.tail.metadata);
        }
        this.error = null;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching tail:', err);
        this.error = `Failed to load tail data. Status: ${err.status}, Message: ${err.message || err}`;
        this.isLoading = false;
      }
    });
  }

  openResolveModal(): void {
    if (!this.tail) return;
    this.resolveModalVisible = true;
  }

  handleModalCancel(): void {
    this.resolveModalVisible = false;
  }

  handleModalOk(note: string): void {
    if (!this.tail || !this.currentUser) {
      this.messageService.error('Cannot resolve tail: Missing tail data or user information.');
      return;
    }

    const tailSummary: TailSummary = {
      id: this.tail.id,
      title: this.tail.title,
      description: this.tail.description,
      timestamp: this.tail.timestamp,
      resolvedtimestamp: this.tail.resolvedTimestamp,
      assignedUserId: this.currentUser.id,
      level: this.tail.level,
      type: this.tail.type,
      status: this.tail.status
    };

    const tailResolveRequest: ResolveTailRequest = {
      userId: this.currentUser.id,
      tailSummary: tailSummary,
      note: note,
    };

    this.tailService.markTailResolved(tailResolveRequest).subscribe({
      next: (result) => {
        this.messageService.success(`Resolved "${this.tail?.title}"`);
        this.resolveModalVisible = false;
        if (this.tail) {
          this.loadTailData(this.tail.id);
        }
      },
      error: (err) => {
        this.messageService.error(`Unable to mark tail "${this.tail?.title}" as resolved. Error: ${err.message || err}`);
      },
    });
  }
}
