package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.N1neTokenGenerateException;
import com.n1netails.n1netails.api.exception.type.N1neTokenNotFoundException;
import com.n1netails.n1netails.api.model.request.CreateTokenRequest;
import com.n1netails.n1netails.api.model.request.PageRequest;
import com.n1netails.n1netails.api.model.response.N1neTokenResponse;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service responsible for managing N1ne API tokens.
 *
 * <p>
 * This service handles token creation, validation, lifecycle
 * management, and retrieval. Tokens are stored as secure hashes
 * and the plain token value is only exposed at creation time.
 * </p>
 */
public interface N1neTokenService {

    /**
     * Creates a new API token for a user within an organization.
     *
     * <p>
     * The user must belong to the specified organization.
     * A cryptographically secure token is generated, stored
     * as a hash, and returned once in plain form.
     * </p>
     *
     * @param createTokenRequest request containing user, organization,
     *                           expiration, and token metadata
     * @return the created token response including the plain token value
     * @throws N1neTokenGenerateException if token generation fails
     * @throws IllegalArgumentException   if the user or organization
     *                                    does not exist or the user
     *                                    is not part of the organization
     */
    N1neTokenResponse create(CreateTokenRequest createTokenRequest) throws N1neTokenGenerateException;

    /**
     * Retrieves a token by its identifier.
     *
     * <p>
     * The returned response does not include the plain token value.
     * </p>
     *
     * @param id the token identifier
     * @return the token metadata
     * @throws IllegalArgumentException if the token does not exist
     */
    N1neTokenResponse getById(Long id);

    /**
     * Retrieves all tokens associated with a specific user.
     *
     * @param userId      the user identifier
     * @param pageRequest pagination and sorting configuration
     * @return a paginated list of token responses
     */
    Page<N1neTokenResponse> getAllByUserId(Long userId, PageRequest pageRequest);

    /**
     * Revokes a token, preventing further use.
     *
     * @param id the token identifier
     * @throws IllegalArgumentException if the token does not exist
     */
    void revoke(Long id);

    /**
     * Enables a previously revoked token.
     *
     * @param id the token identifier
     * @throws IllegalArgumentException if the token does not exist
     */
    void enable(Long id);

    /**
     * Permanently deletes a token.
     *
     * @param id the token identifier
     */
    void delete(Long id);

    /**
     * Validates a token against stored hashes and lifecycle rules.
     *
     * <p>
     * A token is considered valid if it:
     * <ul>
     *   <li>Exists</li>
     *   <li>Is not revoked</li>
     *   <li>Is not expired</li>
     * </ul>
     * </p>
     *
     * @param n1neToken the plain token value
     * @return {@code true} if the token is valid; {@code false} otherwise
     * @throws N1neTokenGenerateException if hashing fails
     */
    boolean validateToken(String n1neToken) throws N1neTokenGenerateException;

    /**
     * Updates the last-used timestamp for a token.
     *
     * <p>
     * This method is typically called after successful token
     * authentication.
     * </p>
     *
     * @param n1neToken the plain token value
     * @throws N1neTokenNotFoundException if the token does not exist
     * @throws N1neTokenGenerateException if hashing fails
     */
    void setLastUsedAt(String n1neToken) throws N1neTokenNotFoundException, N1neTokenGenerateException;
}
