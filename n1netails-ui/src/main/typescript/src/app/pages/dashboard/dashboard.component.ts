import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzListModule } from 'ng-zorro-antd/list';
import { NzMessageService } from 'ng-zorro-antd/message';
import { NzSkeletonModule } from 'ng-zorro-antd/skeleton';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { BaseChartDirective } from 'ng2-charts';
import { HeaderComponent } from "../../shared/template/header/header.component";
import { SidenavComponent } from "../../shared/template/sidenav/sidenav.component";
import { UiConfigService } from "../../shared/ui-config.service";
import { AuthenticationService } from '../../service/authentication.service';
import { Router } from '@angular/router';
import { TailMetricsService } from '../../service/tail-metrics.service';
import { ResolveTailRequest, TailService, TailSummary } from '../../service/tail.service';
import { TailTypeResponse } from '../../service/tail-type.service';
import { NzModalModule } from 'ng-zorro-antd/modal';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { User } from '../../model/user';
import { DurationPipe } from '../../pipe/duration.pipe';
import { NzEmptyModule } from 'ng-zorro-antd/empty';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule, FormsModule,NzEmptyModule,NzIconModule, NzModalModule, NzLayoutModule, NzCardModule, NzGridModule, NzAvatarModule, NzListModule, NzSkeletonModule, NzTagModule, BaseChartDirective, HeaderComponent, SidenavComponent,DurationPipe],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.less'
})
export class DashboardComponent implements OnInit {

  user: User;

  // metrics
  totalTailAlertsToday = 0;
  totalTailsResolved = 0;
  totalTailsNotResolved = 0;
  mttr = 0;

  // Bar Chart Options
  barChartOptions = { responsive: true, maintainAspectRatio: false, };
  stackedBarOptions = { 
    responsive: true, 
    maintainAspectRatio: false,
    scales: { x: { stacked: true }, y: { stacked: true } } 
  };

  // Line Chart Options
  lineChartOptions = { responsive: true, maintainAspectRatio: false, };
  doughnutOptions = { 
    responsive: true,
    maintainAspectRatio: false,
  };

  // Tail Resoultion Status (Pie Chart)
  alertStatusData = {
    labels: ['Resolved', 'Not Resolved'],
    datasets: [{ data: [this.totalTailsResolved, this.totalTailsNotResolved], backgroundColor: ['#F06D0F', '#F00F21'], borderWidth: 1, borderColor: '#F38A3F'}]
  };

