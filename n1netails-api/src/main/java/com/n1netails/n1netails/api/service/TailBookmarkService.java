package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.*;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.TailBookmarkEntity;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
import com.n1netails.n1netails.api.model.response.TailResponse;
import org.springframework.data.domain.Page;

/**
 * Service responsible for managing user bookmarks on tails.
 *
 * <p>Provides operations to bookmark and unbookmark tails,
 * query bookmarked tails for a user, and check bookmark status.</p>
 */
public interface TailBookmarkService {

    /**
     * Creates a bookmark for a tail on behalf of a user.
     *
     * @param userId the identifier of the user bookmarking the tail
     * @param tailId the identifier of the tail to bookmark
     * @return the created bookmark entity
     * @throws UserNotFoundException if the user does not exist
     * @throws TailNotFoundException if the tail does not exist
     * @throws TailAlreadyBookmarkedException if the tail is already bookmarked by the user
     */
    TailBookmarkEntity bookmarkTail(Long userId, Long tailId) throws UserNotFoundException, TailNotFoundException, TailAlreadyBookmarkedException;

    /**
     * Removes a bookmark for a given user and tail.
     *
     * <p>If no bookmark exists, the operation completes without error.</p>
     *
     * @param userId the identifier of the user
     * @param tailId the identifier of the tail
     */
    void removeBookmark(Long userId, Long tailId);

    /**
     * Retrieves a paginated list of tails bookmarked by the current user,
     * optionally filtered by search term and tail attributes.
     *
     * @param request pagination and filter criteria
     * @param currentUser the authenticated user principal
     * @return a page of bookmarked tail responses
     * @throws TailTypeNotFoundException if a requested tail type filter is invalid
     * @throws TailLevelNotFoundException if a requested tail level filter is invalid
     * @throws TailStatusNotFoundException if a requested tail status filter is invalid
     */
    Page<TailResponse> getUserBookmarks(TailPageRequest request, UserPrincipal currentUser) throws TailTypeNotFoundException, TailLevelNotFoundException, TailStatusNotFoundException;

    /**
     * Determines whether a tail is bookmarked by a specific user.
     *
     * @param userId the identifier of the user
     * @param tailId the identifier of the tail
     * @return {@code true} if the tail is bookmarked by the user, {@code false} otherwise
     */
    boolean isBookmarked(Long userId, Long tailId);
}
