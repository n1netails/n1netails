import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { N1neTokenService, CreateTokenRequest, N1neTokenResponse } from './n1ne-token.service';

describe('N1neTokenService', () => {
  let service: N1neTokenService;
  let httpMock: HttpTestingController;
  const apiUrl = '/api/n1ne-token';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [N1neTokenService]
    });
    service = TestBed.inject(N1neTokenService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Make sure that there are no outstanding requests
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('createToken', () => {
    it('should send a POST request to create a token and return the created token', () => {
      const mockRequest: CreateTokenRequest = { userId: 1, organizationId: 1, name: 'Test Token', expiresAt: new Date().toISOString() };
      const mockResponse: N1neTokenResponse = { id: 1, ...mockRequest, lastUsedAt: '', token: 'mockTokenValue', createdAt: new Date().toISOString(), revoked: false };

      service.createToken(mockRequest).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(mockRequest);
      req.flush(mockResponse);
    });

    it('should handle errors when creating a token', () => {
      const mockRequest: CreateTokenRequest = { userId: 1, name: 'Test Error Token' };
      const errorMessage = 'Failed to create token';

      service.createToken(mockRequest).subscribe({
        next: () => fail('should have failed with an error'),
        error: (error) => {
          expect(error.status).toBe(500);
          expect(error.statusText).toBe('Server Error');
        }
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('POST');
      req.flush({ message: errorMessage }, { status: 500, statusText: 'Server Error' });
    });
  });

  describe('getAllTokens', () => {
    it('should send a GET request to retrieve all tokens', () => {
      const mockResponse: N1neTokenResponse[] = [
        { id: 1, userId: 1, organizationId: 1, name: 'Token 1', lastUsedAt: '', token: 'token1', createdAt: new Date().toISOString(), expiresAt: new Date().toISOString(), revoked: false },
        { id: 2, userId: 1, organizationId: 1, name: 'Token 2', lastUsedAt: '', token: 'token2', createdAt: new Date().toISOString(), expiresAt: new Date().toISOString(), revoked: true },
      ];

      service.getAllTokens().subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should handle errors when getting all tokens', () => {
      service.getAllTokens().subscribe({
        next: () => fail('should have failed with an error'),
        error: (error) => {
          expect(error.status).toBe(500);
          expect(error.statusText).toBe('Server Error');
        }
      });

      const req = httpMock.expectOne(apiUrl);
      expect(req.request.method).toBe('GET');
      req.flush({ message: 'Error fetching tokens' }, { status: 500, statusText: 'Server Error' });
    });
  });

  describe('getTokenById', () => {
    it('should send a GET request to retrieve a token by ID', () => {
      const tokenId = 1;
      const mockResponse: N1neTokenResponse = { id: tokenId, userId: 1, organizationId: 1, name: 'Token 1', lastUsedAt: '', token: 'token1', createdAt: new Date().toISOString(), expiresAt: new Date().toISOString(), revoked: false };

      service.getTokenById(tokenId).subscribe(response => {
        expect(response).toEqual(mockResponse);
      });

      const req = httpMock.expectOne(`${apiUrl}/${tokenId}`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResponse);
    });

    it('should handle errors when getting a token by ID', () => {
      const tokenId = 1;
      service.getTokenById(tokenId).subscribe({
        next: () => fail('should have failed with an error'),
        error: (error) => {
          expect(error.status).toBe(404);
          expect(error.statusText).toBe('Not Found');
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${tokenId}`);
      expect(req.request.method).toBe('GET');
      req.flush({ message: 'Token not found' }, { status: 404, statusText: 'Not Found' });
    });
  });

  describe('revokeToken', () => {
    it('should send a PUT request to revoke a token', () => {
      const tokenId = 1;
      service.revokeToken(tokenId).subscribe(response => {
        expect(response).toBeNull(); // Or whatever the service returns on success (void -> null)
      });

      const req = httpMock.expectOne(`${apiUrl}/${tokenId}/revoke`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual({}); // Assuming empty body
      req.flush(null); // For void response
    });

    it('should handle errors when revoking a token', () => {
      const tokenId = 1;
      service.revokeToken(tokenId).subscribe({
        next: () => fail('should have failed with an error'),
        error: (error) => {
          expect(error.status).toBe(500);
          expect(error.statusText).toBe('Server Error');
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${tokenId}/revoke`);
      expect(req.request.method).toBe('PUT');
      req.flush({ message: 'Error revoking token' }, { status: 500, statusText: 'Server Error' });
    });
  });

  describe('enableToken', () => {
    it('should send a PUT request to enable a token', () => {
      const tokenId = 1;
      service.enableToken(tokenId).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`${apiUrl}/${tokenId}/enable`);
      expect(req.request.method).toBe('PUT');
      expect(req.request.body).toEqual({});
      req.flush(null);
    });

    it('should handle errors when enabling a token', () => {
      const tokenId = 1;
      service.enableToken(tokenId).subscribe({
        next: () => fail('should have failed with an error'),
        error: (error) => {
          expect(error.status).toBe(500);
          expect(error.statusText).toBe('Server Error');
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${tokenId}/enable`);
      expect(req.request.method).toBe('PUT');
      req.flush({ message: 'Error enabling token' }, { status: 500, statusText: 'Server Error' });
    });
  });

  describe('deleteToken', () => {
    it('should send a DELETE request to delete a token', () => {
      const tokenId = 1;
      service.deleteToken(tokenId).subscribe(response => {
        expect(response).toBeNull();
      });

      const req = httpMock.expectOne(`${apiUrl}/${tokenId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush(null);
    });

    it('should handle errors when deleting a token', () => {
      const tokenId = 1;
      service.deleteToken(tokenId).subscribe({
        next: () => fail('should have failed with an error'),
        error: (error) => {
          expect(error.status).toBe(500);
          expect(error.statusText).toBe('Server Error');
        }
      });

      const req = httpMock.expectOne(`${apiUrl}/${tokenId}`);
      expect(req.request.method).toBe('DELETE');
      req.flush({ message: 'Error deleting token' }, { status: 500, statusText: 'Server Error' });
    });
  });
});
