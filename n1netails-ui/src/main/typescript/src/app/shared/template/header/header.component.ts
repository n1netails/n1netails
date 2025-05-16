import { Component } from '@angular/core';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { AuthenticationService } from '../../../service/authentication.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  imports: [NzLayoutModule, NzIconModule, NzDropDownModule, NzAvatarModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.less'
})
export class HeaderComponent {

  constructor(    
    private authenticationService: AuthenticationService,
    private router: Router) {}

  logOut() {
    this.authenticationService.logOut();
    console.log('User logged out');
    this.router.navigate(['/login']);
  }
}
