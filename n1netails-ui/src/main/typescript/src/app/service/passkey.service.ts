import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, from, throwError } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { UiConfigService } from '../shared/ui-config.service';
import { PasskeyRegistrationStartRequestDto, PasskeyRegistrationStartResponseDto, PasskeyApiResponseDto, PasskeyAuthenticationStartRequestDto, PasskeyAuthenticationStartResponseDto, PasskeyAuthenticationResponseDto } from '../model/dto/passkey-dtos'; // Assuming a combined DTO file or individual imports
import { AuthenticationService } from './authentication.service'; // To handle JWT and user caching

// Helper function to convert base64url to ArrayBuffer
function base64urlToArrayBuffer(base64url: string): ArrayBuffer {
  const base64 = base64url.replace(/-/g, '+').replace(/_/g, '/');
  const pad = '='.repeat((4 - (base64.length % 4)) % 4);
  const base64Padded = base64 + pad;
  const binaryString = atob(base64Padded);
  const bytes = new Uint8Array(binaryString.length);
  for (let i = 0; i < binaryString.length; i++) {
    bytes[i] = binaryString.charCodeAt(i);
  }
  return bytes.buffer;
}

// Helper function to convert ArrayBuffer to base64url
function arrayBufferToBase64url(buffer: ArrayBuffer): string {
  let binary = '';
  const bytes = new Uint8Array(buffer);
  const len = bytes.byteLength;
  for (let i = 0; i < len; i++) {
    binary += String.fromCharCode(bytes[i]);
  }
  return window.btoa(binary).replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');
}

function base64urlDecode(input: string): string {
  // Replace base64url characters with base64 equivalents
  input = input.replace(/-/g, '+').replace(/_/g, '/');

  // Pad with '=' to make length a multiple of 4
  while (input.length % 4) {
    input += '=';
  }

  // Decode base64 to string
  return atob(input);
}


@Injectable({
  providedIn: 'root'
})
export class PasskeyService {
  private host: string;
  private apiUrl = '/ninetails/auth/passkey';
  private isWebAuthnSupported: boolean;

  constructor(
    private http: HttpClient,
    private uiConfigService: UiConfigService,
    private authService: AuthenticationService // For saving token/user on successful login
  ) {
    this.host = this.uiConfigService.getApiUrl();
    this.host = this.host + this.apiUrl;
    // Check if the functions exist, which implies WebAuthn support
    this.isWebAuthnSupported = typeof navigator.credentials !== 'undefined' &&
                               typeof navigator.credentials.create === 'function' &&
                               typeof navigator.credentials.get === 'function';
  }

  public checkWebAuthnSupport(): boolean {
    if (!this.isWebAuthnSupported) {
      console.warn('WebAuthn is not supported by this browser.');
    }
    return this.isWebAuthnSupported;
  }

  // --- REGISTRATION ---
  startPasskeyRegistration(email: string, domain: string): Observable<PasskeyRegistrationStartResponseDto> {
    if (!this.checkWebAuthnSupport()) {
      return throwError(() => new Error('Passkey authentication (WebAuthn) is not supported by this browser.'));
    }
    const request: PasskeyRegistrationStartRequestDto = { email, domain };
    return this.http.post<PasskeyRegistrationStartResponseDto>(`${this.host}/register/start`, request)
      .pipe(catchError(this.handleError));
  }

  finishPasskeyRegistration(
    flowId: string,
    credential: PublicKeyCredential,
    friendlyName?: string
  ): Observable<PasskeyApiResponseDto> {
    if (!this.checkWebAuthnSupport()) {
      return throwError(() => new Error('Passkey authentication (WebAuthn) is not supported by this browser.'));
    }

    const attestationResponse = credential.response as AuthenticatorAttestationResponse;

    const requestBody = {
      flowId: flowId,
      credential: {
        id: credential.id, // already base64url by browser — DO NOT re-encode
        rawId: arrayBufferToBase64url(credential.rawId), // must be encoded
        response: {
          attestationObject: arrayBufferToBase64url(attestationResponse.attestationObject), // must be encoded
          clientDataJSON: arrayBufferToBase64url(attestationResponse.clientDataJSON),       // must be encoded
          transports: attestationResponse.getTransports?.() ?? [],
        },
        type: credential.type,
        clientExtensionResults: credential.getClientExtensionResults(),
      },
      friendlyName: friendlyName,
    };

    return this.http.post<PasskeyApiResponseDto>(`${this.host}/register/finish`, requestBody);
  }

