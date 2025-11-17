import { Component, EventEmitter, Input, OnInit, Output, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { NzModalModule } from 'ng-zorro-antd/modal';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzSelectModule } from 'ng-zorro-antd/select';
import { NzInputModule } from 'ng-zorro-antd/input';
import { TailStatus, TailStatusResponse, TailStatusService } from '../../../service/tail-status.service';
import { PageRequest } from '../../../model/interface/page.interface';
import { TailResponse } from '../../../model/tail.model';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';

@Component({
  selector: 'app-update-tail-status-modal',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    NzModalModule,
    NzFormModule,
    NzSelectModule,
    NzInputModule,
    NzAvatarModule,
  ],
  templateUrl: './update-tail-status-modal.component.html',
  styleUrls: ['./update-tail-status-modal.component.less']
})
export class UpdateTailStatusModalComponent implements OnInit {
  @Input() isVisible: boolean = false;
  @Input() selectedItem: TailResponse | null = null;
  @Output() onOk: EventEmitter<{ status: string, note: string }> = new EventEmitter();
  @Output() onCancel: EventEmitter<void> = new EventEmitter();

  selectedStatus: string | null = null;
  note: string = '';
  tailStatuses: TailStatusResponse[] = [];

  private tailStatusService = inject(TailStatusService);

  ngOnInit(): void {
    this.loadTailStatuses();
  }

  loadTailStatuses(): void {
    const pageRequest: PageRequest = { pageNumber: 0, pageSize: 100, sortDirection: "DESC", sortBy: 'name' };
    this.tailStatusService.getTailStatusList(pageRequest).subscribe(response => {
      this.tailStatuses = response.content;
    });
  }

  handleOk(): void {
    if (this.selectedStatus) {
      this.onOk.emit({ status: this.selectedStatus, note: this.note });
    }
  }

  handleCancel(): void {
    this.onCancel.emit();
  }
}
