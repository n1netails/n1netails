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

interface AlertToken {
  value: string;
  expiration: Date;
}

@Component({
  selector: 'app-settings',
  imports: [NzLayoutModule,HeaderComponent,SidenavComponent,NzCardModule,NzFormModule,FormsModule,NzTableModule,NzCheckboxModule,CommonModule],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.less'
})
export class SettingsComponent {

  // Alert Token Manager
  tokens: AlertToken[] = [
    { value: 'abc123', expiration: new Date('2025-12-31') },
    { value: 'def456', expiration: new Date('2025-11-30') }
  ];

  newTokenExpiration: string = '';

  onCreateToken() {
    if (this.newTokenExpiration) {
      const newToken: AlertToken = {
        value: Math.random().toString(36).substring(2, 10), // Simulate token
        expiration: new Date(this.newTokenExpiration)
      };
      this.tokens.push(newToken);
      this.newTokenExpiration = '';
    }
  }

  onDeleteToken(token: AlertToken) {
    this.tokens = this.tokens.filter(t => t !== token);
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

  constructor() {
    this.updateAlertTypeOptions();
  }

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
