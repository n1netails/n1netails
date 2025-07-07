import { Component, HostListener } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NzMenuModule } from 'ng-zorro-antd/menu';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sidenav',
  imports: [NzMenuModule,RouterModule, CommonModule],
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.less'
})
export class SidenavComponent {
  isCollapsed = true;
  public screenWidth: any;
  public isMobileView: boolean;

  constructor() {
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
}
