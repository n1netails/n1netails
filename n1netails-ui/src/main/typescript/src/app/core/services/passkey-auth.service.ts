import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../../environments/environment'; // Check if environment.ts exists and has apiUrl

// Assuming DTO structures from backend for request/response types
// These should ideally be in shared interfaces if you have a mono-repo or shared types library
interface PasskeyRegistrationStartRequest {
  username: string;
  displayName?: string;
}

interface PasskeyRegistrationStartResponse {
  registrationId: string;
  options: any; // This will be parsed PublicKeyCredentialCreationOptions like structure
}

interface PasskeyRegistrationFinishRequest {
  registrationId: string;
  credential: any; // This will be the JSON representation of PublicKeyCredential
}

interface PasskeyLoginStartRequest {
  username?: string;
}

interface PasskeyLoginStartResponse {
  assertionId: string;
  options: any; // This will be parsed PublicKeyCredentialRequestOptions like structure
}

interface PasskeyLoginFinishRequest {
  assertionId: string;
  credential: any; // This will be the JSON representation of PublicKeyCredential
}

interface PasskeyAuthenticationResponse {
  token: string;
  username: string;
  message: string;
}


@Injectable({
  providedIn: 'root'
})
export class PasskeyAuthService {

  private apiUrl = `${environment.apiUrl}/passkey`; // Ensure environment.apiUrl is set up

  constructor(private http: HttpClient) {
    // Check if environment.apiUrl is defined
    if (!environment.apiUrl) {
        console.warn('environment.apiUrl is not defined. PasskeyAuthService API calls may fail.');
        this.apiUrl = `/api/v1/passkey`; // Fallback or ensure your proxy handles this
    }
  }

  private getHeaders(): HttpHeaders {
    // Add any standard headers if needed, e.g., for authorization if user is already partially logged in
    // For passkey registration/login start, usually no prior auth token is needed.
    return new HttpHeaders({
      'Content-Type': 'application/json'
    });
  }

  startRegistration(username: string, displayName?: string): Observable<PasskeyRegistrationStartResponse> {
    const payload: PasskeyRegistrationStartRequest = { username, displayName };
    return this.http.post<PasskeyRegistrationStartResponse>(`${this.apiUrl}/register/start`, payload, { headers: this.getHeaders() })
      .pipe(catchError(this.handleError));
  }

  finishRegistration(payload: PasskeyRegistrationFinishRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/register/finish`, payload, { headers: this.getHeaders() })
      .pipe(catchError(this.handleError));
  }

  startLogin(username?: string): Observable<PasskeyLoginStartResponse> {
    const payload: PasskeyLoginStartRequest = { username };
    // For GET-like start login (if username in query param or no body) or POST with optional body:
    // Adjust if your backend expects GET or POST with empty body for discoverable credentials
    return this.http.post<PasskeyLoginStartResponse>(`${this.apiUrl}/login/start`, username ? payload : {}, { headers: this.getHeaders() })
      .pipe(catchError(this.handleError));
  }

  finishLogin(payload: PasskeyLoginFinishRequest): Observable<PasskeyAuthenticationResponse> {
    return this.http.post<PasskeyAuthenticationResponse>(`${this.apiUrl}/login/finish`, payload, { headers: this.getHeaders() })
      .pipe(catchError(this.handleError));
  }

  private handleError(error: any): Observable<never> {
    console.error('PasskeyAuthService error:', error);
    // Customize error handling (e.g., user-friendly messages, logging)
    let errorMessage = 'An unknown error occurred with Passkey authentication.';
    if (error.error instanceof ErrorEvent) {
      // Client-side or network error
      errorMessage = `Error: ${error.error.message}`;
    } else if (error.status) {
      // Backend returned an unsuccessful response code
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message || error.error?.message || error.error}`;
      if (error.error && typeof error.error === 'object' && error.error.message) {
        errorMessage = error.error.message; // Use specific error message from backend if available
      } else if (typeof error.error === 'string') {
        errorMessage = error.error;
      }
    }
    return throwError(() => new Error(errorMessage));
  }
}
