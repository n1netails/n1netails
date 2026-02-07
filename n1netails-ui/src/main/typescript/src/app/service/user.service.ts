import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { UiConfigService } from '../shared/util/ui-config.service';
import { User } from '../model/user';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  host: string = '';

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService
  ) {}

  getSelf(): Observable<User> {
    this.host = this.uiConfigService.getApiUrl();
    return this.http.get<User>(`${this.host}/ninetails/user/self`);
  }

  editUser(user: User): Observable<User> {
    this.host = this.uiConfigService.getApiUrl();
    return this.http.post<User>(`${this.host}/ninetails/user/edit`, user);
  }

  completeTutorial(): Observable<any> {
    this.host = this.uiConfigService.getApiUrl();
    return this.http.post(`${this.host}/ninetails/user/complete-tutorial`, {});
  }

  setTutorialInProgress(inProgress: boolean) {
    localStorage.setItem('tutorialInProgress', String(inProgress));
  }

  tutorialInProgress(): boolean {
    return Boolean(localStorage.getItem('tutorialInProgress'));
  }
}
