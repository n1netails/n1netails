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
import { TailLevel, TailLevelResponse, TailLevelService } from '../../service/tail-level.service';
import { TailStatus, TailStatusResponse, TailStatusService } from '../../service/tail-status.service';
import { TailType, TailTypeResponse, TailTypeService } from '../../service/tail-type.service';
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
  tailLevels: TailLevelResponse[] = [];
  tailStatuses: TailStatusResponse[] = [];
  tailTypes: TailTypeResponse[] = [];

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
    this.tailLevelService.getTailLevels().subscribe((response: TailLevelResponse[]) => {
      this.tailLevels = response;
      console.log('tail levels:', this.tailLevels);
    });
    this.tailStatusService.getTailStatusList().subscribe((response: TailStatusResponse[]) => {
      this.tailStatuses = response;
      console.log('tail statuses:', this.tailStatuses);
    });
    this.tailTypeService.getTailTypes().subscribe((response: TailTypeResponse[]) => {
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
    if (this.newTailLevel && !this.tailLevels.some(level => level.name === tailLevel.name)) {
      this.tailLevelService.createTailLevel(tailLevel).subscribe(response => {
        console.log('TailLevel created:', response);
        this.tailLevels.push(response); // Assuming response is the created TailLevel
        this.newTailLevel = '';
      });
    }
  }
  removeAlertLevel(levelName: string) {
    const levelToRemove = this.tailLevels.find(lvl => lvl.name === levelName);
    if (levelToRemove && levelToRemove.id) {
      this.tailLevelService.deleteTailLevel(levelToRemove.id).subscribe(() => {
        console.log('TailLevel deleted:', levelToRemove.id);
        this.tailLevels = this.tailLevels.filter(lvl => lvl.id !== levelToRemove.id);
      });
    } else {
      console.warn('TailLevel not found or id missing for level:', levelName);
    }
  }

  addAlertStatus() {
    console.log('adding status', this.newTailStatus);
    const tailStatus: TailStatus = { name: this.newTailStatus }
    if (this.newTailStatus && !this.tailStatuses.some(status => status.name === tailStatus.name)) {
      this.tailStatusService.createTailStatus(tailStatus).subscribe(response => {
        console.log('TailStatus created:', response);
        this.tailStatuses.push(response); // Assuming response is the created TailStatus
        this.newTailStatus = '';
      });
    }
  }

  removeAlertStatus(statusName: string) {
    const statusToRemove = this.tailStatuses.find(stat => stat.name === statusName);
    if (statusToRemove && statusToRemove.id) {
      this.tailStatusService.deleteTailStatus(statusToRemove.id).subscribe(() => {
        console.log('TailStatus deleted:', statusToRemove.id);
        this.tailStatuses = this.tailStatuses.filter(stat => stat.id !== statusToRemove.id);
      });
    } else {
      console.warn('TailStatus not found or id missing for status:', statusName);
    }
  }

  addAlertType() {
    console.log('adding type', this.newTailType);
    const tailType: TailType = { name: this.newTailType, description: '' } // Assuming description is empty for new types or handled by backend
    if (this.newTailType && !this.tailTypes.some(type => type.name === tailType.name)) {
      this.tailTypeService.createTailType(tailType).subscribe(response => {
        console.log('TailType created:', response);
        this.tailTypes.push(response); // Assuming response is the created TailType
        this.newTailType = '';
      });
    }
  }

  removeAlertType(typeName: string) {
    const typeToRemove = this.tailTypes.find(type => type.name === typeName);
    if (typeToRemove && typeToRemove.id) {
      this.tailTypeService.deleteTailType(typeToRemove.id).subscribe(() => {
        console.log('TailType deleted:', typeToRemove.id);
        this.tailTypes = this.tailTypes.filter(type => type.id !== typeToRemove.id);
        // todo implement this later
        // this.updateAlertTypeOptions();
        // this.preferredAlertTypes = this.preferredAlertTypes.filter(
        //   (t: string) => t !== typeName
        // );
      });
    } else {
      console.warn('TailType not found or id missing for type:', typeName);
    }
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
