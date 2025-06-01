import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { TailService, TailResponse } from '../../service/tail.service.ts';
import { CommonModule } from '@angular/common';
import { NzTagModule } from 'ng-zorro-antd/tag'; // For nz-tag
import { NzSpinModule } from 'ng-zorro-antd/spin'; // For nz-spin
import { NzAlertModule } from 'ng-zorro-antd/alert'; // For nz-alert
import { NzEmptyModule } from 'ng-zorro-antd/empty'; // For nz-empty

@Component({
  selector: 'app-tail',
  standalone: true, // Make it a standalone component
  imports: [
    CommonModule, // For *ngIf, etc.
    NzTagModule,   // For <nz-tag>
    NzSpinModule,
    NzAlertModule,
    NzEmptyModule
  ],
  templateUrl: './tail.component.html',
  styleUrls: ['./tail.component.less'] // Corrected from styleUrl to styleUrls
})
export class TailComponent implements OnInit {
  tail: TailResponse | null = null;
  error: string | null = null;
  isLoading: boolean = true;

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
}
