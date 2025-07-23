import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/util/ui-config.service';
import { TailAlert } from '../model/interface/tail-alert.interface';

@Injectable({
  providedIn: 'root'
})
export class AlertService {

  host: string = '';
  private apiPath = '/ninetails/alert';

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService,
  ) { }

  createTail(token: string, tailAlert: TailAlert): Observable<void> {
    const headers = new HttpHeaders().set('N1ne-Token', token);
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.post<void>(this.host, tailAlert, { headers });
  }

  createManualTail(organizationId: number, userId: number, tailAlert: TailAlert): Observable<void> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.post<void>(`${this.host}/manual/${userId}/organization/${organizationId}`, tailAlert);
  }
}
