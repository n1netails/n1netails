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
    NzButtonModule
  ],
  templateUrl: './add-tail-modal.component.html',
  styleUrls: ['./add-tail-modal.component.less']
})
export class AddTailModalComponent {
  @Input() isVisible: boolean = true;

  // TODO set type for tail data
  tailData: any = {};

  public tailUtilService = inject(TailUtilService);

  constructor(
    private modal: NzModalRef,
    private alertService: AlertService
  ) { }

  handleOk(): void {
    // Hardcoded for now, will be replaced with a proper token management system
    // TODO get list of n1ne tokens that user owns
    const token = 'c8f36742-a0fe-4915-8087-d0d7a5aa8424';
    this.alertService.createTail(token, this.tailData).subscribe(() => {
      this.modal.close(this.tailData);
      window.location.reload();
    });
  }

  handleCancel(): void {
    this.modal.destroy();
  }
}
