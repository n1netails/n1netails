// Based on backend DTOs

// --- Registration ---
export interface PasskeyRegistrationStartRequestDto {
  email: string;
  domain: string;
}

// PublicKeyCredentialCreationOptions is a built-in browser type (lib.dom.d.ts)
// We expect the 'options' field from the backend to match this structure,
// but with some fields (challenge, user.id, excludeCredentials.id) as base64url strings
// that the client-side service will convert to ArrayBuffer before calling navigator.credentials.create()
export interface PasskeyRegistrationStartResponseDto {
  flowId: string;
  options: PublicKeyCredentialCreationOptions;
}

// PublicKeyCredential is a built-in browser type
// The service will transform its ArrayBuffer fields to base64url for this DTO
export interface PasskeyRegistrationFinishRequestDto {
  flowId: string;
  credential: { // This mirrors the structure expected by the backend
    id: string; // Base64URL encoded
    rawId: string; // Base64URL encoded
    response: {
      attestationObject: string; // Base64URL encoded
      clientDataJSON: string; // Base64URL encoded
      transports?: string[];
    };
    type: string; // e.g., "public-key"
    clientExtensionResults?: AuthenticationExtensionsClientOutputs;
  };
  friendlyName?: string;
}

// --- Authentication ---
export interface PasskeyAuthenticationStartRequestDto {
  email?: string;
  domain?: string;
}

// PublicKeyCredentialRequestOptions is a built-in browser type (lib.dom.d.ts)
// We expect the 'options' field from the backend to match this structure,
// but with some fields (challenge, allowCredentials.id) as base64url strings
// that the client-side service will convert to ArrayBuffer
export interface PasskeyAuthenticationStartResponseDto {
  flowId: string;
  options: PublicKeyCredentialRequestOptions;
  // options: any;
}

export interface PasskeyAuthenticationFinishRequestDto {
  flowId: string;
  credential: { // This mirrors the structure expected by the backend
    id: string; // Base64URL encoded
    rawId: string; // Base64URL encoded
    response: {
      authenticatorData: string; // Base64URL encoded
      clientDataJSON: string; // Base64URL encoded
      signature: string; // Base64URL encoded
      userHandle?: string | null; // Base64URL encoded
    };
    type: string; // e.g., "public-key"
    clientExtensionResults?: AuthenticationExtensionsClientOutputs;
  };
}

export interface PasskeyAuthenticationResponseDto {
  success: boolean;
  message: string;
  jwtToken?: string;
  user?: any; // Replace 'any' with your User model if available client-side
}

export interface PasskeyApiResponseDto {
  success: boolean;
  message: string;
}
