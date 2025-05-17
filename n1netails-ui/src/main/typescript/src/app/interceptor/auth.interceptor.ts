import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { Observable, tap } from "rxjs";
import { AuthenticationService } from "../service/authentication.service";

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    constructor(
        private authenticationService: AuthenticationService,
        private router: Router
    ) {}


    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        if (request.url.includes(`${this.authenticationService.host}/api/user/login`) ||
            request.url.includes(`${this.authenticationService.host}/api/user/register`)     
        ) {
            return next.handle(request);
        }

        this.authenticationService.loadToken();
        const token = this.authenticationService.getToken();
        const authorizationRequest = request.clone({ setHeaders: { Authorization: `Bearer ${token}` }});

        return next.handle(authorizationRequest).pipe(tap({
            next: () => {},
            error: (error: any) => {
                if (error instanceof HttpErrorResponse) {
                    if (error.status !== 401) {
                        return;
                    }
                    this.presentToast('Your login token has expired please login again sorry for the inconvinence.');
                    this.authenticationService.logOut();
                    setTimeout(() => {
                        this.router.navigate(['/login']);
                    }, 1000);
                }
            }
        }));
    }
   
    private async presentToast(message: string) {
        // const toast = await this.toastController.create({
        //   position: 'top',
        //   message: message,
        //   duration: 3000,
        //   color: 'danger'
        // });
        // toast.present();
        alert(message);
    }
}
