import { Component, Input, Output, EventEmitter, inject } from '@angular/core'; // Added inject
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NzModalModule } from 'ng-zorro-antd/modal';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { NzTagModule } from 'ng-zorro-antd/tag';
import { NzInputModule } from 'ng-zorro-antd/input';
import { NzButtonModule } from 'ng-zorro-antd/button';
import { TailUtilService } from '../../../service/tail-util.service';

@Component({
  selector: 'app-resolve-tail-modal',
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
  templateUrl: './resolve-tail-modal.component.html',
  styleUrls: ['./resolve-tail-modal.component.less']
})
export class ResolveTailModalComponent {
  @Input() isVisible: boolean = false;
  @Input() selectedItem: any = null; // Consider creating a specific interface later

  @Output() onOk = new EventEmitter<string>();
  @Output() onCancel = new EventEmitter<void>();

  resolveNote: string = '';

  // Inject TailUtilService
  public tailUtilService = inject(TailUtilService);

  constructor() { }

  handleOk(): void {
    this.onOk.emit(this.resolveNote);
  }

  handleCancel(): void {
    this.onCancel.emit();
  }
}
