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

  host: string = '';
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
      try {
        const decodedToken = this.jwtHelper.decodeToken(this.token);
        if (decodedToken && decodedToken.sub != null && decodedToken.sub !== '') {
          if (!this.jwtHelper.isTokenExpired(this.token)) {
            this.loggedInUsername = decodedToken.sub;
            return true;
          } else {
            this.logOut(); // Token expired
            return false;
          }
        }
      } catch (error) {
        console.error('Error decoding token:', error);
        this.logOut(); // Invalid token
        return false;
      }
    }
    // No token or failed decoding
    this.logOut();
    return false;
  }

  public hasAuthority(requiredAuthority: string): boolean {
    this.loadToken();
    if (!this.token) {
      return false;
    }
    try {
      const decodedToken = this.jwtHelper.decodeToken(this.token);
      if (decodedToken && decodedToken.authorities && Array.isArray(decodedToken.authorities)) {
        return decodedToken.authorities.includes(requiredAuthority);
      }
      // Also check for 'scope' if authorities might be stored there (common in OAuth2)
      if (decodedToken && typeof decodedToken.scope === 'string') {
        const scopes = decodedToken.scope.split(' ');
        return scopes.includes(requiredAuthority);
      }
    } catch (error) {
      console.error('Error decoding token for authority check:', error);
      return false;
    }
    return false;
  }
}
