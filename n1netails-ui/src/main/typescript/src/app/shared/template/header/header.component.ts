import { Component, HostListener } from '@angular/core';
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
  public screenWidth: any;
  public isMobileView: boolean;

  constructor(    
    private authenticationService: AuthenticationService,
    private router: Router,
  ) {
      this.loggedInUser = this.authenticationService.getUserFromLocalCache();
      console.log('Logged in user:', this.loggedInUser);
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

  dashboard() {
    this.router.navigate(['/dashboard'])
  }

  accountSettings() {
    this.router.navigate(['/settings'])
  }

  editProfile() {
    this.router.navigate(['/edit-profile'])
  }

  logOut() {
    this.authenticationService.logOut();
    this.router.navigate(['/login']);
  }
}
