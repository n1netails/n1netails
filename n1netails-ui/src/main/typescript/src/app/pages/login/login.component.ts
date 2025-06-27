import { Component, OnDestroy, OnInit } from '@angular/core';
import { AuthenticationService } from '../../service/authentication.service';
import { Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { FormsModule, NgForm } from '@angular/forms';
import { User } from '../../model/user';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { HeaderType } from '../../model/enum/header-type.enum';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { PasskeyService, AuthenticationSuccessResponse } from '../../service/passkey.service'; // Import PasskeyService

@Component({
  selector: 'app-login',
  imports: [NzFormModule,FormsModule,RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.less'
})
export class LoginComponent implements OnInit, OnDestroy {

  public isLoading: boolean = false;
  public isPasskeyLoading: boolean = false;
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
    const user: User = form.value; // This User model might only have email/password from form
    this.isLoading = true;
    this.subscriptions.push(
      this.authenticationService.login(user).subscribe({
        next: (response: HttpResponse<User>) => {
          this.saveUserAndNavigate(response);
          this.isLoading = false;
          form.resetForm();
        },
        error: (errorResponse: HttpErrorResponse) => {
          this.presentToast('Error logging in with password: ' + (errorResponse.error?.message || errorResponse.message));
          console.error('Password Login Error: ', errorResponse);
          this.isLoading = false;
        }
      })
    );
  }

  public async onLoginWithPasskey(form: NgForm): Promise<void> {
    this.isPasskeyLoading = true;
    const usernameFromForm = form.value.email; // Assuming email field is used as username for passkey context if needed.
                                          // For discoverable, this isn't strictly necessary for the first attempt.
    try {
      // Attempt discoverable credential login first (username undefined)
      const authResponse: AuthenticationSuccessResponse = await this.passkeyService.loginWithPasskey();
      this.isPasskeyLoading = false;

      if (authResponse && authResponse.success) {
        this.presentToast(`Passkey login successful for ${authResponse.username}!`, 'success');
        // IMPORTANT: After successful passkey authentication, the backend has verified the user.
        // Now, the frontend needs to establish its own session (e.g., get a JWT).
        // This usually involves making another call to a backend endpoint that,
        // based on the successful passkey auth (perhaps a temporary session cookie or a one-time code from passkey auth),
        // returns the standard User object and JWT token.

        // For now, let's assume AuthenticationService needs to be updated or a new method added
        // to handle post-passkey authentication session setup.
        // Example: this.authenticationService.establishSessionForPasskeyUser(authResponse.username);
        // This is a placeholder for what needs to happen next to fully integrate with the existing auth flow.
        // A simple approach: if passkey auth is successful, call a specific endpoint
        // like /api/v1/user/details-after-passkey/{username} which returns HttpResponse<User> with JWT.

        // Quick HACK for now: Manually construct what saveUserAndNavigate expects, if possible.
        // This is NOT secure or correct for a real app. The backend must issue the token.
        // We need a proper way to get the User object and JWT.
        // For this exercise, I will proceed as if a mechanism exists to get user details and token.
        // Let's simulate this by calling a hypothetical method in AuthenticationService.
        // This will likely require a new backend endpoint and AuthenticationService method.

        this.notification.info('Post-Passkey Auth', 'Session establishment step needed here.', { nzPlacement: 'topRight' });
        // TODO: Implement proper session establishment.
        // For now, let's assume we need to fetch user details and token for authResponse.username
        // This is a placeholder for fetching user and token after successful passkey auth
        // Then call this.saveUserAndNavigate(response);
        // For now, just redirecting to dashboard, but user won't be "logged in" in Angular app's context fully.
        // A better approach would be for PasskeyController's finishLogin to return the JWT and User object directly.
        // Or, the PasskeyController sets a secure, short-lived cookie, and another endpoint exchanges that for a JWT.

        // Let's assume for now the backend's /api/v1/passkey/login/finish on success
        // should also log the user in server-side and return the same HttpResponse<User> as regular login.
        // If so, AuthenticationSuccessResponse should include User and token.
        // Modifying AuthenticationSuccessResponse and backend controller is one way.
        // For now, I will assume `authResponse` is enriched by the actual implementation.
        // If `AuthenticationSuccessResponse` from `passkey.service` was updated to include `HttpResponse<User>`:
        // this.saveUserAndNavigate(authResponse.httpResponse); // Assuming authResponse contains it
        // Since it doesn't, we'll just show a success message and the developer needs to integrate session management.

        this.router.navigateByUrl('/dashboard'); // User is authenticated, but Angular app state might not reflect it fully yet.

      } else {
        this.presentToast(`Passkey login failed: ${authResponse?.message || 'Unknown error'}`);
      }
    } catch (error: any) {
      this.isPasskeyLoading = false;
      console.error('Passkey login error:', error);
      // Check if error is due to no discoverable credentials and then try with username if provided
      if (usernameFromForm && error.message && (error.message.includes('No credential available') || error.message.includes('NotFoundError'))) {
        this.presentToast('No discoverable passkey found. Trying with username from email field...', 'info');
        await this.tryLoginWithPasskeyAndUsername(usernameFromForm);
      } else {
        this.presentToast(`Passkey login error: ${error.message || error}`);
      }
    }
  }

  private async tryLoginWithPasskeyAndUsername(username: string): Promise<void> {
    this.isPasskeyLoading = true;
    try {
      const authResponse: AuthenticationSuccessResponse = await this.passkeyService.loginWithPasskey(username);
      this.isPasskeyLoading = false;
      if (authResponse && authResponse.success) {
        this.presentToast(`Passkey login successful for ${authResponse.username}!`, 'success');
        // TODO: Implement proper session establishment (same as above)
        this.notification.info('Post-Passkey Auth', 'Session establishment step needed here.', { nzPlacement: 'topRight' });
        this.router.navigateByUrl('/dashboard');
      } else {
        this.presentToast(`Passkey login with username ${username} failed: ${authResponse?.message || 'Unknown error'}`);
      }
    } catch (error: any) {
      this.isPasskeyLoading = false;
      console.error(`Passkey login error for username ${username}:`, error);
      this.presentToast(`Passkey login error for ${username}: ${error.message || error}`);
    }
  }

  private presentToast(message: string, type: 'success' | 'error' | 'warning' | 'info' = 'error') {
    this.notification[type](type.toUpperCase(), message, {
      nzPlacement: 'topRight',
      nzDuration: 10000
    });
  }

  private saveUserAndNavigate(response: HttpResponse<User>): void {
    const token = response.headers.get(HeaderType.JWT_TOKEN) || "";
    this.authenticationService.saveToken(token);
    this.authenticationService.addUserToLocalCache(response.body || null);
    this.router.navigateByUrl('/dashboard');
  }
}
