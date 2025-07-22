import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AlertService {
  private apiUrl = '/ninetails/alert';

  constructor(private http: HttpClient) { }

  createTail(token: string, tailData: any): Observable<any> {
    const headers = new HttpHeaders().set('N1ne-Token', token);
    return this.http.post(this.apiUrl, tailData, { headers });
  }
}
