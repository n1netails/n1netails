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
        if (
            request.url.includes(`${this.authenticationService.host}/ninetails/user/login`) ||
            request.url.includes(`${this.authenticationService.host}/ninetails/user/register`) ||
            // currently users can only register passkeys if they have an existing account that is registered with basic authentication.
            // request.url.includes(`${this.authenticationService.host}/ninetails/auth/passkey/register/start`) ||
            request.url.includes(`${this.authenticationService.host}/ninetails/auth/passkey/register/finish`) ||
            request.url.includes(`${this.authenticationService.host}/ninetails/auth/passkey/login/start`) ||
            request.url.includes(`${this.authenticationService.host}/ninetails/auth/passkey/login/finish`)
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
                    this.authenticationService.logOut();
                    setTimeout(() => {
                        this.router.navigate(['/login']);
                    }, 1000);
                }
            }
        }));
    }
}
