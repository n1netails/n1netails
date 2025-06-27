import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from '../../environments/environment';

// --- Interfaces for API communication ---
// Matches StartRegistrationRequest.java
export interface StartRegistrationRequest {
  email: string; // Changed from username
}

// Matches StartRegistrationResponse.java
export interface StartRegistrationResponse {
  publicKeyCredentialCreationOptionsJson: string; // JSON string of PublicKeyCredentialCreationOptions
}

// Matches FinishRegistrationRequest.java
export interface FinishRegistrationRequest {
  email: string; // Changed from username
  attestationResponseJson: string;
  clientExtensionsJson?: string;
  originalCreationOptionsJson: string;
}

// Matches StartAuthenticationRequest.java
export interface StartAuthenticationRequest {
  email?: string; // Changed from username, optional for discoverable credentials
}

// Matches StartAuthenticationResponse.java
export interface StartAuthenticationResponse {
  publicKeyCredentialRequestOptionsJson: string; // JSON string of PublicKeyCredentialRequestOptions
}

// Matches FinishAuthenticationRequest.java
export interface FinishAuthenticationRequest {
  assertionResponseJson: string;
  originalRequestOptionsJson: string;
}

// Matches AuthenticationSuccessResponse.java
export interface AuthenticationSuccessResponse {
  success: boolean;
  username: string;
  message: string;
  // token?: string;
}

// --- WebAuthn Helper Functions ---
// These are crucial for converting between ArrayBuffer (used by WebAuthn API)
// and Base64URL (often used for JSON transport).

/**
 * Converts a Base64URL string to an ArrayBuffer.
 */
function base64UrlToArrayBuffer(base64Url: string): ArrayBuffer {
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const binaryString = window.atob(base64);
  const len = binaryString.length;
  const bytes = new Uint8Array(len);
  for (let i = 0; i < len; i++) {
    bytes[i] = binaryString.charCodeAt(i);
  }
  return bytes.buffer;
}

/**
 * Converts an ArrayBuffer to a Base64URL string.
 */
function arrayBufferToBase64Url(buffer: ArrayBuffer): string {
  const bytes = new Uint8Array(buffer);
  let binary = '';
  for (let i = 0; i < bytes.byteLength; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return window.btoa(binary)
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=/g, '');
}


@Injectable({
  providedIn: 'root'
})
export class PasskeyService {
  // Updated apiUrl to match the new controller path
  private apiUrl = `${environment.apiUrl}/ninetails/auth/passkey`;

  constructor(private http: HttpClient) { }

  // --- Registration ---

  startRegistration(email: string): Observable<StartRegistrationResponse> {
    const requestBody: StartRegistrationRequest = { email };
    return this.http.post<StartRegistrationResponse>(`${this.apiUrl}/register/start`, requestBody)
      .pipe(catchError(this.handleError));
  }

