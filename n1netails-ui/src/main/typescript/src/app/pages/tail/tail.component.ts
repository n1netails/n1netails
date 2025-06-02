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
import { TailUtilService } from '../../service/tail-util.service';

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
    public tailUtilService: TailUtilService,
    private tailService: TailService,
    private route: ActivatedRoute,
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
}
