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
import { Organization } from '../../model/organization';
import { NzSelectModule } from 'ng-zorro-antd/select';
import { PageRequest, PageResponse } from '../../model/interface/page.interface';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { PageUtilService } from '../../shared/page-util.service';

@Component({
  selector: 'app-settings',
  imports: [
    NzLayoutModule,
    HeaderComponent,
    SidenavComponent,
    NzCardModule,
    NzFormModule,
    FormsModule,
    NzTableModule,
    NzCheckboxModule,
    CommonModule,
    NzDividerModule,
    NzSelectModule,
    NzIconModule
  ],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.less'
})
export class SettingsComponent implements OnInit {

  user: User;
  organizations: Organization[];

  // N1ne Token Management
  tokens: N1neTokenResponse[] = [];
  newTokenRequestForm: { name?: string, expiresAt?: string, organizationId?: number } = {};
  isLoading: boolean = false;
  errorMessage: string = '';

  currentPage: number = 0;
  pageSize: number = 5;
  totalElements: number = 0;
  totalPages: number = 0;

  // Alert Levels, Statuses, Types
  tailLevels: TailLevelResponse[] = [];
  tailStatuses: TailStatusResponse[] = [];
  tailTypes: TailTypeResponse[] = [];

  newTailLevel: string = '';
  newTailStatus: string = '';
  newTailType: string = '';

  searchTailLevel: string = '';
  searchTailStatus: string = '';
  searchTailType: string = '';

  constructor(
    private authenticationService: AuthenticationService,
    private tailLevelService: TailLevelService,
    private tailStatusService: TailStatusService,
    private tailTypeService: TailTypeService,
    private n1neTokenService: N1neTokenService,
    private pageUtilService: PageUtilService
  ) {
    this.updateAlertTypeOptions();
    this.user = this.authenticationService.getUserFromLocalCache();
    this.organizations = this.user.organizations;
  }

  ngOnInit(): void {
    this.loadTokens(); // Load tokens on init
    this.listTailLevels();
    this.listTailStatus();
    this.listTailTypes();
  }

  private listTailLevels() {
    const pageRequest: PageRequest = this.pageUtilService.setDefaultPageRequest();
    this.tailLevelService.getTailLevels(pageRequest).subscribe((response: PageResponse<TailLevelResponse>) => {
      this.tailLevels = response.content;
    });
  }

    private listTailStatus() {
    const pageRequest: PageRequest = this.pageUtilService.setDefaultPageRequest();
    this.tailStatusService.getTailStatusList(pageRequest).subscribe((response: PageResponse<TailStatusResponse>) => {
      this.tailStatuses = response.content;
    });
  }

  private listTailTypes() {
    const pageRequest: PageRequest = this.pageUtilService.setDefaultPageRequest();
    this.tailTypeService.getTailTypes(pageRequest).subscribe((response: PageResponse<TailTypeResponse>) => {
      this.tailTypes = response.content;
      this.updateAlertTypeOptions();
    });
  }

  // N1ne Token Management Methods
  loadTokens(): void {
    this.isLoading = true;
    this.errorMessage = '';

    const pageRequest: PageRequest = {
      pageNumber: this.currentPage,
      pageSize: this.pageSize,
      sortDirection: "ASC",
      sortBy: "id"
    };

    this.n1neTokenService.getAllTokensByUserId(this.user.id, pageRequest).subscribe({
      next: (data: PageResponse<N1neTokenResponse>) => {
        this.tokens = data.content;
        this.isLoading = false;
        this.totalElements = data.totalElements;
        this.totalPages = data.totalPages;
      },
      error: (err) => {
        this.errorMessage = 'Failed to load tokens.';
        this.isLoading = false;
        console.error('Failed to load tokens', err);
      }
    });
  }

  // Helper to get current page display number (1-based)
  get displayedCurrentPage(): number {
    return this.currentPage + 1;
  }

