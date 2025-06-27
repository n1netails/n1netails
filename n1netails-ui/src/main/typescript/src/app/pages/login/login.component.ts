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
import { PasskeyAuthService } from '../../core/services/passkey-auth.service'; // Added
import * as WebAuthnUtils from '../../core/utils/webauthn.utils'; // Added

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
    private router: Router,
    private passkeyAuthService: PasskeyAuthService // Added
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
          this.presentToast('Error logging in please try again later. ' + errorResponse.error.message);;
          console.error('Error: ', errorResponse);
          this.isLoading = false;
        }
      })
    );
  }

  private async presentToast(message: string) {
    this.notification.error('Error', "Error logging in please try again later.", {
      nzPlacement: 'topRight',
      nzDuration: 10000
    });
  }

  private saveUser(response: HttpResponse<User>) {
    const token = response.headers.get(HeaderType.JWT_TOKEN) || "";
    this.authenticationService.saveToken(token);
    this.authenticationService.addUserToLocalCache(response.body || null);
  }

  public async onLoginWithPasskey(form: NgForm): Promise<void> {
    // Username (email) from form can be used as a hint, but not strictly required for discoverable credentials
    const username = form.value.email;
    this.isLoading = true;

    try {
      // 1. Start passkey login
      // Pass username if available, otherwise backend should handle discoverable credentials flow
      const startResponse = await this.passkeyAuthService.startLogin(username || undefined).toPromise();
      if (!startResponse || !startResponse.options) {
        throw new Error('Failed to get login options from server.');
      }

      // 2. Prepare options and call navigator.credentials.get()
      const credentialRequestOptions = WebAuthnUtils.prepareRequestOptions(JSON.parse(startResponse.options));

      let credential;
      try {
        credential = await navigator.credentials.get({ publicKey: credentialRequestOptions });
      } catch (err: any) {
        console.error('navigator.credentials.get() error:', err);
        this.presentToast(`Passkey selection failed: ${err.message || 'No authenticator was selected or an error occurred.'}`);
        this.isLoading = false;
        return;
      }

      if (!credential) {
        this.presentToast('Passkey selection was cancelled or failed.');
        this.isLoading = false;
        return;
      }

      // 3. Convert credential to JSON and finish login
      const credentialForServer = WebAuthnUtils.publicKeyCredentialToJSON(credential as PublicKeyCredential);

      const authResponse = await this.passkeyAuthService.finishLogin({
        assertionId: startResponse.assertionId,
        credential: JSON.stringify(credentialForServer)
      }).toPromise();

      if (authResponse && authResponse.token) {
        // Manually construct a HttpResponse-like object to reuse saveUser
        const fakeResponse: Partial<HttpResponse<User>> = {
          headers: { get: (headerName: string) => headerName === HeaderType.JWT_TOKEN ? authResponse.token : null } as any,
          body: { // Construct a partial User object based on what PasskeyAuthenticationResponse provides
            // This might need adjustment based on what `addUserToLocalCache` actually needs
            // and what `PasskeyAuthenticationResponse` contains.
            // For now, assume username from authResponse.username is enough for local cache.
            // A more robust solution might involve fetching full user details after passkey login.
            email: authResponse.username,
            // Other User fields would be undefined here unless fetched separately
          } as User
        };
        this.authenticationService.saveToken(authResponse.token);
        // If PasskeyAuthenticationResponse contains enough user details, use them.
        // Otherwise, addUserToLocalCache might need to be adapted or a separate user fetch call made.
        // For now, creating a minimal user object for the cache.
        const minimalUser: User = {
            userId: '', // Not available from passkey response directly
            firstName: '', // Not available
            lastName: '', // Not available
            username: authResponse.username,
            email: authResponse.username, // Assuming username is email
            profileImageUrl: '', // Not available
            lastLoginDateDisplay: null,
            joinDate: null,
            role: '', // Role might need to be parsed from token or fetched
            authorities: [], // Authorities from token
            active: true,
            notLocked: true,
            enabled: true,
            organizations: []
        };
        this.authenticationService.addUserToLocalCache(minimalUser);


        this.notification.success('Success', 'Logged in successfully with Passkey!', { nzPlacement: 'topRight' });
        this.router.navigateByUrl('/dashboard');
        form.resetForm();
      } else {
        throw new Error('Passkey login failed: No token received.');
      }

    } catch (error: any) {
      console.error('Passkey login process error: ', error);
      this.presentToast(`Passkey login failed: ${error.message || 'An unknown error occurred.'}`);
    } finally {
      this.isLoading = false;
    }
  }
}
