import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { AuthenticationService } from '../../service/authentication.service';
import { Router, RouterModule } from '@angular/router';
import { PasskeyService } from '../../service/passkey.service'; // Import PasskeyService
import { Subscription } from 'rxjs';
import { FormsModule, NgForm } from '@angular/forms';
import { User } from '../../model/user';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { HeaderType } from '../../model/enum/header-type.enum';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzNotificationService } from 'ng-zorro-antd/notification';

@Component({
  selector: 'app-login',
  imports: [NzFormModule,FormsModule,RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.less'
})
export class LoginComponent implements OnInit, OnDestroy {

  public isLoading: boolean = false;
  private subscriptions: Subscription[] = [];

  constructor(
    private notification: NzNotificationService,
    private authenticationService: AuthenticationService,
    private passkeyService: PasskeyService, // Inject PasskeyService
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authenticationService.isUserLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  public onLogin(form: NgForm): void {
    const user: User = form.value;
    this.isLoading = true;
    this.subscriptions.push(
      this.authenticationService.login(user).subscribe({
        next: (response: HttpResponse<User>) => {
          this.saveUser(response);
          this.router.navigateByUrl('/dashboard');
          this.isLoading = false;
          form.resetForm();
        },
        error: (errorResponse: HttpErrorResponse) => {
          const displayMessage = errorResponse.error?.message || 'An unknown error occurred.';
          this.presentToast('Error logging in please try again later. ' + displayMessage);
          console.error(`Login form submission error: Status ${errorResponse.status}. Message: ${displayMessage}`, errorResponse.error);
          this.isLoading = false;
        }
      })
    );
  }

  private async presentToast(message: string) {
    // Ensure the message passed to presentToast is already sanitized or generic.
    // The original 'Error logging in please try again later.' seems fine.
    // If 'message' itself comes from an unsafe source, it needs sanitization here too.
    // For now, assuming 'message' is a developer-crafted string or a sanitized server message.
    this.notification.error('Error', message, { // Using the passed message directly
      nzPlacement: 'topRight',
      nzDuration: 10000
    });
  }

  private saveUser(response: HttpResponse<User>) {
    const token = response.headers.get(HeaderType.JWT_TOKEN) || "";
    this.authenticationService.saveToken(token);
    this.authenticationService.addUserToLocalCache(response.body || null);
  }

  public onLoginWithPasskey(): void {
    const email = (this.loginForm?.value as User)?.email;
    if (email) {
      console.log(`Attempting to login with passkey for user (email provided).`);
    } else {
      console.log(`Attempting to login with passkey (discoverable credential).`);
    }
    this.isLoading = true;
    const domain = window.location.hostname; // Or a configured RP ID domain

    this.subscriptions.push(
      this.passkeyService.startPasskeyAuthentication(email || undefined, domain).subscribe({
        next: (startResponse) => {
          if (startResponse && startResponse.options) {
            this.passkeyService.getPasskey(startResponse.options).subscribe({
              next: (credential) => {
                if (credential) {
                  this.passkeyService.finishPasskeyAuthentication(startResponse.flowId, credential).subscribe({
                    next: (finishResponse) => {
                      if (finishResponse.success && finishResponse.jwtToken 
                        && finishResponse.user
                      ) {
                        // Successful login using passkey, token and user are already saved by PasskeyService
                        this.notification.success('Success', 'Logged in successfully with passkey!', { nzPlacement: 'topRight' });
                        this.router.navigateByUrl('/dashboard');
                      } else {
                        this.presentToast(`Passkey login failed: ${finishResponse.message}`);
                      }
                      this.isLoading = false;
                    },
                    error: (err) => {
                      // err.message should be the sanitized message from passkey.service.ts
                      console.error('Error finishing passkey authentication:', err.message ? err.message : err);
                      this.presentToast(err.message || 'An unknown error occurred while finishing passkey login.');
                      this.isLoading = false;
                    }
                  });
                } else {
                  // This case implies credential was null, user might have cancelled.
                  console.log('Passkey assertion was cancelled by user or failed before finishing.');
                  this.presentToast('Passkey assertion was cancelled or failed.');
                  this.isLoading = false;
                }
              },
              error: (err) => {
                // err.message should be the sanitized message from passkey.service.ts
                console.error('Error getting passkey credential:', err.message ? err.message : err);
                this.presentToast(err.message || 'Could not get passkey. User may have cancelled or an error occurred.');
                this.isLoading = false;
              }
            });
          } else {
            console.warn('Failed to start passkey authentication process, startResponse or options missing.');
            this.presentToast('Failed to start passkey authentication process.');
            this.isLoading = false;
          }
        },
        error: (err) => {
          // err.message should be the sanitized message from passkey.service.ts
          console.error('Error starting passkey authentication:', err.message ? err.message : err);
          this.presentToast(err.message || 'An unknown error occurred while starting passkey login.');
          this.isLoading = false;
        }
      })
    );
  }

  // Access to the form to get values if needed
  @ViewChild('loginForm') loginForm?: NgForm;
}
