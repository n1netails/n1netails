import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { NzFormModule } from 'ng-zorro-antd/form';
import { Subscription } from 'rxjs';
import { User } from '../../model/user';
import { AuthenticationService } from '../../service/authentication.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { HeaderType } from '../../model/enum/header-type.enum';

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
    private router: Router,
    private authenticationService: AuthenticationService,
  ) {}

  ngOnInit(): void {
    // if (this.authenticationService.isUserLoggedIn()) {
    //   this.router.navigateByUrl('/dashboard');
    // }
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  public onRegister(form: NgForm): void {
    const user: User = form.value;
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
          // this.presentToast('Error registering in please try again later. ' + errorResponse.error.message);
          this.isLoading = false;
        }
      })
    )
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
