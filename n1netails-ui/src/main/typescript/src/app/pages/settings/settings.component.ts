import { Component } from '@angular/core';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { HeaderComponent } from '../../shared/template/header/header.component';
import { SidenavComponent } from '../../shared/template/sidenav/sidenav.component';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzFormModule } from 'ng-zorro-antd/form';
import { FormsModule } from '@angular/forms';
import { NzTableModule } from 'ng-zorro-antd/table';
import { NzCheckboxModule, NzCheckBoxOptionInterface } from 'ng-zorro-antd/checkbox';
import { CommonModule } from '@angular/common';

interface N1neToken {
  id?: number | undefined;
  token: string | undefined;
  createdAt: Date | undefined;
  expiresAt: Date | undefined;
  revoked: boolean | undefined;
}

@Component({
  selector: 'app-settings',
  imports: [NzLayoutModule,HeaderComponent,SidenavComponent,NzCardModule,NzFormModule,FormsModule,NzTableModule,NzCheckboxModule,CommonModule],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.less'
})
export class SettingsComponent {

  constructor() {
    this.updateAlertTypeOptions();
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

  // Alert Levels, Statuses, Types
  alertLevels: string[] = ['Critical', 'Warning', 'Info'];
  alertStatuses: string[] = ['Active', 'Resolved', 'Acknowledged'];
  alertTypes: string[] = ['Email', 'SMS', 'Push'];

  newAlertLevel: string = '';
  newAlertStatus: string = '';
  newAlertType: string = '';

  addAlertLevel() {
    if (this.newAlertLevel && !this.alertLevels.includes(this.newAlertLevel)) {
      this.alertLevels.push(this.newAlertLevel);
      this.newAlertLevel = '';
    }
  }
  removeAlertLevel(level: string) {
    this.alertLevels = this.alertLevels.filter(l => l !== level);
  }

  addAlertStatus() {
    if (this.newAlertStatus && !this.alertStatuses.includes(this.newAlertStatus)) {
      this.alertStatuses.push(this.newAlertStatus);
      this.newAlertStatus = '';
    }
  }
  removeAlertStatus(status: string) {
    this.alertStatuses = this.alertStatuses.filter(s => s !== status);
  }

  addAlertType() {
    if (this.newAlertType && !this.alertTypes.includes(this.newAlertType)) {
      this.alertTypes.push(this.newAlertType);
      this.updateAlertTypeOptions();
      this.newAlertType = '';
    }
  }
  removeAlertType(type: string) {
    this.alertTypes = this.alertTypes.filter(t => t !== type);
    this.updateAlertTypeOptions();
    this.preferredAlertTypes = this.preferredAlertTypes.filter(
      (t: string) => t !== type
    );
  }

  // Preferred Alert Types
  alertTypeOptions: NzCheckBoxOptionInterface[] = [];
  preferredAlertTypes: string[] = [];

  updateAlertTypeOptions() {
    this.alertTypeOptions = this.alertTypes.map(type => ({
      label: type,
      value: type
    }));
  }

  onSavePreferredTypes() {
    // Save preferredAlertTypes to backend or local storage as needed
  }
}
