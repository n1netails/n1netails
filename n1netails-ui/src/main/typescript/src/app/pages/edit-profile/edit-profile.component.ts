import { Component } from '@angular/core';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { SidenavComponent } from '../../shared/template/sidenav/sidenav.component';
import { HeaderComponent } from '../../shared/template/header/header.component';

@Component({
  selector: 'app-edit-profile',
  imports: [NzLayoutModule,HeaderComponent,SidenavComponent],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.less'
})
export class EditProfileComponent {

}
