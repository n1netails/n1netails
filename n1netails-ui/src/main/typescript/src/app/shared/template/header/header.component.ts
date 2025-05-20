import { Component } from '@angular/core';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { AuthenticationService } from '../../../service/authentication.service';
import { Router } from '@angular/router';
import { User } from '../../../model/user';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-header',
  imports: [NzLayoutModule, NzIconModule, NzDropDownModule, NzAvatarModule,CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.less'
})
export class HeaderComponent {

  loggedInUser: User;

  constructor(    
    private authenticationService: AuthenticationService,
    private router: Router,
  ) {
      this.loggedInUser = this.authenticationService.getUserFromLocalCache();
      console.log('Logged in user:', this.loggedInUser);
  }

  editProfile() {
    this.router.navigate(['/edit-profile'])
  }

  logOut() {
    this.authenticationService.logOut();
    console.log('User logged out');
    this.router.navigate(['/login']);
  }
}
