import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { NzModalModule } from 'ng-zorro-antd/modal';
import { N1neTokenResponse } from '../../../service/n1ne-token.service';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzMessageService } from 'ng-zorro-antd/message';

@Component({
  selector: 'app-n1-token-modal',
  imports: [CommonModule, NzModalModule, NzAvatarModule, NzButtonModule, NzIconModule],
  templateUrl: './n1-token-modal.component.html',
  styleUrl: './n1-token-modal.component.less',
})
export class N1TokenModalComponent {
  @Input() isVisible: boolean = false;
  @Input() n1Token: N1neTokenResponse = {
    id: 0,
    userId: 0,
    organizationId: 0,
    n1Token: '',
    name: '',
    lastUsedAt: '',
    revoked: false,
  };

  @Output() onOk = new EventEmitter<void>();
  @Output() onCancel = new EventEmitter<void>();

  constructor(private msg: NzMessageService) {}

  copyToken(tokenValue: string): void {
    navigator.clipboard.writeText(tokenValue).then(() => {
      console.log('copied token');
      this.msg.success('Token copied successfully.');
    });
  }

  handleOk(): void {
    this.onOk.emit();
  }

  handleCancel(): void {
    this.onCancel.emit();
  }
}
