package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.TailTypeNotFoundException;
import com.n1netails.n1netails.api.model.core.TailType;
import com.n1netails.n1netails.api.model.request.PageRequest;
import com.n1netails.n1netails.api.model.response.TailTypeResponse;
import org.springframework.data.domain.Page;

/**
 * Service responsible for managing tail types.
 *
 * <p>This service provides methods for retrieving, creating, updating, and deleting tail types.</p>
 */
public interface TailTypeService {

    /**
     * Retrieves a paginated list of tail types.
     *
     * <p>If the search term is provided, only types containing the search term
     * (case-insensitive) are returned.</p>
     *
     * @param request the page request containing page number, size, sort, and optional search term
     * @return a page of tail types; never {@code null}, may be empty
     */
    Page<TailTypeResponse> getTailTypes(PageRequest request);

    /**
     * Retrieves a tail type by its ID.
     *
     * @param id the identifier of the tail type
     * @return the corresponding tail type
     * @throws IllegalArgumentException if a tail type with the given ID does not exist
     */
    TailTypeResponse getTailTypeById(Long id);

    /**
     * Creates a new tail type.
     *
     * @param request the tail type data to create
     * @return the created tail type
     * @throws IllegalArgumentException if a tail type with the same name already exists
     */

    TailTypeResponse createTailType(TailType request);

    /**
     * Updates an existing tail type.
     *
     * @param id      the ID of the tail type to update
     * @param request the updated tail type data
     * @return the updated tail type
     * @throws IllegalArgumentException if a tail type with the given ID does not exist,
     *                                  or if another tail type already exists with the same name
     */
    TailTypeResponse updateTailType(Long id, TailType request);

    /**
     * Deletes a tail type by its ID if it is deletable.
     *
     * @param id the ID of the tail type to delete
     * @throws TailTypeNotFoundException if a tail type with the given ID does not exist
     */
    void deleteTailType(Long id) throws TailTypeNotFoundException;
}
