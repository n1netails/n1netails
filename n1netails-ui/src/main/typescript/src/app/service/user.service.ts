import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UiConfigService } from '../shared/ui-config.service';
import { User } from '../model/user'; // This might be UserRegisterRequest or similar, UserResponse will be different
import { Observable } from 'rxjs';

// Copied from tail.service.ts - consider moving to a shared location
export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number; // current page index
}

export interface UserResponse {
  id: number;
  userId: string;
  firstName: string;
  lastName: string;
  email: string;
  role: string;
  active: boolean;
  notLocked: boolean;
  joinDate: string; // Or Date, keep as string for now
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private host: string; // Base API URL

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService
  ) { 
    this.host = this.uiConfigService.getApiUrl(); // Assuming this returns the base URL like http://localhost:8080
  }

  // This existing method might need review based on User model vs UserRegisterRequest
  editUser(user: User): Observable<User> { 
    return this.http.post<User>(`${this.host}/api/user/edit`, user);
  }

  getUsers(page: number, size: number): Observable<Page<UserResponse>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    // Ensure the URL is correct for the admin endpoint
    return this.http.get<Page<UserResponse>>(`${this.host}/api/admin/users`, { params });
  }

  changePassword(request: ChangePasswordRequest): Observable<any> {
    return this.http.post<any>(`${this.host}/api/user/change-password`, request);
  }
}
