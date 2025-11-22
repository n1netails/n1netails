import { Component, Input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NzModalModule, NzModalRef } from 'ng-zorro-antd/modal';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { TailUtilService } from '../../util/tail-util.service';
import { AlertService } from '../../../service/alert.service';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzSelectModule } from 'ng-zorro-antd/select';
import { TailLevelService } from '../../../service/tail-level.service';
import { TailTypeService } from '../../../service/tail-type.service';
import { PageRequest } from '../../../model/interface/page.interface';
import { PageUtilService } from '../../util/page-util.service';
import { User } from '../../../model/user';
import { Organization } from '../../../model/organization';
import { AuthenticationService } from '../../../service/authentication.service';
import { Router } from '@angular/router';
import { TailAlert } from '../../../model/interface/tail-alert.interface';
import { NzMessageService } from 'ng-zorro-antd/message';
import { N1neTokenService } from '../../../service/n1ne-token.service';
import { N1neTokenResponse } from '../../../service/n1ne-token.service';
import { PageResponse } from '../../../model/interface/page.interface';

@Component({
  selector: 'app-add-tail-modal',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NzModalModule,
    NzAvatarModule,
    NzTagModule,
    NzInputModule,
    NzButtonModule,
    NzIconModule,
    NzSelectModule,
  ],
  templateUrl: './add-tail-modal.component.html',
  styleUrls: ['./add-tail-modal.component.less']
})
export class AddTailModalComponent {
  @Input() isVisible: boolean = true;

  user: User;
  organizations: Organization[];
  organizationId: number = 0;
  tokens: N1neTokenResponse[] = [];
  tokenId: number = -1;

  selectedLevel: string = '';
  selectedType: string = '';

  tailLevels: string[] = [];
  tailTypes: string[] = [];

  // tail alert data
  tailAlert: TailAlert = {};
  metadata: { [key: string]: string } = {};
  metadataKeys: Array<{ id: number; key: string }> = [];
  metadataValues: Array<{ id: number; value: string }> = [];

  public tailUtilService = inject(TailUtilService);

  constructor(
    private authenticationService: AuthenticationService,
    private modal: NzModalRef<AddTailModalComponent>,
    private alertService: AlertService,
    private n1neTokenService: N1neTokenService,
    private tailLevelService: TailLevelService,
    private tailTypeService: TailTypeService,
    private pageUtilService: PageUtilService,
    private msg: NzMessageService,
    private router: Router,
  ) { 
    this.user = this.authenticationService.getUserFromLocalCache();
    this.organizations = this.user.organizations;
    this.loadUserTokens();
  }

  loadUserTokens() {
    const pageRequest: PageRequest = {
      pageNumber: 0,
      pageSize: 50,
      sortDirection: "DESC",
      sortBy: "id"
    };

    this.n1neTokenService.getAllTokensByUserId(this.user.id, pageRequest).subscribe({
      next: (data: PageResponse<N1neTokenResponse>) => {
        this.tokens = data.content;
      },
      error: (err) => this.msg.error(`Failed to load tokens: ${err.message || err}`)
    });
  }

  handleOk(): void {
    if (!this.isValidTailAlert()) return;

    this.tailAlert.metadata = this.buildMetadata();

    if (this.tokenId !== -1) {
      this.alertService.createManualTailWithToken(this.organizationId, this.user.id, this.tailAlert, this.tokenId)
        .subscribe({
          next: () => {
            this.modal.close(this.tailAlert);
            this.navigateToDashboard();
          },
          error: (err) => this.msg.error(`Failed to create tail alert: ${err.message || err}`)
        });
    } else {
      this.alertService.createManualTail(this.organizationId, this.user.id, this.tailAlert)
        .subscribe({
          next: () => {
            this.modal.close(this.tailAlert);
            this.navigateToDashboard();
          },
          error: (err) => this.msg.error(`Failed to create tail alert: ${err.message || err}`)
        });
    }
  }

  private isValidTailAlert(): boolean {
    if (!this.tailAlert.title?.trim()) {
      this.msg.error('The tail title cannot be empty.');
      return false;
    }
    if (!this.tailAlert.description?.trim()) {
      this.msg.error('The tail description cannot be empty.');
      return false;
    }
    return true;
  }

  private buildMetadata(): Record<string, string> {
    return this.metadataKeys.reduce((acc, { key }, index) => {
      acc[key] = this.metadataValues[index].value;
      return acc;
    }, {} as Record<string, string>);
  }

  private navigateToDashboard(): void {
    if (this.router.url === '/dashboard') {
      window.location.reload();
    } else {
      this.router.navigate(['/dashboard']);
    }
  }

  handleCancel(): void {
    this.modal.destroy();
  }

  addField(e?: MouseEvent): void {
    e?.preventDefault();
    const id = this.metadataKeys.length > 0 ? this.metadataKeys[this.metadataKeys.length - 1].id + 1 : 0;
    const mKey = { id, key: '' };
    const mValue = { id, value: '' };
    this.metadataKeys.push(mKey);
    this.metadataValues.push(mValue);
  }

  removeField(i: { id: number; key: string }, e: MouseEvent): void {
    e.preventDefault();
    if (this.metadataKeys.length > 0) {
      const index = this.metadataKeys.indexOf(i);
      this.metadataKeys.splice(index, 1);
      this.metadataValues.splice(index, 1);
    }
  }

  onTokenSearch(term: string): void {
    const pageRequest: PageRequest = this.pageUtilService.setDefaultPageRequestWithSearch(term);
    this.n1neTokenService.getAllTokensByUserId(this.user.id, pageRequest).subscribe(result => {
      this.tokens = [];
      result.content.forEach(token => this.tokens.push(token));
    })
  }

  onTypeSearch(term: string): void {
    const pageRequest: PageRequest = this.pageUtilService.setDefaultPageRequestWithSearch(term);
    this.tailTypeService.getTailTypes(pageRequest).subscribe(result => {
      this.tailTypes = [];
      result.content.forEach(type => this.tailTypes.push(type.name));
    });
  } 

  onLevelSearch(term: string): void {
    const pageRequest: PageRequest = this.pageUtilService.setDefaultPageRequestWithSearch(term);
    this.tailLevelService.getTailLevels(pageRequest).subscribe(result => {
      this.tailLevels = [];
      result.content.forEach(level => this.tailLevels.push(level.name));
    });
  } 
}
