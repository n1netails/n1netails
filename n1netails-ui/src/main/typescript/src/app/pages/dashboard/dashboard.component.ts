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
import { catchError, of } from 'rxjs';
import { HeaderComponent } from "../../shared/template/header/header.component";
import { SidenavComponent } from "../../shared/template/sidenav/sidenav.component";
import { UiConfigService } from "../../shared/ui-config.service";
import { AuthenticationService } from '../../service/authentication.service';
import { Router } from '@angular/router';
import { TailMetricsService } from '../../service/tail-metrics.service';
import { TailResponse, TailService } from '../../service/tail.service';

// todo remove
const count = 5;
const fakeDataUrl = 'https://randomuser.me/api/?results=5&inc=name,gender,email,nat&noinfo';

@Component({
  selector: 'app-dashboard',
  imports: [NzIconModule, NzLayoutModule, NzCardModule, NzGridModule, NzAvatarModule, NzListModule, NzSkeletonModule, NzTagModule, BaseChartDirective, HeaderComponent, SidenavComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.less'
})
export class DashboardComponent implements OnInit {

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
      { label: 'Success', data: [0], backgroundColor: '#FFD700' },
      { label: 'Warn', data: [0], backgroundColor: '#FFA500' },
      { label: 'Error', data: [0], backgroundColor: '#FF4500' },
      { label: 'Critical', data: [0], backgroundColor: '#FF0000' },
      { label: 'Kuda', data: [0], backgroundColor: '#8B0000' },
    ]
  };

  initLoading = true; // bug
  loadingMore = false;
  data: any[] = [];
  list: Array<{ loading: boolean; title: string, description: string, level: string, type: string, status: string }> = [];

  constructor(
    private http: HttpClient,
    private msg: NzMessageService,
    private uiConfigService: UiConfigService,
    private authenticationService: AuthenticationService,
    private tailMetricsService: TailMetricsService,
    private tailService: TailService,
    private router: Router
  ) {}

  ngOnInit() {
    if (!this.authenticationService.isUserLoggedIn()) {
      this.router.navigate(['/login']);
    }

    const apiUrl = this.uiConfigService.getApiUrl();
    console.log('API URL:', apiUrl); // Log the API URL to verify it's loaded correctly

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
          { label: result.datasets[1].label, data: result.datasets[1].data, backgroundColor: '#FFD700' },
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

  edit(item: any): void {
    this.msg.success(item.email);
  }

  // todo get levels list from api
  getLevelColor(level: string): string {
    switch (level?.toLowerCase()) {
      case 'critical': return 'red';
      case 'warning': return 'orange';
      case 'info': return 'blue';
      default: return 'orange';
    }
  }

  // todo get type list from api
  getTypeColor(type: string): string {
    switch (type?.toLowerCase()) {
      case 'email': return 'geekblue';
      case 'system_alert': return 'purple';
      case 'push': return 'cyan';
      default: return 'default';
    }
  }

  // todo get status list from api
  getStatusColor(status: string): string {
    switch (status?.toLowerCase()) {
      case 'new': return 'green';
      case 'resolved': return 'gold';
      case 'acknowledged': return 'volcano';
      default: return 'default';
    }
  }
}