  // Wrapper for navigator.credentials.create()
  createPasskey(options: PublicKeyCredentialCreationOptions): Observable<PublicKeyCredential | null> {
    console.log("create passkey checking for web authn support");
    if (!this.checkWebAuthnSupport()) {
      return throwError(() => new Error('Passkey authentication (WebAuthn) is not supported by this browser.'));
    }

    console.log(" OPTIONS: ", options);

    // options.extensions.appidExclude
    // Need to convert challenge and user.id from base64url to ArrayBuffer
    const createOptions: any = {
      rp: options.rp,
      pubKeyCredParams: options.pubKeyCredParams,
      challenge: base64urlToArrayBuffer(
        typeof options.challenge === 'string'
          ? options.challenge
          : String(options.challenge)
      ),
      user: {
        ...options.user,
        id: typeof options.user.id === 'string'
          ? new TextEncoder().encode(base64urlDecode(options.user.id)).buffer
          : options.user.id,
      },
      // Ensure pubKeyCredParams if any are correctly formatted (usually fine)
      // Ensure excludeCredentials IDs are ArrayBuffer
      excludeCredentials: options.excludeCredentials?.map(exCred => ({
        ...exCred,
        id: base64urlToArrayBuffer(
          typeof exCred.id === 'string'
          ? exCred.id
          : String(exCred.id)
        
        ),
      })),
      extensions: options.extensions ? { ...options.extensions } : undefined
    };

    // Sanitize appidExclude if present
    if (createOptions.extensions && (createOptions.extensions as any)['appidExclude']) {
      try {
        new URL((createOptions.extensions as any)['appidExclude']);
      } catch {
        (createOptions.extensions as any)['appidExclude'] = window.location.origin;
        // (createOptions.extensions as any)['appidExclude'] = 'localhost';
      }
    }

    if (createOptions.extensions) {
      Object.keys(createOptions.extensions).forEach(key => {
        if (createOptions.extensions[key] == null) delete createOptions.extensions[key];
      });
    }

    console.log("CREATE OPTIONS: ", createOptions);

    console.log('challenge byteLength:', createOptions.challenge.byteLength);
    console.log('challenge instanceof ArrayBuffer:', createOptions.challenge instanceof ArrayBuffer);
    console.log('user.id instanceof Uint8Array:', createOptions.user.id instanceof Uint8Array);
    console.log('user.id: ', createOptions.user.id);

    console.log("NAVIGATOR CREDENTIALS CREATE");
    console.log('CREATE OPTIONS (final):', JSON.stringify(createOptions, null, 2));
    return from(navigator.credentials.create({ publicKey: createOptions }) as Promise<PublicKeyCredential | null>)
      .pipe(catchError(this.handleNavigatorError));
  }


  // --- AUTHENTICATION ---
  startPasskeyAuthentication(email?: string, domain?: string): Observable<PasskeyAuthenticationStartResponseDto> {
    if (!this.checkWebAuthnSupport()) {
      return throwError(() => new Error('Passkey authentication (WebAuthn) is not supported by this browser.'));
    }
    const request: PasskeyAuthenticationStartRequestDto = { email, domain: domain || window.location.hostname };
    return this.http.post<PasskeyAuthenticationStartResponseDto>(`${this.host}/login/start`, request)
      .pipe(catchError(this.handleError));
  }

