import { Component, OnInit, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { NzFormModule } from 'ng-zorro-antd/form';
import { NzNotificationService } from 'ng-zorro-antd/notification';
import { validatePassword } from '../../shared/validation/user-login-validation';
import { AuthenticationService } from '../../service/authentication.service';
import { Subscription } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

export interface ForgotPasswordResetRequest {
  requestId: string,
  newRawPassword: string,
}

@Component({
  selector: 'app-reset-password',
  templateUrl: './reset-password.component.html',
  styleUrls: ['./reset-password.component.less'],
  imports: [NzFormModule, FormsModule, RouterModule]
})
export class ResetPasswordComponent implements OnInit {

  @ViewChild("resetPasswordForm") resetPasswordForm?: NgForm;
  private subscriptions: Subscription[] = [];

  constructor(
    private activatedRoute: ActivatedRoute,
    private notification: NzNotificationService,
    private authenticationService: AuthenticationService,
    private router: Router
  ) { }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(params => {
      const requestId = params['request_id'];
      console.log(requestId)
    })
  }

  public onResetPasswordFormSubmit(resetPasswordForm: NgForm): void {
    const requestId = this.activatedRoute.snapshot.queryParamMap.get('request_id')
    console.log("Reset password with id: " + requestId)
    const value: {password: string, reEnterPassword: string} = resetPasswordForm.value;
    const password = value.password;
    const reEnterPassword = value.reEnterPassword;
    if (password !== reEnterPassword) {
      this.presentToast('Error', 'Both password must be match')
      return;
    }
    if (!validatePassword(password)) {
      this.presentToast('Error', 'Password must be at least 8 characters long, contain at least 1 uppercase letter, and 1 special character.');
      return;
    }
    this.subscriptions.push(
      this.authenticationService.resetPasswordOnForgot({requestId: requestId || "", newRawPassword: password}).subscribe({
        next: (response: string) => {
          this.presentToast('Success', response);
          resetPasswordForm.resetForm();
        },
        error: (errorResponse: HttpErrorResponse) => {
          this.presentToast('Error', JSON.parse(errorResponse.error).message)
        }
      })
    )
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
}
