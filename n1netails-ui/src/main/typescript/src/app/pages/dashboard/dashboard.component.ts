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
import { BaseChartDirective } from 'ng2-charts';
import { catchError, of } from 'rxjs';
import { HeaderComponent } from "../../shared/template/header/header.component";
import { SidenavComponent } from "../../shared/template/sidenav/sidenav.component";
import { UiConfigService } from "../../shared/ui-config.service";
import { AuthenticationService } from '../../service/authentication.service';
import { Router } from '@angular/router';
import { TailMetricsService } from '../../service/tail-metrics.service';

// todo remove
const count = 5;
const fakeDataUrl = 'https://randomuser.me/api/?results=5&inc=name,gender,email,nat&noinfo';

@Component({
  selector: 'app-dashboard',
  imports: [NzIconModule, NzLayoutModule, NzCardModule, NzGridModule, NzAvatarModule, NzListModule, NzSkeletonModule, BaseChartDirective, HeaderComponent, SidenavComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.less'
})
export class DashboardComponent implements OnInit {

  initLoading = true; // bug
  loadingMore = false;
  data: any[] = [];
  list: Array<{ loading: boolean; name: any }> = [];

  // metrics
  totalTailAlertsToday = 0;
  totalTailsResolved = 0;
  totalTailsNotResolved = 0;
  mttr = 0;

  // Tail Resoultion Status (Pie Chart)
  alertStatusData = {
    labels: ['Resolved', 'Unresolved'],
    datasets: [{ data: [this.totalTailsResolved, this.totalTailsNotResolved], backgroundColor: ['#F06D0F', '#F00F21'], borderWidth: 1, borderColor: '#F38A3F'}]
  };

  constructor(
    private http: HttpClient,
    private msg: NzMessageService,
    private uiConfigService: UiConfigService,
    private authenticationService: AuthenticationService,
    private tailMetricsService: TailMetricsService,
    private router: Router
  ) {}

  ngOnInit() {
    if (!this.authenticationService.isUserLoggedIn()) {
      this.router.navigate(['/login']);
    }

    const apiUrl = this.uiConfigService.getApiUrl();
    console.log('API URL:', apiUrl); // Log the API URL to verify it's loaded correctly

    this.getData((res: any) => {
      this.data = res.results;
      this.list = res.results;
      this.initLoading = false;
    });

    // metrics
    this.countAlerts();
  }

  countAlerts() {
    this.tailMetricsService.countTailAlertsToday().subscribe(result => {
      this.totalTailAlertsToday = result;
    });

    this.tailMetricsService.countTailAlertsResolved().subscribe(result => {
      this.totalTailsResolved = result;

      // todo make this better load after data has been received
      this.alertStatusData = {
        labels: ['Resolved', 'Unresolved'],
        datasets: [{ data: [this.totalTailsResolved, this.totalTailsNotResolved], backgroundColor: ['#F06D0F', '#F00F21'], borderWidth: 1, borderColor: '#F38A3F'}]
      };
    });

    this.tailMetricsService.countTailAlertsNotResolved().subscribe(result => {
      this.totalTailsNotResolved = result;

      // todo make this better load after data has been received
      this.alertStatusData = {
        labels: ['Resolved', 'Unresolved'],
        datasets: [{ data: [this.totalTailsResolved, this.totalTailsNotResolved], backgroundColor: ['#F06D0F', '#F00F21'], borderWidth: 1, borderColor: '#F38A3F'}]
      };
    });
    this.tailMetricsService.mttr().subscribe(result => {
      this.mttr = result;
    });
  }


  
  alertsTodayData = {
    labels: ['00:00', '01:00', '02:00', '03:00', '04:00', '05:00', '06:00', '07:00','08:00'],
    datasets: [{ label: 'Alerts', data: [5, 2, 1, 7, 3, 10, 7 , 8, 9], backgroundColor: '#F06D0F' }]
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



  getData(callback: (res: any) => void): void {
    this.http
      .get(fakeDataUrl)
      .pipe(catchError(() => of({ results: [] })))
      .subscribe((res: any) => callback(res));
  }

  onLoadMore(): void {
    this.loadingMore = true;
    this.list = this.data.concat([...Array(count)].fill({}).map(() => ({ loading: true, name: {} })));
    this.http
      .get(fakeDataUrl)
      .pipe(catchError(() => of({ results: [] })))
      .subscribe((res: any) => {
        this.data = this.data.concat(res.results);
        this.list = [...this.data];
        this.loadingMore = false;
      });
  }

  edit(item: any): void {
    this.msg.success(item.email);
  }
}
