import { Component, OnDestroy, OnInit } from '@angular/core';
import { NzLayoutModule } from 'ng-zorro-antd/layout';
import { SidenavComponent } from '../../shared/template/sidenav/sidenav.component';
import { HeaderComponent } from '../../shared/template/header/header.component';
import { AuthenticationService } from '../../service/authentication.service';
import { UserService } from '../../service/user.service';
import { User } from '../../model/user';
import { Subscription } from 'rxjs';
import { NzCardModule } from 'ng-zorro-antd/card';
import { NzGridModule } from 'ng-zorro-antd/grid';
import { NzFormModule } from 'ng-zorro-antd/form';
import { FormsModule, NgForm } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { NzAvatarModule } from 'ng-zorro-antd/avatar';

@Component({
  selector: 'app-edit-profile',
  imports: [NzLayoutModule,NzGridModule,NzCardModule,NzAvatarModule,HeaderComponent,SidenavComponent,NzFormModule,FormsModule],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.less'
})
export class EditProfileComponent implements OnInit, OnDestroy {

  user: User = new User();
  usernameInput: string = "";
  firstNameInput: string = "";
  lastNameInput: string = "";

  subscriptions: Subscription[] = [];

  constructor(
    private notification: NzNotificationService,
    private authenticationService: AuthenticationService,
    private userService: UserService,
  ) {}

  ngOnInit(): void {
    this.user = this.authenticationService.getUserFromLocalCache();
    this.usernameInput = this.user.username;
    this.firstNameInput = this.user.firstName;
    this.lastNameInput = this.user.lastName;
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  onSaveProfile(form: NgForm) {
    const editUser = this.user;
    editUser.username = form.value.username;
    editUser.firstName = form.value.firstName;
    editUser.lastName = form.value.lastName;
    const sub: Subscription = this.userService
      .editUser(editUser).subscribe({
        next: (response: User) => {
          this.authenticationService.addUserToLocalCache(response);
          this.user = response;
          this.presentToast('Success','Updated profile successfully');
        },
        error: (errorResponse: HttpErrorResponse) => {
          console.error(errorResponse);
          this.presentToast('Error','Error updating profile');
        }
      });
    this.subscriptions.push(sub);
  }

  private async presentToast(type: string, message: string) {
    switch (type) {
      case 'Error':
        this.notification.error(type, message, {
          nzPlacement: 'topRight',
          nzDuration: 10000
        });
        break;
      case 'Success':
        this.notification.success(type, message, {
          nzPlacement: 'topRight',
          nzDuration: 10000
        });
        break;
    }
  }
}
