import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { TailService, TailResponse, Page } from '../../service/tail.service'; // Added
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzListModule } from 'ng-zorro-antd/list';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzSkeletonModule } from 'ng-zorro-antd/skeleton';
import { BaseChartDirective } from 'ng2-charts';
import { catchError, of } from 'rxjs';
import { HeaderComponent } from "../../shared/template/header/header.component";
import { SidenavComponent } from "../../shared/template/sidenav/sidenav.component";
import { UiConfigService } from "../../shared/ui-config.service";
import { AuthenticationService } from '../../service/authentication.service';
import { Router } from '@angular/router';
import { NzPaginationModule } from 'ng-zorro-antd/pagination'; // Added

@Component({
  selector: 'app-dashboard',
  imports: [NzIconModule, NzLayoutModule, NzCardModule, NzGridModule, NzAvatarModule, NzListModule, NzSkeletonModule, BaseChartDirective, HeaderComponent, SidenavComponent, NzPaginationModule], // Added NzPaginationModule
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.less'
})
export class DashboardComponent implements OnInit {

  tails: TailResponse[] = [];
  currentPage: number = 0; // 0-indexed for API
  pageSize: number = 10;
  totalElements: number = 0;
  totalPages: number = 0;
  loadingTails: boolean = true;


  constructor(
    private http: HttpClient, // Kept for chart data for now, can be removed if charts are refactored
    private msg: NzMessageService,
    private uiConfigService: UiConfigService,
    private authenticationService: AuthenticationService,
    private router: Router,
    private tailService: TailService // Added
  ) {}

  ngOnInit() {
    if (!this.authenticationService.isUserLoggedIn()) {
      this.router.navigate(['/login']);
    }

    const apiUrl = this.uiConfigService.getApiUrl();
    console.log('API URL:', apiUrl); // Log the API URL to verify it's loaded correctly

    this.loadTails();
  }

  loadTails(page: number = this.currentPage, size: number = this.pageSize): void {
    this.loadingTails = true;
    this.tailService.getTails(page, size).subscribe(
      (response: Page<TailResponse>) => {
        this.tails = response.content;
        this.currentPage = response.number;
        this.pageSize = response.size;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.loadingTails = false;
      },
      error => {
        this.loadingTails = false;
        this.msg.error('Failed to load tails.');
        console.error('Error loading tails:', error);
      }
    );
  }

  onPageIndexChange(pageIndex: number): void {
    // pageIndex is 1-based from nz-pagination
    this.loadTails(pageIndex - 1, this.pageSize);
  }

  alertsTodayData = {
    labels: ['00:00', '01:00', '02:00', '03:00', '04:00', '05:00', '06:00', '07:00','08:00'],
    datasets: [{ label: 'Alerts', data: [5, 2, 1, 7, 3, 10, 7 , 8, 9], backgroundColor: '#F06D0F' }]
  };
  
  alertStatusData = {
    labels: ['Resolved', 'Unresolved'],
    datasets: [{ data: [34, 18], backgroundColor: ['#F06D0F', '#F00F21'], borderWidth: 1, borderColor: '#F38A3F'}]
  };
  
  monthlyAlertsData = {
    labels: ['Apr 1', 'Apr 2', 'Apr 3', 'Apr 4', 'Apr 5', 'Apr 6', 'Apr 7', 'Apr 8', 'Apr 9', 'Apr 10', 'Apr 11', 'Apr 12', 'Apr 13', 'Apr 14', 'Apr 15', 'Apr 16', 'Apr 17', 'Apr 18', 'Apr 19', 'Apr 20', 'Apr 21', 'Apr 22', 'Apr 23', 'Apr 24', 'Apr 25', 'Apr 26', 'Apr 27', 'Apr 28', 'Apr 29', 'Apr 30', '...'],
    datasets: [
      { label: 'Critical', data: [10, 5, 2, 3, 8, 9, 3, 1, 24, 0, 5, 9, 16, 2, 1, 15 ,21 ,5 ,6 ,9 ,5 ,1 ,2 ,8, 5 , 3, 1 , 5 ,3, 4], backgroundColor: '#F00F21' },
      { label: 'Warning', data: [20, 10, 7, 2, 5, 4, 12, 2, 5, 1, 8, 3, 4, 4, 8, 3 ,3 ,8 ,18 ,7 ,14 ,5 ,2 ,18, 3 , 1, 17 , 16 ,8, 48], backgroundColor: '#F06D0F' }
    ]
  };
  
  mttrLineData = {
    labels: ['Apr 28', 'Apr 29', 'Apr 30'],
    datasets: [
      {
        label: 'MTTR (hours)',
        data: [2.5, 2.1, 1.9],
        borderColor: '#F06D0F',
        tension: 0.4
      }
    ]
  };
  
  userAlertLoadData = {
    labels: ['Alice', 'Bob', 'Carol'],
    datasets: [{ label: 'Assigned Alerts', data: [12, 9, 7], backgroundColor: '#59C7FF' }]
  };
  
  barChartOptions = { responsive: true, maintainAspectRatio: false, };
  stackedBarOptions = { 
    responsive: true, 
    maintainAspectRatio: false,
    scales: { x: { stacked: true }, y: { stacked: true } } 
  };
  lineChartOptions = { responsive: true, maintainAspectRatio: false, };
  doughnutOptions = { 
    responsive: true,
    maintainAspectRatio: false,
  };

  // edit method might need to be updated based on TailResponse structure if it's used
  edit(item: TailResponse): void {
    // Placeholder for actual edit functionality
    this.msg.success(`Editing: ${item.title}`);
  }
}
