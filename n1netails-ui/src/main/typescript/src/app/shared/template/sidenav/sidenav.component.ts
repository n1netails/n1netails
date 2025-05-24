import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { NzMenuModule } from 'ng-zorro-antd/menu';
import { AuthenticationService } from '../../../service/authentication.service'; // Added

@Component({
  selector: 'app-sidenav',
  imports: [NzMenuModule,RouterModule],
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.less'
})
export class SidenavComponent {
  isCollapsed = true;

  constructor(public authService: AuthenticationService) {} // Added and made public
}
