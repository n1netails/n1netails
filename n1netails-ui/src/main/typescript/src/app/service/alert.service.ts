import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UiConfigService } from '../shared/util/ui-config.service';

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

  createTail(token: string, tailData: any): Observable<any> {
    const headers = new HttpHeaders().set('N1ne-Token', token);
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.post(this.host, tailData, { headers });
  }

  createManualTail(organizationId: number, userId: number, tailData: any): Observable<any> {
    this.host = this.uiConfigService.getApiUrl() + this.apiPath;
    return this.http.post(`${this.host}/manual/${userId}/organization/${organizationId}`, tailData);
  }
}
