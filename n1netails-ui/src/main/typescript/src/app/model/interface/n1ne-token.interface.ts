interface N1neToken {
  name?: string;
  token: string;
  createdAt: Date;
  expiresAt: Date;
  revoked: boolean;
  lastUsedAt?: Date;
}

interface CreateTokenRequest extends N1neToken {
  userId: number;
  organizationId?: number;
}

interface N1neTokenResponse extends N1neToken {
  id: number;
  userId: number;
  organizationId?: number;
}
