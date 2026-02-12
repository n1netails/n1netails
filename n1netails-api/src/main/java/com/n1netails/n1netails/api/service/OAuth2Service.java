package com.n1netails.n1netails.api.service;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * Service for handling OAuth2 logins and registration for supported providers (GitHub, Google, etc.).
 *
 * <p>
 * Handles linking OAuth2 provider accounts to existing users or creating new users,
 * updating user metadata, and issuing JWT tokens for authenticated sessions.
 * </p>
 */
public interface OAuth2Service {

    /**
     * Logs in or registers a user via GitHub OAuth2.
     *
     * @param oAuth2User     the OAuth2 user object returned from GitHub
     * @param authentication the OAuth2 authentication token
     * @return a JWT token for the authenticated user
     * @throws RuntimeException if the default "n1netails" organization is not found or saving the user fails
     */
    String loginGithub(OAuth2User oAuth2User, OAuth2AuthenticationToken authentication);

    /**
     * Logs in or registers a user via Google OAuth2.
     *
     * @param oAuth2User     the OAuth2 user object returned from Google
     * @param authentication the OAuth2 authentication token
     * @return a JWT token for the authenticated user
     * @throws RuntimeException if the default "n1netails" organization is not found or saving the user fails
     */
    String loginGoogle(OAuth2User oAuth2User, OAuth2AuthenticationToken authentication);
}
