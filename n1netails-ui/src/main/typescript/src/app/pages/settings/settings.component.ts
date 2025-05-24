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
import { TailLevel, TailLevelResponse, TailLevelService } from '../../service/tail-level.service';
import { TailStatus, TailStatusResponse, TailStatusService } from '../../service/tail-status.service';
import { TailType, TailTypeResponse, TailTypeService } from '../../service/tail-type.service';
import { User } from '../../model/user';
import { AuthenticationService } from '../../service/authentication.service';
import { N1neTokenService, N1neTokenResponse, CreateTokenRequest } from '../../service/n1ne-token.service';
import { NzDividerModule } from 'ng-zorro-antd/divider';

@Component({
  selector: 'app-settings',
  imports: [NzLayoutModule,HeaderComponent,SidenavComponent,NzCardModule,NzFormModule,FormsModule,NzTableModule,NzCheckboxModule,CommonModule,NzDividerModule],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.less'
})
export class SettingsComponent implements OnInit {

  user: User;

  // N1ne Token Management
  tokens: N1neTokenResponse[] = [];
  newTokenRequestForm: { name?: string, expiresAt?: string } = {};
  isLoading: boolean = false;
  errorMessage: string = '';

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
    private n1neTokenService: N1neTokenService,
  ) {
    this.updateAlertTypeOptions();
    this.user = this.authenticationService.getUserFromLocalCache();
  }

  ngOnInit(): void {
    this.loadTokens(); // Load tokens on init
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

  // N1ne Token Management Methods
  loadTokens(): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.n1neTokenService.getAllTokens().subscribe({
      next: (data) => {
        console.log('TOKEN DATA:', data);
        this.tokens = data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load tokens.';
        this.isLoading = false;
        console.error('Failed to load tokens', err);
      }
    });
  }

  createToken(): void {
    if (!this.newTokenRequestForm.name) {
      this.errorMessage = 'Token name is required.';
      return;
    }
    // this.user is from AuthenticationService, and User model has id: number
    if (!this.user || typeof this.user.id === 'undefined') {
      this.errorMessage = 'User information is not available. Cannot create token.';
      console.error('User ID is not available for token creation.');
      return;
    }

    this.isLoading = true;
    this.errorMessage = '';

    const request: CreateTokenRequest = {
      name: this.newTokenRequestForm.name,
      userId: this.user.id,
      organizationId: 0,
      expiresAt: ''
    };

    if (this.newTokenRequestForm.expiresAt) {
      // Ensure the date is sent in ISO 8601 format if it's a string from datetime-local
      request.expiresAt = new Date(this.newTokenRequestForm.expiresAt).toISOString();
    }

    console.log('Create token request:', request);
    this.n1neTokenService.createToken(request).subscribe({
      next: () => {
        this.newTokenRequestForm = {}; // Reset form
        this.loadTokens(); // Reloads tokens and sets isLoading = false
      },
      error: (err) => {
        this.errorMessage = 'Failed to create token.';
        this.isLoading = false;
        console.error('Failed to create token', err);
      }
    });
  }

  revokeToken(token: N1neTokenResponse): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.n1neTokenService.revokeToken(token.id).subscribe({
      next: () => {
        this.loadTokens(); // Reloads tokens, which will update status and set isLoading = false
      },
      error: (err) => {
        this.errorMessage = 'Failed to revoke token.';
        this.isLoading = false;
        console.error(`Failed to revoke token ${token.id}`, err);
      }
    });
  }

  enableToken(token: N1neTokenResponse): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.n1neTokenService.enableToken(token.id).subscribe({
      next: () => {
        this.loadTokens(); // Reloads tokens
      },
      error: (err) => {
        this.errorMessage = 'Failed to enable token.';
        this.isLoading = false;
        console.error(`Failed to enable token ${token.id}`, err);
      }
    });
  }

  deleteToken(tokenId: number): void {
    this.isLoading = true;
    this.errorMessage = '';
    this.n1neTokenService.deleteToken(tokenId).subscribe({
      next: () => {
        this.loadTokens(); // Reloads tokens
      },
      error: (err) => {
        this.errorMessage = 'Failed to delete token.';
        this.isLoading = false;
        console.error(`Failed to delete token ${tokenId}`, err);
      }
    });
  }

  showTokenIndex: number | null = null;

  toggleShowToken(index: number): void {
    this.showTokenIndex = this.showTokenIndex === index ? null : index;
  }

  copyToken(tokenValue: string): void {
    navigator.clipboard.writeText(tokenValue).then(() => {
      // Optionally show a notification/toast here
      console.log('copied token');
    });
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
        this.tailLevels.push(response);
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
        this.tailStatuses.push(response);
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
    const tailType: TailType = { name: this.newTailType, description: '' }
    if (this.newTailType && !this.tailTypes.some(type => type.name === tailType.name)) {
      this.tailTypeService.createTailType(tailType).subscribe(response => {
        console.log('TailType created:', response);
        this.tailTypes.push(response);
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

    // todo implement this later
    // this.alertTypeOptions = this.alertTypes.map(type => ({
    //   label: type,
    //   value: type
    // }));
  }

  onSavePreferredTypes() {
    // todo implement this later
    // Save preferredAlertTypes to backend or local storage as needed
  }
}