  nextPage() {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadTokens();
    }
  }
  previousPage() {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadTokens();
    }
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
      organizationId: this.newTokenRequestForm.organizationId,
      expiresAt: ''
    };

    if (this.newTokenRequestForm.expiresAt) {
      // Ensure the date is sent in ISO 8601 format if it's a string from datetime-local
      request.expiresAt = new Date(this.newTokenRequestForm.expiresAt).toISOString();
    }

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

  addAlertLevel() {
    console.log('adding level', this.newTailLevel);
    const tailLevel: TailLevel = { name: this.newTailLevel, description: '', deletable: true }
    if (this.newTailLevel && !this.tailLevels.some(level => level.name === tailLevel.name)) {
      this.tailLevelService.createTailLevel(tailLevel).subscribe(response => {
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
    const tailStatus: TailStatus = { name: this.newTailStatus, deletable: true }
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
    const tailType: TailType = { name: this.newTailType, description: '', deletable: true }
    if (this.newTailType && !this.tailTypes.some(type => type.name === tailType.name)) {
      this.tailTypeService.createTailType(tailType).subscribe(response => {
        console.log('TailType created:', response);
        this.tailTypes.push(response);
        this.newTailType = '';
        this.updateAlertTypeOptions(); // Call after adding a new type
      });
    }
  }

  removeAlertType(typeName: string) {
    const typeToRemove = this.tailTypes.find(type => type.name === typeName);
    if (typeToRemove && typeToRemove.id) {
      this.tailTypeService.deleteTailType(typeToRemove.id).subscribe(() => {
        console.log('TailType deleted:', typeToRemove.id);
        this.tailTypes = this.tailTypes.filter(type => type.id !== typeToRemove.id);
        this.updateAlertTypeOptions(); // Call after removing a type
        this.preferredAlertTypes = this.preferredAlertTypes.filter(
          (t: string) => t !== typeName
        );
      });
    } else {
      console.warn('TailType not found or id missing for type:', typeName);
    }
  }

  // Preferred Alert Types
  alertTypeOptions: NzCheckBoxOptionInterface[] = [];
  preferredAlertTypes: string[] = [];

  updateAlertTypeOptions() {
    // TODO CONSIDER REMOVING THIS
    // this.tailTypeService.getTailTypes().subscribe((response: TailTypeResponse[]) => {
    //   this.alertTypeOptions = response.map(type => ({
    //     label: type.name,
    //     value: type.name,
    //     // `checked` property can be managed based on `preferredAlertTypes`
    //     // For now, we just populate the options.
    //     // checked: this.preferredAlertTypes.includes(type.name)
    //   }));
    //   console.log('Updated alertTypeOptions:', this.alertTypeOptions);
    // });
  }

  onSavePreferredTypes() {
    console.log('Saving preferred tail types:', this.preferredAlertTypes);
    // TODO: Implement backend call to save preferredAlertTypes
    // For example, this might involve a service call like:
    // this.userPreferenceService.savePreferredTailTypes(this.preferredAlertTypes).subscribe(...);
    //
    // The following tasks would also need to be addressed in a full implementation:
    // - Extend upon the /api/tail-type: Create a process to save a user's preferred tail types.
    // - Modify /n1netails-liquibase and /n1netails-api services in the root directory to include and save the user's preferred tail types.
    // - If tail types do not exist in the tail types list, they should be removed from the user's preferred tail types as well.
    //   This does not need to be corrected until the user checks to confirm and view their preferred tail type list.
  }

  searchLevels() {
    const pageRequest: PageRequest = this.pageUtilService.setDefaultPageRequestWithSearch(this.searchTailLevel);
    this.tailLevelService.getTailLevels(pageRequest).subscribe((response: PageResponse<TailLevelResponse>) => {
      this.tailLevels = response.content;
    });
  }

  searchStatuses() {
    const pageRequest: PageRequest = this.pageUtilService.setDefaultPageRequestWithSearch(this.searchTailStatus);
    this.tailStatusService.getTailStatusList(pageRequest).subscribe((response: PageResponse<TailStatusResponse>) => {
      this.tailStatuses = response.content;
    });
  }

  searchType() {
    const pageRequest: PageRequest = this.pageUtilService.setDefaultPageRequestWithSearch(this.searchTailType);
    this.tailTypeService.getTailTypes(pageRequest).subscribe((response: PageResponse<TailTypeResponse>) => {
      this.tailTypes = response.content;
    });
  }
}