  // finishRegistration still uses FinishRegistrationRequest which now contains email
  finishRegistration(requestBody: FinishRegistrationRequest): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/register/finish`, requestBody)
      .pipe(catchError(this.handleError));
  }

  // --- Authentication ---

  startAuthentication(email?: string): Observable<StartAuthenticationResponse> {
    const requestBody: StartAuthenticationRequest = { email };
    return this.http.post<StartAuthenticationResponse>(`${this.apiUrl}/login/start`, email ? requestBody : {})
      .pipe(catchError(this.handleError));
  }

  // finishAuthentication uses FinishAuthenticationRequest which doesn't need email directly
  // as user is identified by assertion
  finishAuthentication(requestBody: FinishAuthenticationRequest): Observable<AuthenticationSuccessResponse> {
    return this.http.post<AuthenticationSuccessResponse>(`${this.apiUrl}/login/finish`, requestBody)
      .pipe(catchError(this.handleError));
  }

  // --- WebAuthn Browser API Interaction Helpers ---
  // These methods will be called by components.

  async registerPasskey(email: string): Promise<any> {
    // 1. Call startRegistration to get options from the server
    const startResponse = await this.startRegistration(email).toPromise();
    if (!startResponse) {
      throw new Error('Failed to start passkey registration: No response from server.');
    }
    const creationOptions = JSON.parse(startResponse.publicKeyCredentialCreationOptionsJson);

    // 2. Convert challenge and user.id from Base64URL to ArrayBuffer for the WebAuthn API
    //    The `creationOptions.user.name` from server is the username, `creationOptions.user.displayName` is the display name.
    //    `creationOptions.user.id` is the userHandle (byte array as base64url).
    creationOptions.challenge = base64UrlToArrayBuffer(creationOptions.challenge);
    creationOptions.user.id = base64UrlToArrayBuffer(creationOptions.user.id);
    if (creationOptions.excludeCredentials) {
      creationOptions.excludeCredentials.forEach((cred: any) => {
        cred.id = base64UrlToArrayBuffer(cred.id);
      });
    }

    // 3. Call navigator.credentials.create()
    let credential;
    try {
      credential = await navigator.credentials.create({ publicKey: creationOptions }) as PublicKeyCredential;
    } catch (e) {
      console.error('navigator.credentials.create() error:', e);
      throw new Error(`Error creating credential: ${e.message || e}`);
    }

    if (!credential) {
        throw new Error('Credential creation failed: No credential returned from navigator.credentials.create().');
    }

    // 4. Prepare the response for the server: Convert ArrayBuffers back to Base64URL
    const attestationResponse = credential.response as AuthenticatorAttestationResponse;

    const finishRequest: FinishRegistrationRequest = {
      email, // Use email here
      originalCreationOptionsJson: startResponse.publicKeyCredentialCreationOptionsJson,
      attestationResponseJson: JSON.stringify({ // This structure might need to match server expectation more closely
        // Ensure this JSON structure for attestationResponseJson matches what the server's
        // ObjectMapper expects for AuthenticatorAttestationResponse.
        // Key names must match fields in Yubico's AuthenticatorAttestationResponse.
        // For Yubico library, it typically expects the raw `id`, `rawId`, `type`, `clientDataJSON`, `attestationObject`.
        // The structure I built below `attestationResponseForServer` is more robust.
        clientDataJSON: arrayBufferToBase64Url(attestationResponse.clientDataJSON),
        attestationObject: arrayBufferToBase64Url(attestationResponse.attestationObject),
        transports: (attestationResponse.getTransports && typeof attestationResponse.getTransports === 'function') ? attestationResponse.getTransports() : [],
        publicKeyAlgorithm: (attestationResponse as any).publicKeyAlgorithm, // Not standard, but some libs might use it
        publicKey: arrayBufferToBase64Url((attestationResponse as any).publicKey), // Not standard but some libs
        authenticatorData: arrayBufferToBase64Url((attestationResponse as any).authenticatorData) // Not standard but some libs might use it
      }),
      // clientExtensionsJson: credential.getClientExtensionResults() ? JSON.stringify(credential.getClientExtensionResults()) : undefined,
       clientExtensionsJson: JSON.stringify(credential.getClientExtensionResults()),
    };
     // More robust way to get response parts:
    const rawId = credential.rawId;
    const clientDataJSON = attestationResponse.clientDataJSON;
    const attestationObject = attestationResponse.attestationObject;

    const keyId = credential.id; // This is already base64url from the browser, but rawId is ArrayBuffer

    // Rebuild attestationResponseJson with correct conversions
    const attestationResponseForServer = {
        id: keyId, // The ID of the new credential, base64url encoded.
        rawId: arrayBufferToBase64Url(rawId), // The raw ID, for server to store.
        type: credential.type,
        clientDataJSON: arrayBufferToBase64Url(clientDataJSON),
        attestationObject: arrayBufferToBase64Url(attestationObject),
        // Include transports if available and needed by server logic (Yubico lib derives it)
        transports: (attestationResponse.getTransports && typeof attestationResponse.getTransports === 'function') ? attestationResponse.getTransports() : undefined,
    };


    finishRequest.attestationResponseJson = JSON.stringify(attestationResponseForServer);


    // 5. Call finishRegistration
    return this.finishRegistration(finishRequest).toPromise();
  }


  async loginWithPasskey(email?: string): Promise<AuthenticationSuccessResponse> {
    // 1. Call startAuthentication
    const startResponse = await this.startAuthentication(email).toPromise();
    if (!startResponse) {
      throw new Error('Failed to start passkey authentication: No response from server.');
    }
    const requestOptions = JSON.parse(startResponse.publicKeyCredentialRequestOptionsJson);

    // 2. Convert challenge and allowCredentials IDs from Base64URL to ArrayBuffer
    requestOptions.challenge = base64UrlToArrayBuffer(requestOptions.challenge);
    if (requestOptions.allowCredentials) {
      requestOptions.allowCredentials.forEach((cred: any) => {
        cred.id = base64UrlToArrayBuffer(cred.id);
      });
    }

    // 3. Call navigator.credentials.get()
    let assertion;
    try {
        assertion = await navigator.credentials.get({ publicKey: requestOptions }) as PublicKeyCredential;
    } catch (e) {
        console.error('navigator.credentials.get() error:', e);
        throw new Error(`Error getting credential: ${e.message || e}`);
    }

    if (!assertion) {
        throw new Error('Authentication failed: No assertion returned from navigator.credentials.get().');
    }

    // 4. Prepare the response for the server
    const assertionResponse = assertion.response as AuthenticatorAssertionResponse;
    const clientExtensions = assertion.getClientExtensionResults();

    // Build the assertionResponseForServer object carefully
    const assertionResponseForServer = {
        id: assertion.id, // Credential ID (base64url)
        rawId: arrayBufferToBase64Url(assertion.rawId), // Raw credential ID (ArrayBuffer to base64url)
        type: assertion.type,
        clientDataJSON: arrayBufferToBase64Url(assertionResponse.clientDataJSON),
        authenticatorData: arrayBufferToBase64Url(assertionResponse.authenticatorData),
        signature: arrayBufferToBase64Url(assertionResponse.signature),
        userHandle: assertionResponse.userHandle ? arrayBufferToBase64Url(assertionResponse.userHandle) : null,
    };

    const finishRequest: FinishAuthenticationRequest = {
      originalRequestOptionsJson: startResponse.publicKeyCredentialRequestOptionsJson,
      assertionResponseJson: JSON.stringify(assertionResponseForServer),
      // clientExtensionsJson: clientExtensions ? JSON.stringify(clientExtensions) : undefined, // If your server processes extensions
    };

    // 5. Call finishAuthentication
    return this.finishAuthentication(finishRequest).toPromise();
  }


  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An unknown error occurred!';
    if (error.error instanceof ErrorEvent) {
      // A client-side or network error occurred. Handle it accordingly.
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong.
      if (error.status === 0) {
        errorMessage = 'Could not connect to the server. Please check your network connection.';
      } else if (error.error && typeof error.error === 'string') {
        errorMessage = error.error; // If backend sends plain text error
      } else if (error.error && error.error.message) {
        errorMessage = `Error ${error.status}: ${error.error.message}`;
      } else if (error.message) {
        errorMessage = `Error ${error.status}: ${error.message}`;
      } else {
        errorMessage = `Error ${error.status}: ${error.statusText}`;
      }
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }

  // --- Base64URL and ArrayBuffer Helper Functions ---
  // Exposed publicly if components need them, or keep private if only used internally.
  // For now, keeping them as module-level functions. If they were methods of the class:
  // public base64UrlToArrayBuffer = base64UrlToArrayBuffer;
  // public arrayBufferToBase64Url = arrayBufferToBase64Url;
}
