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
import { HeaderComponent } from '../../shared/template/header/header.component';
import { SidenavComponent } from '../../shared/template/sidenav/sidenav.component';
import { UiConfigService } from '../../shared/util/ui-config.service';
import { AuthenticationService } from '../../service/authentication.service';
import { Router } from '@angular/router';
import { TailMetricsService } from '../../service/tail-metrics.service';
import { TailService } from '../../service/tail.service';
import { TailTypeResponse } from '../../service/tail-type.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { User } from '../../model/user';
import { DurationPipe } from '../../pipe/duration.pipe';
import { NzEmptyModule } from 'ng-zorro-antd/empty';
import { TailUtilService } from '../../shared/util/tail-util.service';
import { ResolveTailModalComponent } from '../../shared/components/resolve-tail-modal/resolve-tail-modal.component';
import { ResolveTailRequest, TailSummary } from '../../model/tail.model';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { UserService } from '../../service/user.service';
import { TutorialService } from '../../service/tutorial.service';

@Component({
  selector: 'app-dashboard',
  imports: [
    CommonModule,
    FormsModule,
    NzEmptyModule,
    NzIconModule,
    NzLayoutModule,
    NzCardModule,
    NzGridModule,
    NzAvatarModule,
    NzListModule,
    NzSkeletonModule,
    NzTagModule,
    BaseChartDirective,
    HeaderComponent,
    SidenavComponent,
    DurationPipe,
    ResolveTailModalComponent,
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.less',
})
export class DashboardComponent implements OnInit {
  user: User;
  isMobile = true;

  // metrics
  totalTailAlertsToday = 0;
  totalTailsResolved = 0;
  totalTailsNotResolved = 0;
  mttr = 0;

  // Bar Chart Options
  barChartOptions = { responsive: true, maintainAspectRatio: false };
  stackedBarOptions = {
    responsive: true,
    maintainAspectRatio: false,
    scales: { x: { stacked: true }, y: { stacked: true } },
    plugins: {
      legend: {
        display: true,
        position: 'bottom' as const,
      },
    },
  };

  // Line Chart Options
  lineChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
      },
    },
  };

  // Pie Chart Options
  pieChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: true,
        position: 'right' as const,
      },
    },
    cutout: '70%',
  };

  // Tail Resoultion Status (Pie Chart)
  alertStatusData = {
    labels: ['Resolved', 'Blocked', 'New', 'In progress'],
    datasets: [
      {
        data: [0, 0, 0, 0],
        backgroundColor: ['#F06D0F', '#F00F21', '#FFA500', '#1E90FF'],
        borderWidth: 3,
        borderColor: '#F38A3F',
      },
    ],
  };

  // Tail Alerts Hourly (Bar Chart)
  alertsTodayData = {
    labels: [
      '00:00',
      '01:00',
      '02:00',
      '03:00',
      '04:00',
      '05:00',
      '06:00',
      '07:00',
      '08:00',
      '09:00',
    ],
    datasets: [
      { label: 'Info', data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0], backgroundColor: '#1E90FF' },
      { label: 'Success', data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0], backgroundColor: 'green' },
      { label: 'Warn', data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0], backgroundColor: '#FFA500' },
      { label: 'Error', data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0], backgroundColor: '#FF4500' },
      { label: 'Critical', data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0], backgroundColor: '#FF0000' },
      { label: 'Kuda', data: [0, 0, 0, 0, 0, 0, 0, 0, 0, 0], backgroundColor: '#8B0000' },
    ],
  };

  // Monthly Alerts Stacked (Stacked Bar Chart)
  monthlyAlertsData = {
    labels: [
      'Apr 1',
      'Apr 2',
      'Apr 3',
      'Apr 4',
      'Apr 5',
      'Apr 6',
      'Apr 7',
      'Apr 8',
      'Apr 9',
      'Apr 10',
      'Apr 11',
      'Apr 12',
      'Apr 13',
      'Apr 14',
      'Apr 15',
      'Apr 16',
      'Apr 17',
      'Apr 18',
      'Apr 19',
      'Apr 20',
      'Apr 21',
      'Apr 22',
      'Apr 23',
      'Apr 24',
      'Apr 25',
      'Apr 26',
      'Apr 27',
      'Apr 28',
      'Apr 29',
      '...',
    ],
    datasets: [
      { label: 'Info', data: [0], backgroundColor: '#1E90FF' },
      { label: 'Success', data: [0], backgroundColor: 'green' },
      { label: 'Warn', data: [0], backgroundColor: '#FFA500' },
      { label: 'Error', data: [0], backgroundColor: '#FF4500' },
      { label: 'Critical', data: [0], backgroundColor: '#FF0000' },
      { label: 'Kuda', data: [0], backgroundColor: '#8B0000' },
    ],
  };

  // Mean Time To Resolution Hours (Line Chart)
  mttrLineData = {
    labels: ['Apr 24', 'Apr 25', 'Apr 26', 'Apr 27', 'Apr 28', 'Apr 29', 'Apr 30'],
    datasets: [
      {
        label: 'MTTR (hours)',
        data: [0],
        borderColor: '#F06D0F',
        tension: 0.4,
      },
    ],
  };

  // 9 newest tails
  initLoading = true; // bug
  loadingMore = false;
  data: any[] = [];
  list: Array<{
    loading: boolean;
    id: number;
    title: string;
    description: string;
    level: string;
    type: string;
    status: string;
    assignedUserId: number;
  }> = [];

  // tail domain info
  tailTypes: TailTypeResponse[] = [];

  constructor(
    public tailUtilService: TailUtilService,
    private msg: NzMessageService,
    private uiConfigService: UiConfigService,
    private authenticationService: AuthenticationService,
    private tailMetricsService: TailMetricsService,
    private tailService: TailService,
    private router: Router,
    private breakpointObserver: BreakpointObserver,
    private userService: UserService,
    private tutorialService: TutorialService
  ) {
    this.user = this.authenticationService.getUserFromLocalCache();
  }

  ngOnInit() {
    if (!this.authenticationService.isUserLoggedIn()) {
      this.router.navigate(['/login']);
    }

    const apiUrl = this.uiConfigService.getApiUrl();
    console.log('API URL:', apiUrl);

    this.initDashboard();

    this.breakpointObserver.observe([Breakpoints.Small, Breakpoints.XSmall]).subscribe((result) => {
      this.isMobile = result.matches;
      this.updateChartOptions();
      this.runTutorial();
    });
  }

  updateChartOptions() {
    this.stackedBarOptions = {
      responsive: true,
      maintainAspectRatio: false,
      scales: { x: { stacked: true }, y: { stacked: true } },
      plugins: {
        legend: {
          display: !this.isMobile,
          position: 'bottom' as const,
        },
      },
    };

    this.pieChartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: true,
          position: 'right',
        },
      },
      cutout: '70%',
    };
  }

  runTutorial() {
    this.userService.getSelf().subscribe((user) => {
      if (!user.tutorialCompleted && !this.userService.tutorialInProgress() && !this.isMobile) {
        this.tutorialService.startTutorial();
        this.userService.setTutorialInProgress(true);
      }
    });
  }

  goToTail(id: number) {
    this.router.navigate(['/tail', id]);
  }

  initDashboard() {
    this.getTop9NewestTails((res: any) => {
      this.data = res;
      this.list = res;
      this.initLoading = false;
    });
    this.getMetrics();
  }

  getMetrics() {
    const userTimezone = Intl.DateTimeFormat().resolvedOptions().timeZone;
    this.tailMetricsService.countTailAlertsToday(userTimezone).subscribe((result) => {
      this.totalTailAlertsToday = result;
    });
    this.tailMetricsService.countTailAlertsResolved().subscribe((result) => {
      this.totalTailsResolved = result;
      this.updateAlertStatusData();
    });
    this.tailMetricsService.countTailAlertsNotResolved().subscribe((result) => {
      this.totalTailsNotResolved = result;
      this.updateAlertStatusData();
    });
    this.tailMetricsService.mttr().subscribe((result) => {
      this.mttr = result;
    });
    this.tailMetricsService.mttrLast7Days().subscribe((result) => {
      this.mttrLineData = {
        labels: result.labels,
        datasets: [
          { label: 'MTTR (hours)', data: result.data, borderColor: '#F06D0F', tension: 0.4 },
        ],
      };
    });
    this.tailMetricsService.getTailAlertsHourlyByLevel(userTimezone).subscribe((result) => {
      this.alertsTodayData = {
        labels: result.labels,
        datasets: result.datasets.map((dataset) => ({
          ...dataset,
          backgroundColor: this.tailUtilService.getBarChartLevelColor(dataset.label),
        })),
      };
    });
    this.tailMetricsService.getTailResolutionStatus().subscribe((result) => {
      console.log('tail status count', result);

      this.alertStatusData = {
        labels: result.labels,
        datasets: [
          {
            data: result.data,
            backgroundColor: result.labels.map((label) =>
              this.tailUtilService.getPieChartStatusColor(label)
            ),
            borderWidth: 1.5,
            borderColor: '#F38A3F',
          },
        ],
      };
    });
    this.tailMetricsService.getTailMonthlySummary(userTimezone).subscribe((result) => {
      this.monthlyAlertsData = {
        labels: result.labels,
        datasets: result.datasets.map((dataset) => ({
          ...dataset,
          backgroundColor: this.tailUtilService.getBarChartLevelColor(dataset.label),
        })),
      };
    });
  }

  updateAlertStatusData() {
    this.alertStatusData = {
      labels: ['Resolved', 'Not Resolved'],
      datasets: [
        {
          data: [this.totalTailsResolved, this.totalTailsNotResolved],
          backgroundColor: ['#F06D0F', '#F00F21'],
          borderWidth: 3,
          borderColor: '#F38A3F',
        },
      ],
    };
  }

  getTop9NewestTails(callback: (res: any) => void): void {
    this.tailService.getTop9NewestTails().subscribe((result) => {
      callback(result);
    });
  }

  // Modal properties
  resolveModalVisible = false;
  selectedItem: any = null;

  resolve(item: any): void {
    this.selectedItem = item;
    this.resolveModalVisible = true;
  }

  handleResolveCancel(): void {
    this.resolveModalVisible = false;
    this.selectedItem = null;
  }

  handleResolveOk(note: string): void {
    if (!this.selectedItem) return;

    const tailSummary: TailSummary = {
      id: this.selectedItem.id,
      title: this.selectedItem.title,
      description: this.selectedItem.description,
      timestamp: this.selectedItem.timestamp,
      resolvedTimestamp: this.selectedItem.resolvedTimestamp,
      assignedUserId: this.user.id,
      level: this.selectedItem.level,
      type: this.selectedItem.type,
      status: this.selectedItem.status,
    };

    const tailResolveRequest: ResolveTailRequest = {
      userId: this.user.id,
      tailSummary: tailSummary,
      note: note,
    };

    this.tailService.markTailResolved(tailResolveRequest).subscribe({
      next: (result) => {
        this.msg.success(`Resolved "${this.selectedItem.title}"`);
        this.resolveModalVisible = false;
        this.selectedItem = null;
        this.initDashboard();
      },
      error: (err) => {
        this.msg.error(
          `Unable to mark tail "${this.selectedItem.title}" as resolved. Error: ${err.message || err}`
        );
      },
    });
  }
}
