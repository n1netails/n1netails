import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TailService, TailResponse } from '../../service/tail.service';
import { CommonModule } from '@angular/common';
import { NzTagModule } from 'ng-zorro-antd/tag'; // For nz-tag
import { NzSpinModule } from 'ng-zorro-antd/spin'; // For nz-spin
import { NzAlertModule } from 'ng-zorro-antd/alert'; // For nz-alert
import { NzEmptyModule } from 'ng-zorro-antd/empty'; // For nz-empty
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { SidenavComponent } from '../../shared/template/sidenav/sidenav.component';
import { HeaderComponent } from '../../shared/template/header/header.component';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';

@Component({
  selector: 'app-tail',
  standalone: true,
  imports: [
    CommonModule, // For *ngIf, etc.
    NzTagModule,   // For <nz-tag>
    NzSpinModule,
    NzAlertModule,
    NzEmptyModule,
    NzLayoutModule,
    NzGridModule,
    NzCardModule,
    NzAvatarModule,
    HeaderComponent,
    SidenavComponent
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

  constructor(
    private route: ActivatedRoute,
    private tailService: TailService
  ) {}

  ngOnInit(): void {
    this.isLoading = true;
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = +idParam; // Convert string 'id' to a number
      if (isNaN(id)) {
        this.error = 'Invalid Tail ID in URL.';
        this.isLoading = false;
        return;
      }
      this.tailService.getTailById(id).subscribe({
        next: (data) => {
          this.tail = data;
          console.log('TAIL', this.tail);
          if (this.tail && this.tail.metadata) {
            this.metadataKeys = Object.keys(this.tail.metadata);
          }
          this.error = null;
          this.isLoading = false;
        },
        error: (err) => {
          console.error('Error fetching tail:', err);
          this.error = `Failed to load tail data. Status: ${err.status}, Message: ${err.message}`;
          this.isLoading = false;
        }
      });
    } else {
      this.error = 'No Tail ID found in URL.';
      this.isLoading = false;
    }
  }

  getLevelColor(level: string): string {
    switch (level?.toUpperCase()) {
      case 'INFO': return 'blue';
      case 'SUCCESS': return 'green';
      case 'WARN': return 'orange';
      case 'ERROR': return 'red';
      case 'CRITICAL': return 'volcano';
      default: return 'default'; // Changed from orange to default for unknown levels
    }
  }

  getStatusColor(status: string): string {
    switch (status?.toUpperCase()) {
      case 'NEW': return 'green';
      case 'IN_PROGRESS': return 'gold';
      case 'BLOCKED': return 'red';
      case 'RESOLVED': return 'blue';
      default: return 'default'; // Changed from orange to default for unknown status
    }
  }

  // Using a simplified version for type colors, can be expanded later if needed
  getTypeColor(type: string): string {
    if (!type) return 'default';
    const key = type.toLowerCase();

    // Predefined Zorro colors for consistency
    const zorroColors = [
      'geekblue', 'purple', 'magenta', 'red', 'volcano', 'orange', 'gold', 'lime', 'green',
      'cyan', 'blue',
    ];
    // Simple hash function to pick a color based on type name
    let hash = 0;
    for (let i = 0; i < key.length; i++) {
      hash = key.charCodeAt(i) + ((hash << 5) - hash);
      hash = hash & hash; // Convert to 32bit integer
    }
    const colorIndex = Math.abs(hash) % zorroColors.length;
    return zorroColors[colorIndex];
  }

  // todo move common methods in to services or util
  getKudaAvatar(level: string) {

    switch (level?.toUpperCase()) {
      case 'INFO': return 'kuda_info.jpg';
      case 'SUCCESS': return 'kuda_success.jpg';
      case 'WARN': return 'kuda_warn.jpg';
      case 'ERROR': return 'kuda_error.jpg';
      case 'CRITICAL': return 'kuda_critical.jpg';
      default: return 'kuda.jpg';
    }
  }
}
