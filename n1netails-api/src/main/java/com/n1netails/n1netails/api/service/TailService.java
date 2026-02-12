package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.*;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.dto.TailSummary;
import com.n1netails.n1netails.api.model.request.ResolveTailRequest;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
import com.n1netails.n1netails.api.model.response.TailResponse;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service responsible for managing tail alerts and related operations.
 *
 * <p>This service provides read and write operations for tails, including
 * retrieval, status updates, resolution workflows, and filtered queries.</p>
 *
 * <p>All authorization checks are enforced by implementations.
 * Callers can assume that returned data is already access-controlled.</p>
 *
 */
public interface TailService {

    /**
     * Retrieves a paginated list of tails matching the given filters.
     *
     * @param request the pagination and filter request
     * @param currentUser the authenticated user
     * @return a page of tail responses; may be empty if no tails match
     * @throws TailStatusNotFoundException if a requested status filter does not exist
     * @throws TailTypeNotFoundException if a requested type filter does not exist
     * @throws TailLevelNotFoundException if a requested level filter does not exist
     */
    Page<TailResponse> getTails(TailPageRequest request, UserPrincipal currentUser) throws TailStatusNotFoundException, TailTypeNotFoundException, TailLevelNotFoundException;

    /**
     * Resolves tail level filters from the given request.
     *
     * @param request the tail page request
     * @return a list of level names; never {@code null}, may be empty
     * @throws TailLevelNotFoundException if a requested level does not exist
     */
    List<String> getTailLevels(TailPageRequest request) throws TailLevelNotFoundException;

    /**
     * Resolves tail type filters from the given request.
     *
     * @param request the tail page request
     * @return a list of type names; never {@code null}, may be empty
     * @throws TailTypeNotFoundException if a requested type does not exist
     */
    List<String> getTailTypes(TailPageRequest request) throws TailTypeNotFoundException;

    /**
     * Resolves tail status filters from the given request.
     *
     * @param request the tail page request
     * @return a list of status names; never {@code null}, may be empty
     * @throws TailStatusNotFoundException if a requested status does not exist
     */
    List<String> getTailStatuses(TailPageRequest request) throws TailStatusNotFoundException;

    /**
     * Retrieves the 9 most recent tails visible to the current user.
     *
     * @param currentUser the authenticated user
     * @return a list of tail responses; may be empty if no tails exist
     */
    List<TailResponse> getTop9NewestTails(UserPrincipal currentUser);

    /**
     * Retrieves a tail by its identifier.
     *
     * @param id the identifier of the tail
     * @param currentUser the authenticated user
     * @return the corresponding tail response
     * @throws TailNotFoundException if the tail does not exist
     * @throws UnauthorizedException if the user is not authorized to access the tail
     */
    TailResponse getTailById(Long id, UserPrincipal currentUser) throws TailNotFoundException, UnauthorizedException;

    /**
     * Updates the status of a tail and optionally attaches a note.
     *
     * @param request the status update request
     * @param currentUser the authenticated user performing the update
     * @return the updated tail response
     * @throws TailNotFoundException if the tail does not exist
     * @throws UnauthorizedException if the user is not authorized to update the tail
     */
    TailResponse updateStatus(ResolveTailRequest request, UserPrincipal currentUser) throws TailNotFoundException, UnauthorizedException;

    /**
     * Marks a specific tail as resolved and optionally attaches a note.
     *
     * @param request the resolution request
     * @param currentUser the authenticated user performing the action
     * @throws TailNotFoundException if the tail does not exist
     * @throws TailStatusNotFoundException if the {@code RESOLVED} status does not exist
     * @throws UnauthorizedException if the user is not authorized to resolve the tail
     */
    void markResolved(ResolveTailRequest request, UserPrincipal currentUser) throws TailNotFoundException, TailStatusNotFoundException, UnauthorizedException;

    /**
     * Marks all {@code NEW} tails assigned to the current user as {@code RESOLVED}.
     *
     * @param currentUser the authenticated user
     * @throws TailStatusNotFoundException if required tail statuses are missing
     */
    void resolveAll(UserPrincipal currentUser) throws TailStatusNotFoundException;

    /**
     * Counts the number of {@code NEW} tails assigned to the current user.
     *
     * @param currentUser the authenticated user
     * @return the number of new tails (zero if none exist)
     */
    long countNewTails(UserPrincipal currentUser);

    /**
     * Maps a tail summary projection to a tail response.
     *
     * @param tailSummary the tail summary projection
     * @return the corresponding tail response
     */
    TailResponse setTailSummaryResponse(TailSummary tailSummary);
}
