package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.TailStatusNotFoundException;
import com.n1netails.n1netails.api.model.core.TailStatus;
import com.n1netails.n1netails.api.model.request.PageRequest;
import com.n1netails.n1netails.api.model.response.TailStatusResponse;
import org.springframework.data.domain.Page;

/**
 * Service responsible for managing tail statuses.
 *
 * <p>This service allows retrieval, creation, update, and deletion of tail statuses.</p>
 */
public interface TailStatusService {

    /**
     * Retrieves a paginated list of tail statuses.
     *
     * <p>If the search term is provided, only statuses containing the search term
     * (case-insensitive) are returned.</p>
     *
     * @param request the page request, including page number, size, sort, and optional search term
     * @return a page of tail statuses; never {@code null}, may be empty
     */
    Page<TailStatusResponse> getTailStatusList(PageRequest request);

    /**
     * Retrieves a tail status by its ID.
     *
     * @param id the identifier of the tail status
     * @return the corresponding tail status
     * @throws IllegalArgumentException if a tail status with the given ID does not exist
     */
    TailStatusResponse getTailStatusById(Long id);

    /**
     * Creates a new tail status.
     *
     * @param request the tail status data
     * @return the created tail status
     * @throws IllegalArgumentException if a tail status with the same name already exists
     */
    TailStatusResponse createTailStatus(TailStatus request);

    /**
     * Updates an existing tail status.
     *
     * @param id      the ID of the tail status to update
     * @param request the updated tail status data
     * @return the updated tail status
     * @throws IllegalArgumentException if a tail status with the given ID does not exist,
     *                                  or if another tail status already exists with the given name
     */
    TailStatusResponse updateTailStatus(Long id, TailStatus request);


    /**
     * Deletes a tail status by its ID if it is deletable.
     *
     * @param id the ID of the tail status to delete
     * @throws TailStatusNotFoundException if a tail status with the given ID does not exist
     */
    void deleteTailStatus(Long id) throws TailStatusNotFoundException;
}
