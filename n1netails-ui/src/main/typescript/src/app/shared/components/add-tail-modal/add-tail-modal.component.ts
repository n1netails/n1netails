import { Component, Input, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NzModalModule, NzModalRef } from 'ng-zorro-antd/modal';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { TailUtilService } from '../../../service/tail-util.service';
import { AlertService } from '../../../service/alert.service';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzSelectModule } from 'ng-zorro-antd/select';
import { TailLevelService } from '../../../service/tail-level.service';
import { TailTypeService } from '../../../service/tail-type.service';
import { PageRequest } from '../../../model/interface/page.interface';
import { PageUtilService } from '../../page-util.service';
import { User } from '../../../model/user';
import { Organization } from '../../../model/organization';
import { AuthenticationService } from '../../../service/authentication.service';
import { Router } from '@angular/router';

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

  selectedLevel: string = '';
  selectedType: string = '';

  tailLevels: string[] = [];
  tailTypes: string[] = [];

  // TODO set type for tail data
  tailData: any = {};

  metadata: { [key: string]: string } = {};
  metadataKeys: Array<{ id: number; key: string }> = [];
  metadataValues: Array<{ id: number; value: string }> = [];

  public tailUtilService = inject(TailUtilService);

  constructor(
    private authenticationService: AuthenticationService,
    private modal: NzModalRef<AddTailModalComponent>,
    private alertService: AlertService,
    private tailLevelService: TailLevelService,
    private tailTypeService: TailTypeService,
    private pageUtilService: PageUtilService,
    private router: Router,
  ) { 
    this.user = this.authenticationService.getUserFromLocalCache();
    this.organizations = this.user.organizations;
  }

  handleOk(): void {
    // TODO PERFORM TAIL DATA VALIDATION BEFORE SENDING REQUEST
    for (let i = 0; i < this.metadataKeys.length; i ++) {
      this.metadata[this.metadataKeys[i].key] = this.metadataValues[i].value;
    }
    this.tailData.metadata = this.metadata;

    this.alertService.createManualTail(this.organizationId, this.user.id, this.tailData).subscribe(() => {
      this.modal.close(this.tailData);
      if (this.router.url === '/dashboard') window.location.reload();
      else this.router.navigate(['/dashboard']);
    });
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

  // TODO MOVE TO COMMON UTIL
  onTypeSearch(term: string): void {
    const pageRequest: PageRequest = this.pageUtilService.setDefaultPageRequestWithSearch(term);
    this.tailTypeService.getTailTypes(pageRequest).subscribe(result => {
      this.tailTypes = [];
      result.content.forEach(type => this.tailTypes.push(type.name));
    });
  } 

  // TODO MOVE TO COMMON UTIL
  onLevelSearch(term: string): void {
    const pageRequest: PageRequest = this.pageUtilService.setDefaultPageRequestWithSearch(term);
    this.tailLevelService.getTailLevels(pageRequest).subscribe(result => {
      this.tailLevels = [];
      result.content.forEach(level => this.tailLevels.push(level.name));
    });
  } 
}
