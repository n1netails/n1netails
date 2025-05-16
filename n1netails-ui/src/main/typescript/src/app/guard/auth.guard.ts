import { Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from "@angular/router";
import { Observable } from "rxjs";
import { AuthenticationService } from "../service/authentication.service";

@Injectable({ providedIn: 'root' })
export class AuthGuard  {

    constructor(private authenticationService: AuthenticationService,
                private router: Router) {}

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
        if (this.authenticationService.isUserLoggedIn()) {
            return true;
        }
        this.router.navigate(['/login']); 
        return false;
    }
}
