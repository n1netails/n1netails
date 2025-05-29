import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { NzMessageService } from 'ng-zorro-antd/message';
import { of } from 'rxjs';

import { DashboardComponent } from './dashboard.component';
import { TailMetricsService } from '../../service/tail-metrics.service';
import { AuthenticationService } from '../../service/authentication.service';
import { UiConfigService } from '../../shared/ui-config.service';
import { HeaderComponent } from "../../shared/template/header/header.component";
import { SidenavComponent } from "../../shared/template/sidenav/sidenav.component";
import { BaseChartDirective } from 'ng2-charts';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { NzListModule } from 'ng-zorro-antd/list';
import { NzSkeletonModule } from 'ng-zorro-antd/skeleton';


describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let tailMetricsServiceMock: Partial<TailMetricsService>;
  let authenticationServiceMock: Partial<AuthenticationService>;
  let nzMessageServiceMock: Partial<NzMessageService>;
  let resolvedOptionsSpy: jasmine.Spy;

  const mockUserTimezone = 'Asia/Tokyo';

  beforeEach(async () => {
    // Mock services
    tailMetricsServiceMock = {
      countTailAlertsToday: jasmine.createSpy('countTailAlertsToday').and.returnValue(of(10)),
      countTailAlertsResolved: jasmine.createSpy('countTailAlertsResolved').and.returnValue(of(5)),
      countTailAlertsNotResolved: jasmine.createSpy('countTailAlertsNotResolved').and.returnValue(of(5)),
      mttr: jasmine.createSpy('mttr').and.returnValue(of(3600)),
      getTailAlertsHourly: jasmine.createSpy('getTailAlertsHourly').and.returnValue(of({ labels: [], data: [] }))
    };

    authenticationServiceMock = {
      isUserLoggedIn: jasmine.createSpy('isUserLoggedIn').and.returnValue(true)
    };

    nzMessageServiceMock = {
      success: jasmine.createSpy('success') // Mock any methods used from NzMessageService
    };
    
    // Spy on Intl.DateTimeFormat().resolvedOptions()
    resolvedOptionsSpy = spyOn(Intl.DateTimeFormat.prototype, 'resolvedOptions').and.returnValue({
        timeZone: mockUserTimezone,
        locale: 'en-US', // ensure all expected properties are present
        calendar: 'gregory',
        numberingSystem: 'latn',
        hourCycle: 'h23',
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
    } as Intl.ResolvedDateTimeFormatOptions);


    await TestBed.configureTestingModule({
      imports: [
        DashboardComponent, // Import standalone component
        HttpClientTestingModule,
        RouterTestingModule,
        // Import NgZorro modules used by the component's template, if not already imported by DashboardComponent itself
        NzIconModule,
        NzLayoutModule,
        NzCardModule,
        NzGridModule,
        NzAvatarModule,
        NzListModule,
        NzSkeletonModule,
        BaseChartDirective, // ng2-charts
        // HeaderComponent, // If these are standalone, they should be imported
        // SidenavComponent
      ],
      providers: [
        { provide: TailMetricsService, useValue: tailMetricsServiceMock },
        { provide: AuthenticationService, useValue: authenticationServiceMock },
        { provide: NzMessageService, useValue: nzMessageServiceMock },
        // UiConfigService might be needed if not provided by default or if its methods are called
        // For this test, assuming UiConfigService.getApiUrl() is not critical for getMetrics logic being tested
        UiConfigService 
      ]
    })
    // Remove .compileComponents() for standalone components as it's often not needed or can cause issues.
    // However, if HeaderComponent or SidenavComponent are part of the template and not standalone,
    // or if there are template-related issues, .compileComponents() might be necessary.
    // For now, let's assume DashboardComponent handles its template dependencies correctly.
    // .compileComponents(); 
    // If HeaderComponent and SidenavComponent are standalone and used in the template,
    // they should be listed in the `imports` array of DashboardComponent itself.
    // If they are not standalone, DashboardComponent should import their modules.
    // Given the previous error, it's likely they are part of DashboardComponent's imports.

    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
    // ngOnInit is called by fixture.detectChanges()
  });

  it('should create', () => {
    fixture.detectChanges(); // This calls ngOnInit
    expect(component).toBeTruthy();
  });

  describe('getMetrics', () => {
    beforeEach(() => {
        // Reset spies for resolvedOptions before each test in this describe block
        // to ensure a clean state if it was manipulated in other tests.
        resolvedOptionsSpy.and.returnValue({
            timeZone: mockUserTimezone,
            locale: 'en-US', calendar: 'gregory', numberingSystem: 'latn', hourCycle: 'h23',
            day: '2-digit', month: '2-digit', year: 'numeric'
        } as Intl.ResolvedDateTimeFormatOptions);
    });

    it('should call TailMetricsService methods with user timezone from Intl.DateTimeFormat', fakeAsync(() => {
      fixture.detectChanges(); // Calls ngOnInit, which calls getMetrics
      tick(); // Complete asynchronous operations

      expect(resolvedOptionsSpy).toHaveBeenCalled();
      expect(tailMetricsServiceMock.countTailAlertsToday).toHaveBeenCalledWith(mockUserTimezone);
      expect(tailMetricsServiceMock.getTailAlertsHourly).toHaveBeenCalledWith(mockUserTimezone);
    }));

    it('should update metrics properties after service calls', fakeAsync(() => {
      const mockTodayCount = 120;
      const mockHourlyResponse = { labels: ['01:00', '02:00'], data: [10, 20] };
      (tailMetricsServiceMock.countTailAlertsToday as jasmine.Spy).and.returnValue(of(mockTodayCount));
      (tailMetricsServiceMock.getTailAlertsHourly as jasmine.Spy).and.returnValue(of(mockHourlyResponse));
      
      fixture.detectChanges(); // Calls ngOnInit, which calls getMetrics
      tick(); // Complete asynchronous operations

      expect(component.totalTailAlertsToday).toBe(mockTodayCount);
      expect(component.alertsTodayData.labels).toEqual(mockHourlyResponse.labels);
      expect(component.alertsTodayData.datasets[0].data).toEqual(mockHourlyResponse.data);
    }));
  });
});
