import { Injectable } from '@angular/core';
import { UiConfigService } from '../shared/ui-config.service';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { JwtHelperService } from '@auth0/angular-jwt';
import { User } from '../model/user';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private host: string = '';
  private token: string | null = "";
  private loggedInUsername: string = "";
  private jwtHelper = new JwtHelperService();

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService
  ) { 
    this.host = this.uiConfigService.getApiUrl();
  }

  public login(user: User): Observable<HttpResponse<User>> {
    console.log('login API URL:', this.host); 
    return this.http.post<User>(`${this.host}/api/user/login`, user, { observe: 'response' });
  }

  public register(user: User): Observable<HttpResponse<User>> {
    console.log('register API URL:', this.host); 
    return this.http.post<User>(`${this.host}/api/user/register`, user, { observe: 'response' });
  }

  public logOut(): void {
    this.token = "";
    this.loggedInUsername = "";
    localStorage.removeItem('user');
    localStorage.removeItem('token');
    localStorage.clear();
    // localStorage.removeItem('users');
  }

  public saveToken(token: string): void {
    this.token = token;
    localStorage.setItem('token', token);
  }

  public addUserToLocalCache(user: User | null): void {
    if (user !== null) {
      localStorage.setItem('user', JSON.stringify(user));
    }
  }

  public getUserFromLocalCache(): User {
    return JSON.parse(localStorage.getItem('user') || '{}');
  }

  public loadToken(): void {
    this.token = localStorage.getItem('token');
  }

  public getToken(): string | null {
    return this.token;
  }

  public isUserLoggedIn(): boolean {
    this.loadToken();
    if (this.token != null && this.token !== '') {
      if (this.jwtHelper.decodeToken(this.token).sub != null || '') {
        if (!this.jwtHelper.isTokenExpired(this.token)) {
          this.loggedInUsername = this.jwtHelper.decodeToken(this.token).sub;
          return true;
        } 
        else {
          this.logOut();
          return false;
        }
      }
    } 
    this.logOut();
    return false;
  }
}
