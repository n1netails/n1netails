import { Component, HostListener } from '@angular/core';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';
import { NzDropDownModule } from 'ng-zorro-antd/dropdown';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { AuthenticationService } from '../../../service/authentication.service';
import { Router, NavigationEnd  } from '@angular/router';
import { User } from '../../../model/user';
import { CommonModule } from '@angular/common';
import { NzTooltipModule } from 'ng-zorro-antd/tooltip';
import { filter } from 'rxjs/operators';
import { UiConfigService } from '../../util/ui-config.service';

@Component({
  selector: 'app-header',
  imports: [NzLayoutModule, NzIconModule, NzDropDownModule, NzTooltipModule, NzAvatarModule,CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.less'
})
export class HeaderComponent {

  n1netailsDocUrl: string = 'https://google.com';
  loggedInUser: User;
  isInSettings: boolean;
  public screenWidth: any;
  public isMobileView: boolean;

  constructor(    
    private uiConfigService: UiConfigService,
    private authenticationService: AuthenticationService,
    private router: Router,
  ) {
    this.n1netailsDocUrl = this.uiConfigService.getDocUrl();
    this.loggedInUser = this.authenticationService.getUserFromLocalCache();
    console.log('Logged in user:', this.loggedInUser);
    this.screenWidth = window.innerWidth;
    this.isMobileView = this.screenWidth < 768;

    // initial check
    this.isInSettings = this.router.url.startsWith('/setting');
    // update on navigation
    this.router.events.pipe(
      filter(e => e instanceof NavigationEnd)
    ).subscribe((e: NavigationEnd) => {
      this.isInSettings = e.urlAfterRedirects.startsWith('/setting');
    });
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
    this.router.navigate(['/dashboard']);
  }

  accountSettings() {
    this.router.navigate(['/settings']);
  }

  editProfile() {
    this.router.navigate(['/edit-profile']);
  }

  logOut() {
    this.authenticationService.logOut();
    this.router.navigate(['/login']);
  }
}
