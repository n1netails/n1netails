import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { PasskeyService } from '../../service/passkey.service'; // Import PasskeyService
import { NzFormModule } from 'ng-zorro-antd/form';
import { Subscription } from 'rxjs';
import { User } from '../../model/user';
import { AuthenticationService } from '../../service/authentication.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { HeaderType } from '../../model/enum/header-type.enum';
import { NzNotificationService } from 'ng-zorro-antd/notification';

@Component({
  selector: 'app-register',
  imports: [NzFormModule,FormsModule,RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.less'
})
export class RegisterComponent implements OnInit, OnDestroy {

  public isLoading: boolean = false;
  private subscriptions: Subscription[] = [];

  constructor(
    private notification: NzNotificationService,
    private authenticationService: AuthenticationService,
    private passkeyService: PasskeyService, // Inject PasskeyService
    private router: Router,
  ) {}

  ngOnInit(): void {
    if (this.authenticationService.isUserLoggedIn()) {
      this.router.navigateByUrl('/dashboard');
    }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  public onRegister(form: NgForm): void {
    const user: User = form.value;
    const email = user.email;
    const password = user.password;

    // Validate email and password before sending to the server
    if (!this.validateEmail(email) || !this.validatePassword(password)) {
      return;
    }

    this.isLoading = true;
    this.subscriptions.push(
      this.authenticationService.register(user).subscribe({
        next: (response: HttpResponse<User>) => {
          this.saveUser(response)
          this.router.navigateByUrl('/dashboard');
          this.isLoading = false;
          form.resetForm();
        },
        error: (errorResponse: HttpErrorResponse) => {
          console.error('Error: ', errorResponse);
          this.presentToast('Error registering: \n' + errorResponse.error.message);
          this.isLoading = false;
        }
      })
    )
  }

  private async presentToast(message: string) {
    this.notification.error('Error', message, {
      nzPlacement: 'topRight',
      nzDuration: 10000
    });
  }

  private saveUser(response: HttpResponse<User>) {
    const token = response.headers.get(HeaderType.JWT_TOKEN) || "";
    this.authenticationService.saveToken(token);
    this.authenticationService.addUserToLocalCache(response.body || null);
  }

  private validateEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email)) {
      this.presentToast('Invalid email format.');
      return false;
    }
    return true;
  }

  private validatePassword(password: string): boolean {
    // Password must be at least 8 characters, contain 1 uppercase, and 1 special character
    const passwordRegex = /^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/;
    if (!passwordRegex.test(password)) {
      this.presentToast('Password must be at least 8 characters long, contain at least 1 uppercase letter, and 1 special character.');
      return false;
    }
    return true;
  }

  public onRegisterWithPasskey(): void {
    // Get username from the form; consider making this more explicit for passkey flow
    const username = (this.registerForm?.value as User)?.username;
    if (!username || username.trim() === '') {
      this.presentToast('Please enter a username to register with a passkey.');
      return;
    }
    console.log(`Attempting to register with passkey for username: ${username}`);
    this.isLoading = true;
    // TODO: Call PasskeyService to start registration flow
    this.isLoading = true;
    const domain = window.location.hostname;

    this.subscriptions.push(
      this.passkeyService.startPasskeyRegistration(username, domain).subscribe({
        next: (startResponse) => {
          if (startResponse && startResponse.options) {
            this.passkeyService.createPasskey(startResponse.options).subscribe({
              next: (credential) => {
                if (credential) {
                  // Prompt for a friendly name for the key, or generate one
                  const friendlyName = prompt("Enter a name for this passkey (e.g., 'My Laptop Chrome')", "My Passkey");

                  this.passkeyService.finishPasskeyRegistration(startResponse.flowId, credential, friendlyName || undefined).subscribe({
                    next: (finishResponse) => {
                      if (finishResponse.success) {
                        this.notification.success('Success', 'Passkey registration successful! Please log in.', { nzPlacement: 'topRight' });
                        this.router.navigate(['/login']); // Navigate to login after successful passkey registration
                      } else {
                        this.presentToast(`Passkey registration failed: ${finishResponse.message}`);
                      }
                      this.isLoading = false;
                    },
                    error: (err) => {
                      console.error('Error finishing passkey registration:', err);
                      this.presentToast(err.message || 'An unknown error occurred while finishing passkey registration.');
                      this.isLoading = false;
                    }
                  });
                } else {
                  this.presentToast('Passkey creation was cancelled or failed.');
                  this.isLoading = false;
                }
              },
              error: (err) => {
                console.error('Error creating passkey credential:', err);
                this.presentToast(err.message || 'Could not create passkey. User may have cancelled or an error occurred.');
                this.isLoading = false;
              }
            });
          } else {
            this.presentToast('Failed to start passkey registration process.');
            this.isLoading = false;
          }
        },
        error: (err) => {
          console.error('Error starting passkey registration:', err);
          this.presentToast(err.message || 'An unknown error occurred while starting passkey registration.');
          this.isLoading = false;
        }
      })
    );
  }

  // Access to the form to get values if needed by onRegisterWithPasskey
  @ViewChild('registerForm') registerForm?: NgForm;
}
