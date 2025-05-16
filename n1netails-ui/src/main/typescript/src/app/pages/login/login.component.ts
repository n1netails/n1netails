import { Component, OnDestroy, OnInit } from '@angular/core';
import { AuthenticationService } from '../../service/authentication.service';
import { Router, RouterModule } from '@angular/router';
import { Subscription } from 'rxjs';
import { FormsModule, NgForm } from '@angular/forms';
import { User } from '../../model/user';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { HeaderType } from '../../model/enum/header-type.enum';
import { NzFormModule } from 'ng-zorro-antd/form';

@Component({
  selector: 'app-login',
  imports: [NzFormModule, FormsModule,RouterModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.less'
})
export class LoginComponent implements OnInit, OnDestroy {

  public isLoading: boolean = false;
  private subscriptions: Subscription[] = [];

  constructor(
    private authenticationService: AuthenticationService,
    private router: Router
  ) {}

  ngOnInit(): void {
    // if (this.authenticationService.isUserLoggedIn()) {
    //   this.router.navigate(['/dashboard']);
    // }
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
          // this.presentToast('Error logging in please try again later. ' + errorResponse.error.message);;
          console.error('Error: ', errorResponse);
          this.isLoading = false;
        }
      })
    );
  }

  // private async presentToast(message: string) {
  //   const toast = await this.toastController.create({
  //     position: 'bottom',
  //     message: message,
  //     duration: 10000
  //   });
  //   toast.present();
  // }

  private saveUser(response: HttpResponse<User>) {
    const token = response.headers.get(HeaderType.JWT_TOKEN) || "";
    this.authenticationService.saveToken(token);
    this.authenticationService.addUserToLocalCache(response.body || null);
  }
}