  finishPasskeyAuthentication(flowId: string, credential: PublicKeyCredential): Observable<PasskeyAuthenticationResponseDto> {
    if (!this.checkWebAuthnSupport()) {
      return throwError(() => new Error('Passkey authentication (WebAuthn) is not supported by this browser.'));
    }
    const assertionResponse = credential.response as AuthenticatorAssertionResponse;

    const requestBody = {
      flowId: flowId,
      credential: {
        id: arrayBufferToBase64url(credential.rawId),
        rawId: arrayBufferToBase64url(credential.rawId),
        response: {
          authenticatorData: arrayBufferToBase64url(assertionResponse.authenticatorData),
          clientDataJSON: arrayBufferToBase64url(assertionResponse.clientDataJSON),
          signature: arrayBufferToBase64url(assertionResponse.signature),
          userHandle: assertionResponse.userHandle ? arrayBufferToBase64url(assertionResponse.userHandle) : null,
        },
        type: credential.type,
        clientExtensionResults: credential.getClientExtensionResults(),
      }
    };
    return this.http.post<PasskeyAuthenticationResponseDto>(`${this.host}/login/finish`, requestBody)
      .pipe(
        map(response => {
          if (response.success && response.jwtToken 
            && response.user
          ) {
            this.authService.saveToken(response.jwtToken);
            this.authService.addUserToLocalCache(response.user);
          }
          return response;
        })
      );
  }

  // Wrapper for navigator.credentials.get()
  getPasskey(options: PublicKeyCredentialRequestOptions): Observable<PublicKeyCredential | null> {
    console.log("get passkey");
    console.log("public key credential request options: ", options);
    
    if (!this.checkWebAuthnSupport()) {
      return throwError(() => new Error('Passkey authentication (WebAuthn) is not supported by this browser.'));
    }

    const getOptions: any = {
      // ...options,
      challenge: base64urlToArrayBuffer(options.challenge as unknown as string),
      allowCredentials: options.allowCredentials?.map((cred: any) => {
        return {
          type: cred.type,
          id: base64urlToArrayBuffer(cred.id),
          transports: Array.isArray(cred.transports) && cred.transports.every((t: any) => typeof t === 'string')
            ? cred.transports
            : undefined // skip transports if invalid
        };
      })
    };

    // Sanitize appid if present
    if (getOptions.extensions && (getOptions.extensions as any)['appid']) {
      try {
        // Throws if not a valid URL
        if (getOptions.extensions.appid) {
          new URL((options.extensions as any)['appid']);
        }
      } catch {
        // If invalid, set to window.location.origin (recommended) or remove
        (getOptions.extensions as any)['appid'] = window.location.origin;
      }
    }

    console.log("public key credential request get options: ", getOptions);
    return from(navigator.credentials.get({ publicKey: getOptions }) as Promise<PublicKeyCredential | null>)
      .pipe(
        catchError(this.handleNavigatorError)
      );

  }

  private handleError(error: HttpErrorResponse) {
    console.error('Backend API Error:', error);
    let errorMessage = 'An unknown error occurred with the server.';
    if (error.error instanceof ErrorEvent) {
      // Client-side or network error
      errorMessage = `Client error: ${error.error.message}`;
    } else if (error.status === 0) {
      errorMessage = 'Cannot connect to the server. Please check your network connection.';
    } else if (error.error && error.error.message) {
      // Backend error with a message
      errorMessage = error.error.message;
    } else if (error.statusText) {
      errorMessage = `Server error: ${error.status} ${error.statusText}`;
    }
    return throwError(() => new Error(errorMessage));
  }

  private handleNavigatorError(error: any) {
    console.error('Navigator Credentials Error:', error);
    let message = 'An unexpected error occurred during the passkey operation.';
    if (error instanceof DOMException) {
      switch (error.name) {
        case 'NotAllowedError':
          message = 'The passkey operation was cancelled or not allowed. Please try again. If you denied permission, you might need to reset it in your browser settings.';
          break;
        case 'UnknownError':
          message = 'An unknown error occurred with the authenticator. Please try a different passkey or contact support.';
          break;
        case 'InvalidStateError':
          message = 'The authenticator is in an invalid state for this operation (e.g., this passkey might already be registered for this user).';
          break;
        case 'SecurityError':
          message = 'A security error occurred. This might be due to an insecure connection (HTTPS required) or a policy violation.';
          break;
        case 'NotSupportedError':
           message = 'This operation is not supported by your authenticator or browser.';
           break;
        default:
          message = `Error during passkey operation: ${error.message} (${error.name})`;
      }
    } else if (error instanceof Error) {
      console.error('Not instance of DOMException', error.message);
      // message = error.message;
      message = `There was an error registering the passkey one might already exist for this account on your current device.`;
    }
    return throwError(() => new Error(message));
  }
}
