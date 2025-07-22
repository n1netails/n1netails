import { Component, HostListener } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NzMenuModule } from 'ng-zorro-antd/menu';
import { CommonModule } from '@angular/common';
import { NzModalService } from 'ng-zorro-antd/modal';
import { AddTailModalComponent } from '../../components/add-tail-modal/add-tail-modal.component';
import { NzButtonModule } from 'ng-zorro-antd/button';

@Component({
  selector: 'app-sidenav',
  imports: [NzMenuModule,RouterModule, CommonModule, NzButtonModule],
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.less'
})
export class SidenavComponent {
  isCollapsed = true;
  public screenWidth: any;
  public isMobileView: boolean;

  constructor(
    private modalService: NzModalService
  ) {
    this.screenWidth = window.innerWidth;
    this.isMobileView = this.screenWidth < 768;
  }

  @HostListener('window:resize', ['$event'])
  onResize(event: any) {
    this.screenWidth = window.innerWidth;
    this.isMobileView = this.screenWidth < 768;
  }

  isMobile(): boolean {
    return this.isMobileView;
  }

  addTailAlert(): void {
    this.modalService.create({
      nzTitle: 'Add New Tail',
      nzContent: AddTailModalComponent,
      nzFooter: null,
    });
  }
}
