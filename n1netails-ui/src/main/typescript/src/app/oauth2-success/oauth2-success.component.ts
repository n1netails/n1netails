import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthenticationService } from '../service/authentication.service';
import { UserService } from '../service/user.service';
import { User } from '../model/user';

@Component({
  selector: 'app-oauth2-success',
  imports: [],
  templateUrl: './oauth2-success.component.html',
  styleUrl: './oauth2-success.component.less'
})
export class Oauth2SuccessComponent {
  constructor(
    private authenticationService: AuthenticationService,
    private userService: UserService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    console.log("oauth2 success login");
    this.route.queryParams.subscribe(params => {
      const token = params['token'];
      console.log('TOKEN', token);

      if (token) {
        this.authenticationService.saveToken(token);
        this.userService.getSelf().subscribe((user: User) => {
          this.authenticationService.addUserToLocalCache(user);
          this.router.navigate(['/dashboard']);
        });
      } else {
        // If no token, redirect to login
        this.router.navigate(['/login']);
      }
    });
  }
}
