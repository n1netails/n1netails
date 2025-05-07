import { Component } from '@angular/core';
import { NzMenuModule } from 'ng-zorro-antd/menu';

@Component({
  selector: 'app-sidenav',
  imports: [NzMenuModule],
  templateUrl: './sidenav.component.html',
  styleUrl: './sidenav.component.less'
})
export class SidenavComponent {
  isCollapsed = true;
}
