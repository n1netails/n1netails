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
import { NzMessageService, NzMessageModule } from 'ng-zorro-antd/message'; // Added NzMessageModule

@Component({
  selector: 'app-edit-profile',
  imports: [NzLayoutModule,NzGridModule,NzCardModule,HeaderComponent,SidenavComponent,NzFormModule,FormsModule, NzMessageModule], // Added NzMessageModule
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.less'
})
export class EditProfileComponent implements OnInit, OnDestroy {

  user: User = new User();
  usernameInput: string = "";
  firstNameInput: string = "";
  lastNameInput: string = "";

  // For Change Password Form
  currentPasswordInput: string = "";
  newPasswordInput: string = "";
  confirmNewPasswordInput: string = "";

  subscriptions: Subscription[] = [];

  constructor(
    private notification: NzNotificationService,
    private message: NzMessageService, // Added
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
          this.message.success('Profile updated successfully'); // Using NzMessageService
        },
        error: (errorResponse: HttpErrorResponse) => {
          console.error(errorResponse);
          this.message.error('Error updating profile'); // Using NzMessageService
        }
      });
    this.subscriptions.push(sub);
  }

  onChangePassword(changePasswordForm: NgForm): void {
    if (changePasswordForm.invalid) {
      this.message.error('Please fill in all required fields for password change.');
      // Mark fields as touched to show errors if not already shown by ngSubmit
      Object.values(changePasswordForm.controls).forEach(control => {
        control.markAsTouched();
      });
      return;
    }

    const newPassword = changePasswordForm.value.newPassword;
    const confirmPassword = changePasswordForm.value.confirmNewPassword;

    if (newPassword !== confirmPassword) {
      this.message.error('New password and confirm password do not match.');
      // Optionally set form control error for confirmNewPassword
      changePasswordForm.controls['confirmNewPassword']?.setErrors({'mismatch': true});
      return;
    }
    
    // Basic new password length validation (can be more complex)
    if (newPassword.length < 8) {
        this.message.error('New password must be at least 8 characters long.');
        changePasswordForm.controls['newPassword']?.setErrors({'minlength': true});
        return;
    }


    const request = {
      currentPassword: changePasswordForm.value.currentPassword,
      newPassword: newPassword
    };

    const sub = this.userService.changePassword(request).subscribe({
      next: () => {
        this.message.success('Password changed successfully.');
        changePasswordForm.resetForm();
        // Clear specific input fields if resetForm is not enough or for clarity
        this.currentPasswordInput = "";
        this.newPasswordInput = "";
        this.confirmNewPasswordInput = "";
      },
      error: (errorResponse: HttpErrorResponse) => {
        console.error('Password change error:', errorResponse);
        const errorMessage = errorResponse.error?.message || 'Failed to change password. Please check your current password or try again later.';
        this.message.error(errorMessage);
      }
    });
    this.subscriptions.push(sub);
  }

  // Using NzNotificationService for profile updates as it was originally
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
