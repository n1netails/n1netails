import { Component, OnDestroy, OnInit, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NzFormModule } from 'ng-zorro-antd/form';
import { Subscription } from 'rxjs';
import { User } from '../../model/user';
import { AuthenticationService } from '../../service/authentication.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { HeaderType } from '../../model/enum/header-type.enum';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { NzIconModule } from 'ng-zorro-antd/icon';
import { UiConfigService } from '../../shared/util/ui-config.service';
import { CommonModule } from '@angular/common';
import { validateEmail, validatePassword } from '../../shared/validation/user-login-validation';

@Component({
  selector: 'app-register',
  imports: [NzFormModule, FormsModule, RouterModule, NzIconModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.less',
})
export class RegisterComponent implements OnInit, OnDestroy {
  public isLoading: boolean = false;
  private subscriptions: Subscription[] = [];

  githubAuthEnabled: boolean = false;
  googleAuthEnabled: boolean = false;

  constructor(
    private uiConfigService: UiConfigService,
    private notification: NzNotificationService,
    private authenticationService: AuthenticationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    if (this.authenticationService.isUserLoggedIn()) {
      this.router.navigateByUrl('/dashboard');
    }

    this.githubAuthEnabled = this.uiConfigService.isGithubAuthEnabled();
    this.googleAuthEnabled = this.uiConfigService.isGoogleAuthEnabled();
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach((sub) => sub.unsubscribe());
  }

  loginWithGithub() {
    // Redirects to Spring Boot's OAuth2 authorization endpoint
    // window.location.href = 'http://localhost:9901/oauth2/authorization/github';
    window.location.href = this.uiConfigService.getApiUrl() + '/oauth2/authorization/github';
  }

  loginWithGoogle() {
    // Redirects to Spring Boot's OAuth2 authorization endpoint
    // window.location.href = 'http://localhost:9901/oauth2/authorization/google';
    window.location.href = this.uiConfigService.getApiUrl() + '/oauth2/authorization/google';
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
          this.saveUser(response);
          this.router.navigateByUrl('/dashboard');
          this.isLoading = false;
          form.resetForm();
        },
        error: (errorResponse: HttpErrorResponse) => {
          console.error('Error: ', errorResponse);
          this.presentToast('Error registering: \n' + errorResponse.error.message);
          this.isLoading = false;
        },
      })
    );
  }

  private async presentToast(message: string) {
    this.notification.error('Error', message, {
      nzPlacement: 'topRight',
      nzDuration: 10000,
    });
  }

  private saveUser(response: HttpResponse<User>) {
    const token = response.headers.get(HeaderType.JWT_TOKEN) || '';
    this.authenticationService.saveToken(token);
    this.authenticationService.addUserToLocalCache(response.body || null);
  }

  private validateEmail(email: string): boolean {
    const emailValidated = validateEmail(email);
    if (!emailValidated) {
      this.presentToast('Invalid email format.');
      return false;
    }
    return true;
  }

  private validatePassword(password: string): boolean {
    const passwordValidated = validatePassword(password);
    if (!passwordValidated) {
      this.presentToast(
        'Password must be at least 8 characters long, contain at least 1 uppercase letter, and 1 special character.'
      );
      return false;
    }
    return true;
  }

  // Access to the form to get values if needed by onRegisterWithPasskey
  @ViewChild('registerForm') registerForm?: NgForm;
}
