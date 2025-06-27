// Helper functions for WebAuthn browser API interactions

/**
 * Converts an ArrayBuffer to a Base64URL-encoded string.
 * @param buffer The ArrayBuffer to convert.
 * @returns A Base64URL-encoded string.
 */
export function arrayBufferToBase64Url(buffer: ArrayBuffer): string {
  const bytes = new Uint8Array(buffer);
  let str = '';
  for (const charCode of bytes) {
    str += String.fromCharCode(charCode);
  }
  const base64 = btoa(str);
  return base64.replace(/\+/g, '-').replace(/\//g, '_').replace(/=/g, '');
}

/**
 * Converts a Base64URL-encoded string to an ArrayBuffer.
 * @param base64Url The Base64URL-encoded string to convert.
 * @returns An ArrayBuffer.
 */
export function base64UrlToArrayBuffer(base64Url: string): ArrayBuffer {
  const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
  const padLength = (4 - (base64.length % 4)) % 4;
  const paddedBase64 = base64 + '='.repeat(padLength);
  const binaryStr = atob(paddedBase64);
  const buffer = new ArrayBuffer(binaryStr.length);
  const bytes = new Uint8Array(buffer);
  for (let i = 0; i < binaryStr.length; i++) {
    bytes[i] = binaryStr.charCodeAt(i);
  }
  return buffer;
}

/**
 * Prepares PublicKeyCredentialCreationOptions received from the server
 * for `navigator.credentials.create()`.
 * Specifically, it converts challenge and user.id from Base64URL to ArrayBuffer.
 * It also converts excludeCredentials[*].id from Base64URL to ArrayBuffer.
 * @param options The options from the server.
 * @returns The options ready for the browser API.
 */
export function prepareCreationOptions(options: any): PublicKeyCredentialCreationOptions {
  const preparedOptions = { ...options };

  preparedOptions.challenge = base64UrlToArrayBuffer(options.challenge);
  if (options.user && options.user.id) {
    preparedOptions.user.id = base64UrlToArrayBuffer(options.user.id);
  }

  if (options.excludeCredentials) {
    preparedOptions.excludeCredentials = options.excludeCredentials.map((cred: any) => ({
      ...cred,
      id: base64UrlToArrayBuffer(cred.id),
    }));
  }

  // Ensure pubKeyCredParams is correctly structured if not already
  if (options.pubKeyCredParams) {
    preparedOptions.pubKeyCredParams = options.pubKeyCredParams.map((param: any) => ({
        type: param.type as PublicKeyCredentialType,
        alg: param.alg as number
    }));
  }


  return preparedOptions as PublicKeyCredentialCreationOptions;
}

/**
 * Prepares PublicKeyCredentialRequestOptions received from the server
 * for `navigator.credentials.get()`.
 * Specifically, it converts challenge and allowCredentials[*].id from Base64URL to ArrayBuffer.
 * @param options The options from the server.
 * @returns The options ready for the browser API.
 */
export function prepareRequestOptions(options: any): PublicKeyCredentialRequestOptions {
  const preparedOptions = { ...options };
  preparedOptions.challenge = base64UrlToArrayBuffer(options.challenge);
  if (options.allowCredentials) {
    preparedOptions.allowCredentials = options.allowCredentials.map((cred: any) => ({
      ...cred,
      id: base64UrlToArrayBuffer(cred.id),
    }));
  }
  return preparedOptions as PublicKeyCredentialRequestOptions;
}

/**
 * Converts the browser's PublicKeyCredential object obtained from navigator.credentials.create()
 * into a JSON-serializable object to send to the server for finishing registration.
 * @param pubKeyCred The PublicKeyCredential object from the browser.
 * @returns A JSON-serializable object.
 */
export function publicKeyCredentialToJSON(pubKeyCred: PublicKeyCredential): any {
    if (pubKeyCred instanceof PublicKeyCredential && pubKeyCred.response instanceof AuthenticatorAttestationResponse) {
        return {
            id: pubKeyCred.id,
            rawId: arrayBufferToBase64Url(pubKeyCred.rawId),
            type: pubKeyCred.type,
            response: {
                clientDataJSON: arrayBufferToBase64Url(pubKeyCred.response.clientDataJSON),
                attestationObject: arrayBufferToBase64Url(pubKeyCred.response.attestationObject),
                // transports: pubKeyCred.response.getTransports ? pubKeyCred.response.getTransports() : [], // getTransports() might not be available on all types
            },
            clientExtensionResults: pubKeyCred.getClientExtensionResults(), // This should be JSON serializable
        };
    } else if (pubKeyCred instanceof PublicKeyCredential && pubKeyCred.response instanceof AuthenticatorAssertionResponse) {
         return {
            id: pubKeyCred.id,
            rawId: arrayBufferToBase64Url(pubKeyCred.rawId),
            type: pubKeyCred.type,
            response: {
                clientDataJSON: arrayBufferToBase64Url(pubKeyCred.response.clientDataJSON),
                authenticatorData: arrayBufferToBase64Url(pubKeyCred.response.authenticatorData),
                signature: arrayBufferToBase64Url(pubKeyCred.response.signature),
                userHandle: pubKeyCred.response.userHandle ? arrayBufferToBase64Url(pubKeyCred.response.userHandle) : null,
            },
            clientExtensionResults: pubKeyCred.getClientExtensionResults(),
        };
    }
    throw new Error('Invalid PublicKeyCredential type');
}

// Export interfaces for better type safety if not already globally available or defined elsewhere
// For example, if you don't have @types/web Earenabled or they are outdated:
/*
interface PublicKeyCredentialCreationOptionsJSON {
    rp: PublicKeyCredentialRpEntity;
    user: PublicKeyCredentialUserEntityJSON;
    challenge: string; // Base64URL
    pubKeyCredParams: PublicKeyCredentialParameters[];
    timeout?: number;
    excludeCredentials?: PublicKeyCredentialDescriptorJSON[];
    authenticatorSelection?: AuthenticatorSelectionCriteria;
    attestation?: AttestationConveyancePreference;
    extensions?: AuthenticationExtensionsClientInputs;
}

interface PublicKeyCredentialUserEntityJSON extends PublicKeyCredentialEntity {
    id: string; // Base64URL
    displayName: string;
}

interface PublicKeyCredentialDescriptorJSON {
    type: PublicKeyCredentialType;
    id: string; // Base64URL
    transports?: AuthenticatorTransport[];
}
*/

/* Example usage for parsing server response before navigator.credentials.create():
fetch('/start-registration', { method: 'POST', body: JSON.stringify({ username }), headers })
  .then(res => res.json())
  .then(optionsFromServer => {
    const createOptions = prepareCreationOptions(optionsFromServer.options); // Assuming optionsFromServer.options is the PublicKeyCredentialCreationOptions like object
    return navigator.credentials.create({ publicKey: createOptions });
  })
  .then(credential => {
    const credentialForServer = publicKeyCredentialToJSON(credential);
    return fetch('/finish-registration', { method: 'POST', body: JSON.stringify(credentialForServer), headers });
  });
*/
