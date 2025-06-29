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
import { CommonModule } from '@angular/common';
import { PasskeyService } from '../../service/passkey.service';
import { NzIconModule } from 'ng-zorro-antd/icon';

@Component({
  selector: 'app-edit-profile',
  imports: [
    NzLayoutModule,
    NzGridModule,
    NzCardModule,
    NzAvatarModule,
    NzIconModule,
    HeaderComponent,
    SidenavComponent,
    NzFormModule,
    FormsModule,
    CommonModule
  ],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.less'
})
export class EditProfileComponent implements OnInit, OnDestroy {

  user: User = new User();
  usernameInput: string = "";
  firstNameInput: string = "";
  lastNameInput: string = "";
  isLoading = false;

  // Password Reset
  newPassword: string = '';
  passwordResetSuccessMessage: string = '';
  passwordResetErrorMessage: string = '';

  subscriptions: Subscription[] = [];

  constructor(
    private notification: NzNotificationService,
    private authenticationService: AuthenticationService,
    private passkeyService: PasskeyService,
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

    // Password Reset
  onPasswordReset() {
    console.log('REQUEST TO RESET PASSWORD');
    this.passwordResetSuccessMessage = '';
    this.passwordResetErrorMessage = '';

    if (!this.newPassword) {
      this.passwordResetErrorMessage = 'New password cannot be empty.';
      return;
    }

    if (!this.user || !this.user.email) {
      this.passwordResetErrorMessage = 'User email is not available.';
      return;
    }

    console.log('new password:', this.newPassword);
    console.log('user email', this.user.email);

    this.authenticationService.resetPassword(this.user.email, this.newPassword).subscribe({
      next: () => {
        this.passwordResetSuccessMessage = 'Password updated successfully.';
        console.log('Password updated successfully.');
        this.newPassword = '';
        this.presentToast('Success', 'Password updated successfully.');
      },
      error: (error) => {
        this.passwordResetErrorMessage = 'Failed to update password. The password needs to contain at least 8 characters, 1 uppercase character, and 1 special character.';
        console.error('Failed to update password:', error);
        this.newPassword = '';
        this.presentToast('Error', this.passwordResetErrorMessage);
      }
    });
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

  public onRegisterWithPasskey(): void {
    // Get email from the form; consider making this more explicit for passkey flow
    // const email = (this.registerForm?.value as User)?.email;
    const email = this.user.email;
    if (!email || email.trim() === '') {
      this.presentToast('Error', 'Please enter a email to register with a passkey.');
      return;
    }
    console.log(`Attempting to register with passkey for email: ${email}`);
    this.isLoading = true;
    // TODO: Call PasskeyService to start registration flow
    this.isLoading = true;
    const domain = window.location.hostname;

    console.log("email: ", email);
    console.log("domain: ", domain);
    this.subscriptions.push(
      this.passkeyService.startPasskeyRegistration(email, domain).subscribe({
        next: (startResponse) => {
          console.log("start passkey response: ", startResponse);
          if (startResponse && startResponse.options) {
            console.log("creating pass key");
            this.passkeyService.createPasskey(startResponse.options).subscribe({
              next: (credential) => {
                if (credential) {

                  // Prompt for a friendly name for the key, or generate one
                  const friendlyName = prompt("Enter a name for this passkey (e.g., 'My Laptop Chrome')", "My Passkey");

                  console.log("FINISHING PASSKEY REGISTRATION");
                  this.passkeyService.finishPasskeyRegistration(startResponse.flowId, credential, friendlyName || undefined).subscribe({
                    next: (finishResponse) => {
                      if (finishResponse.success) {
                        this.notification.success('Success', 'Passkey registration successful! You can now login using your passkey in the future.', { nzPlacement: 'topRight' });
                        // this.router.navigate(['/login']); // Navigate to login after successful passkey registration
                      } else {
                        this.presentToast('Error', `Passkey registration failed: ${finishResponse.message}`);
                      }
                      this.isLoading = false;
                    },
                    error: (err) => {
                      console.error('Error finishing passkey registration:', err);
                      this.presentToast('Error', err.message || 'An unknown error occurred while finishing passkey registration.');
                      this.isLoading = false;
                    }
                  });
                } else {
                  this.presentToast('Error', 'Passkey creation was cancelled or failed.');
                  this.isLoading = false;
                }
              },
              error: (err) => {
                console.error('Error creating passkey credential:', err);
                this.presentToast('Error', err.message || 'Could not create passkey. User may have cancelled or an error occurred.');
                this.isLoading = false;
              }
            });
          } else {
            this.presentToast('Error', 'Failed to start passkey registration process.');
            this.isLoading = false;
          }
        },
        error: (err) => {
          console.error('Error starting passkey registration:', err);
          this.presentToast('Error', err.message || 'An unknown error occurred while starting passkey registration.');
          this.isLoading = false;
        }
      })
    );
  }
}
