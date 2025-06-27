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
import { PasskeyService } from '../../service/passkey.service'; // Re-add PasskeyService

@Component({
  selector: 'app-register',
  imports: [NzFormModule,FormsModule,RouterModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.less'
})
export class RegisterComponent implements OnInit, OnDestroy {

  public isLoading: boolean = false;
  public isPasskeyLoading: boolean = false; // Re-add for passkey button
  private subscriptions: Subscription[] = [];

  constructor(
    private notification: NzNotificationService,
    private authenticationService: AuthenticationService,
    private passkeyService: PasskeyService, // Re-add PasskeyService
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

    if (!this.validateEmail(email) || !this.validatePassword(password)) {
      return;
    }

    this.isLoading = true;
    this.subscriptions.push(
      this.authenticationService.register(user).subscribe({
        next: (response: HttpResponse<User>) => {
          this.saveUser(response);
          this.presentToast('Registration successful! You can now log in.', 'success');
          // Redirect to login, or to a page where they can add a passkey if desired.
          // For now, redirecting to dashboard as per original logic, assuming login is implicit.
          // Or, more explicitly, redirect to login:
          this.router.navigateByUrl('/login'); // Changed to login after registration
          this.isLoading = false;
          form.resetForm();
        },
        error: (errorResponse: HttpErrorResponse) => {
          console.error('Error: ', errorResponse);
          this.presentToast('Error registering: \n' + (errorResponse.error?.message || errorResponse.message));
          this.isLoading = false;
        }
      })
    );
  }

  public async onRegisterWithPasskey(form: NgForm): Promise<void> {
    const email = form.value.email;
    if (!email || !this.validateEmail(email)) {
      this.presentToast('A valid email is required to register a passkey.');
      return;
    }

    // Assumption: User with this email MUST ALREADY EXIST.
    // This button is for adding a passkey to an existing account.
    this.isPasskeyLoading = true;
    try {
      const result = await this.passkeyService.registerPasskey(email);
      this.isPasskeyLoading = false;
      if (result && result.success) {
        this.presentToast('Passkey successfully added to your account!', 'success');
        // Optionally, redirect or give further instructions.
        // e.g., redirect to login page or profile page.
        this.router.navigateByUrl('/login');
      } else {
        this.presentToast(`Passkey registration failed: ${result?.message || 'User may not exist or another error occurred.'}`);
      }
    } catch (error: any) {
      this.isPasskeyLoading = false;
      console.error('Passkey registration error:', error);
      this.presentToast(`Passkey registration error: ${error.message || 'An unexpected error occurred.'}`);
    }
  }

  private presentToast(message: string, type: 'success' | 'error' | 'warning' | 'info' = 'error') {
    this.notification[type](type.toUpperCase(), message, {
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
    const passwordRegex = /^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,}$/;
    if (!passwordRegex.test(password)) {
      this.presentToast('Password must be at least 8 characters long, contain at least 1 uppercase letter, and 1 special character.');
      return false;
    }
    return true;
  }
}
