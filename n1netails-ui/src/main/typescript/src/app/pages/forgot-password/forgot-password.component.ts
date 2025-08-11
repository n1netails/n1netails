import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Subscription } from 'rxjs';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { validateEmail } from '../../shared/validation/user-login-validation';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { RouterModule } from '@angular/router';
import { AuthenticationService } from '../../service/authentication.service';

@Component({
  selector: 'app-forgot-password',
  templateUrl: './forgot-password.component.html',
  styleUrls: ['./forgot-password.component.less'],
  imports: [NzFormModule, FormsModule, RouterModule]
})
export class ForgotPasswordComponent implements OnInit {

  @ViewChild('forgotPasswordForm') forgotPasswordForm?: NgForm;
  private isLoading: boolean = false;
  private subscriptions: Subscription[] = [];

  constructor(
    private notification: NzNotificationService,
    private authenticateService: AuthenticationService
  ) { }

  ngOnInit() {
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

  public onForgotPasswordFormSubmit(forgotPasswordForm: NgForm): void {
    const value: {email: string} = forgotPasswordForm.value;
    const email = value.email;
    if (!validateEmail(email)) {
      this.presentToast('Error', 'Invalid email format.');
      return;
    }
    this.subscriptions.push(
      this.authenticateService.forgotPassword(email).subscribe({
        next: (response: string) => {
          this.isLoading = true;
          this.presentToast('Success', 'An email with a link to reset your password was sent to the email address associated with your account');
          forgotPasswordForm.resetForm();
        },
        error: (errorResponse: HttpErrorResponse) => {
          this.presentToast('Error', 'Error logging in please try again later. ' + errorResponse.error.message);;
          console.error('Error: ', errorResponse);
          this.isLoading = false;
        }
      })
    )
  }
}
