import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NzFormModule } from 'ng-zorro-antd/form';
import { Subscription } from 'rxjs';
import { User } from '../../model/user';
import { AuthenticationService } from '../../service/authentication.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { HeaderType } from '../../model/enum/header-type.enum';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { PasskeyAuthService } from '../../core/services/passkey-auth.service'; // Added
import * as WebAuthnUtils from '../../core/utils/webauthn.utils'; // Added

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
}
