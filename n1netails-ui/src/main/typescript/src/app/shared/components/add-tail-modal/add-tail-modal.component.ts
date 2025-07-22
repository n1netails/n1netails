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
  ],
  templateUrl: './add-tail-modal.component.html',
  styleUrls: ['./add-tail-modal.component.less']
})
export class AddTailModalComponent {
  @Input() isVisible: boolean = true;

  // TODO set type for tail data
  tailData: any = {};

  metadata: { [key: string]: string } = {};
  metadataKeys: Array<{ id: number; key: string }> = [];
  metadataValues: Array<{ id: number; value: string }> = [];

  public tailUtilService = inject(TailUtilService);

  constructor(
    private modal: NzModalRef<AddTailModalComponent>,
    private alertService: AlertService
  ) { }

  handleOk(): void {
    for (let i = 0; i < this.metadataKeys.length; i ++) {
      this.metadata[this.metadataKeys[i].key] = this.metadataValues[i].value;
    }
    this.tailData.metadata = this.metadata;

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
}
