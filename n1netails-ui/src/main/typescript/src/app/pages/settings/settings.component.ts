import { Component, OnInit } from '@angular/core';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { HeaderComponent } from '../../shared/template/header/header.component';
import { SidenavComponent } from '../../shared/template/sidenav/sidenav.component';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzFormModule } from 'ng-zorro-antd/form';
import { FormsModule } from '@angular/forms';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzCheckboxModule, NzCheckBoxOptionInterface } from 'ng-zorro-antd/checkbox';
import { CommonModule } from '@angular/common';
import { UserService } from '../../service/user.service';
import { TailLevel, TailLevelService } from '../../service/tail-level.service';
import { TailStatus, TailStatusService } from '../../service/tail-status.service';
import { TailType, TailTypeService } from '../../service/tail-type.service';
import { User } from '../../model/user';
import { AuthenticationService } from '../../service/authentication.service';

// TODO:: create n1ne token service and move these interfaces into 'model'
interface N1neToken {
  name?: string;
  token: string;
  createdAt: Date;
  expiresAt: Date;
  revoked: boolean;
  lastUsedAt?: Date;
}

interface CreateTokenRequest extends N1neToken {
  userId: number;
  organizationId?: number;
}

interface N1neTokenResponse extends N1neToken {
  id: number;
  userId: number;
  organizationId?: number;
}

@Component({
  selector: 'app-settings',
  imports: [NzLayoutModule,HeaderComponent,SidenavComponent,NzCardModule,NzFormModule,FormsModule,NzTableModule,NzCheckboxModule,CommonModule],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.less'
})
export class SettingsComponent implements OnInit {

  private user: User;

  // Alert Levels, Statuses, Types
  tailLevels: TailLevel[] = [];
  tailStatuses: TailStatus[] = [];
  tailTypes: TailType[] = [];

  newTailLevel: string = '';
  newTailStatus: string = '';
  newTailType: string = '';

  constructor(
    private authenticationService: AuthenticationService,
    private tailLevelService: TailLevelService,
    private tailStatusService: TailStatusService,
    private tailTypeService: TailTypeService,
  ) {
    this.updateAlertTypeOptions();
    this.user = this.authenticationService.getUserFromLocalCache();

  }

  ngOnInit(): void {
    this.tailLevelService.getTailLevels().subscribe((response: TailLevel[]) => {
      this.tailLevels = response;
      console.log('tail levels:', this.tailLevels);
    });
    this.tailStatusService.getTailStatusList().subscribe((response: TailStatus[]) => {
      this.tailStatuses = response;
      console.log('tail statuses:', this.tailStatuses);
    });
    this.tailTypeService.getTailTypes().subscribe((response: TailType[]) => {
      this.tailTypes = response;
      console.log('tail types:', this.tailTypes);
    });
  }

  // Alert Token Manager
  n1neTokens: N1neToken[] = [
    { token: 'abc123', expiresAt: new Date('2025-12-31'), createdAt: new Date(), revoked: false },
    { token: 'def456', expiresAt: new Date('2025-11-30'), createdAt: new Date(), revoked: false }
  ];

  newTokenExpiration: string = '';

  onCreateToken() {
    if (this.newTokenExpiration) {
      const newToken: N1neToken = {
        token: Math.random().toString(36).substring(2, 10), // Simulate token
        expiresAt: new Date(this.newTokenExpiration),
        createdAt: new Date(),
        revoked: false
      };
      this.n1neTokens.push(newToken);
      this.newTokenExpiration = '';
    }
  }

  onDeleteToken(token: N1neToken) {
    this.n1neTokens = this.n1neTokens.filter(t => t !== token);
  }

  // Password Reset
  newPassword: string = '';
  onPasswordReset() {
    // Implement password reset logic here
    this.newPassword = '';
    // Show notification if needed
  }

  addAlertLevel() {
    console.log('adding level', this.newTailLevel);
    const tailLevel: TailLevel = { name: this.newTailLevel, description: '' }
    if (this.newTailLevel && !this.tailLevels.includes(tailLevel)) {
      // todo call api
      console.log('pushing new tail level', tailLevel);
      this.tailLevels.push(tailLevel);
      this.newTailLevel = '';
    }
  }
  removeAlertLevel(level: string) {
    // todo call api
    this.tailLevels = this.tailLevels.filter(lvl => lvl.name !== level);
  }

  addAlertStatus() {
    console.log('adding status', this.newTailStatus);
    const tailStatus: TailStatus = { name: this.newTailStatus }
    if (this.newTailStatus && !this.tailStatuses.includes(tailStatus)) {
      // todo call api
      console.log('pushing new tail status', tailStatus);
      this.tailStatuses.push(tailStatus);
      this.newTailStatus = '';
    }
  }

  removeAlertStatus(status: string) {
    // todo call api
    this.tailStatuses = this.tailStatuses.filter(stat => stat.name !== status);
  }

  addAlertType() {
    console.log('adding type', this.newTailType);
    const tailType: TailType = { name: this.newTailType, description: '' }
    if (this.newTailType && !this.tailTypes.includes(tailType)) {
      // todo call api
      console.log('pushing new tail status', tailType);
      this.tailTypes.push(tailType);
      this.newTailType = '';
    }
  }

  removeAlertType(type: string) {
    // todo call api
    this.tailTypes = this.tailTypes.filter(tail => tail.name !== type);

    // todo implement this later
    // this.updateAlertTypeOptions();
    // this.preferredAlertTypes = this.preferredAlertTypes.filter(
    //   (t: string) => t !== type
    // );
  }

  // Preferred Alert Types
  alertTypeOptions: NzCheckBoxOptionInterface[] = [];
  preferredAlertTypes: string[] = [];

  updateAlertTypeOptions() {
    // this.alertTypeOptions = this.alertTypes.map(type => ({
    //   label: type,
    //   value: type
    // }));
  }

  onSavePreferredTypes() {
    // Save preferredAlertTypes to backend or local storage as needed
  }
}