  // Tail Alerts Hourly (Bar Chart)
  alertsTodayData = {
    labels: ['00:00', '01:00', '02:00', '03:00', '04:00', '05:00', '06:00', '07:00','08:00', '09:00'],
    datasets: [{ label: 'Alerts', data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0], backgroundColor: '#F06D0F' }]
  };

  // Monthly Alerts Stacked (Stacked Bar Chart)
  monthlyAlertsData = {
    labels: ['Apr 1', 'Apr 2', 'Apr 3', 'Apr 4', 'Apr 5', 'Apr 6', 'Apr 7', 'Apr 8', 'Apr 9', 'Apr 10', 'Apr 11', 'Apr 12', 'Apr 13', 'Apr 14', 'Apr 15', 'Apr 16', 'Apr 17', 'Apr 18', 'Apr 19', 'Apr 20', 'Apr 21', 'Apr 22', 'Apr 23', 'Apr 24', 'Apr 25', 'Apr 26', 'Apr 27', 'Apr 28', 'Apr 29', '...'],
    datasets: [
      { label: 'Info', data: [0], backgroundColor: '#1E90FF' },
      { label: 'Success', data: [0], backgroundColor: 'green' },
      { label: 'Warn', data: [0], backgroundColor: '#FFA500' },
      { label: 'Error', data: [0], backgroundColor: '#FF4500' },
      { label: 'Critical', data: [0], backgroundColor: '#FF0000' },
      { label: 'Kuda', data: [0], backgroundColor: '#8B0000' },
    ]
  };

  // 9 newest tails
  initLoading = true; // bug
  loadingMore = false;
  data: any[] = [];
  list: Array<{ loading: boolean; title: string, description: string, level: string, type: string, status: string }> = [];

  // tail domain info
  tailTypes: TailTypeResponse[] = [];

  constructor(
    private http: HttpClient,
    private msg: NzMessageService,
    private uiConfigService: UiConfigService,
    private authenticationService: AuthenticationService,
    private tailMetricsService: TailMetricsService,
    private tailService: TailService,
    private router: Router
  ) {
    this.user = this.authenticationService.getUserFromLocalCache();
  }

  ngOnInit() {
    if (!this.authenticationService.isUserLoggedIn()) {
      this.router.navigate(['/login']);
    }

    const apiUrl = this.uiConfigService.getApiUrl();
    console.log('API URL:', apiUrl); // Log the API URL to verify it's loaded correctly

    this.initDashboard();
  }

  initDashboard() {
    // get top 9 newest tails
    this.getTop9NewestTails((res: any) => {
      console.log('getData', res);

      this.data = res;
      console.log('data', this.data);
      this.list = res;
      console.log('list', this.list);
      this.initLoading = false;
    });

    // metrics
    this.getMetrics();
  }

  getMetrics() {
    // GETS THE USERS TIMEZONE!
    const userTimezone = Intl.DateTimeFormat().resolvedOptions().timeZone; // Get user's timezone

    // pass timezone here in countTailAlertsToday
    this.tailMetricsService.countTailAlertsToday(userTimezone).subscribe(result => { // Pass timezone
      this.totalTailAlertsToday = result;
    });

    this.tailMetricsService.countTailAlertsResolved().subscribe(result => {
      this.totalTailsResolved = result;

      // todo make this better load after totalTailsResolved and totalTailsNotResolved data has been received
      this.alertStatusData = {
        labels: ['Resolved', 'Not Resolved'],
        datasets: [{ data: [this.totalTailsResolved, this.totalTailsNotResolved], backgroundColor: ['#F06D0F', '#F00F21'], borderWidth: 1, borderColor: '#F38A3F'}]
      };
    });

    this.tailMetricsService.countTailAlertsNotResolved().subscribe(result => {
      this.totalTailsNotResolved = result;

      // todo make this better load after totalTailsResolved and totalTailsNotResolved data has been received
      this.alertStatusData = {
        labels: ['Resolved', 'Not Resolved'],
        datasets: [{ data: [this.totalTailsResolved, this.totalTailsNotResolved], backgroundColor: ['#F06D0F', '#F00F21'], borderWidth: 1, borderColor: '#F38A3F'}]
      };
    });

    // mttr
    this.tailMetricsService.mttr().subscribe(result => {
      console.log('MTTR', result);
      this.mttr = result;
    });

    // hourly
    // pass timezone here in getTailAlertsHourly
    this.tailMetricsService.getTailAlertsHourly(userTimezone).subscribe(result => { // Pass timezone

      console.log('ALERTS TODAY', result);
      this.alertsTodayData = {
        labels: result.labels,
        datasets: [{ label: 'Alerts', data: result.data, backgroundColor: '#F06D0F' }]
      };
    });

    // monthly
    this.tailMetricsService.getTailMonthlySummary(userTimezone).subscribe(result => {
      console.log('ALERTS THIS MONTH', result);
      console.log('size {}', result.datasets.length);
      this.monthlyAlertsData = {
        labels: result.labels,
        datasets: [
          // INFO
          { label: result.datasets[0].label, data: result.datasets[0].data, backgroundColor: '#1E90FF' },
          // SUCCESS
          { label: result.datasets[1].label, data: result.datasets[1].data, backgroundColor: 'green' },
          // WARN
          { label: result.datasets[2].label, data: result.datasets[2].data, backgroundColor: '#FFA500' },
          // ERROR
          { label: result.datasets[3].label, data: result.datasets[3].data, backgroundColor: '#FF4500' },
          // CRITICAL
          { label: result.datasets[4].label, data: result.datasets[4].data, backgroundColor: '#FF0000' },
          // KUDA
          { label: result.datasets[5].label, data: result.datasets[5].data, backgroundColor: '#8B0000' },
        ]
      };
    });
  }



  // todo get the mean time to resolve
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

  

  getTop9NewestTails(callback: (res: any) => void): void {
    this.tailService.getTop9NewestTails().subscribe(result => {
      console.log('top 9 newest tails', result);
      callback(result);
    });
  }

  resolveModalVisible = false;
  selectedItem: any = null;
  resolveNote: string = '';


  resolve(item: any): void {
    // this.msg.success(item.email);
    this.selectedItem = item;
    console.log('selected item', this.selectedItem);
    this.resolveNote = '';
    this.resolveModalVisible = true;
  }

  // Cancel modal
  handleResolveCancel(): void {
    this.resolveModalVisible = false;
    this.selectedItem = null;
    this.resolveNote = '';
  }

  // Confirm resolve
  handleResolveOk(): void {
    const tailSummary: TailSummary = {
      id: this.selectedItem.id,
      title: this.selectedItem.title,
      description: this.selectedItem.description,
      timestamp: this.selectedItem.timestamp,
      resolvedtimestamp: this.selectedItem.resolvedTimestamp,
      assignedUserId: this.user.id,
      level: this.selectedItem.level,
      type: this.selectedItem.type,
      status: this.selectedItem.status,
    };

    const tailResolveRequest: ResolveTailRequest = {
      userId: this.user.id,
      tailSummary: tailSummary, 
      note: this.resolveNote,
    };

    this.tailService.markTailResolved(tailResolveRequest).subscribe({
      next: (result) => {
        this.msg.success(`Resolved "${this.selectedItem.title}"`);
        this.resolveModalVisible = false;
        this.selectedItem = null;
        this.resolveNote = '';
        this.initDashboard();
      }, 
      error: (err) => {
        this.msg.error(`Unable to mark tail "${this.selectedItem.title}" as resolved. Error: ${err}`);
      },
    });
  }

  view(item: any): void {
    this.msg.success(item.email);
  }

  getLevelColor(level: string): string {
    switch (level?.toUpperCase()) {
      case 'INFO': return 'blue';
      case 'SUCCESS': return 'green';
      case 'WARN': return 'orange';
      case 'ERROR': return 'red';
      case 'CRITICAL': return 'volcano';
      default: return 'orange';
    }
  }

  getStatusColor(status: string): string {
    switch (status?.toUpperCase()) {
      case 'NEW': return 'green';
      case 'IN_PROGRESS': return 'gold';
      case 'BLOCKED': return 'red';
      case 'RESOLVED': return 'blue';
      default: return 'orange';
    }
  }

  private typeColorMap: { [type: string]: string } = {};

  getTypeColor(type: string): string {
    if (!type) return 'default';
    const key = type.toLowerCase();

    const zorroColors = [
      'geekblue', 'purple', 'magenta', 'red', 'volcano', 'orange', 'gold', 'lime', 'green',
      'cyan', 'blue',  
    ];
    // Pick a color based on hash of type for consistency
    let hash = 0;
    for (let i = 0; i < key.length; i++) {
      hash = key.charCodeAt(i) + ((hash << 5) - hash);
    }
    const color = zorroColors[Math.abs(hash) % zorroColors.length];
    this.typeColorMap[key] = color;
    return color;
  }

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
