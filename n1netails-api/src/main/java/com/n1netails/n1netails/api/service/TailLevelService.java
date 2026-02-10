package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.TailLevelNotFoundException;
import com.n1netails.n1netails.api.model.core.TailLevel;
import com.n1netails.n1netails.api.model.request.PageRequest;
import com.n1netails.n1netails.api.model.response.TailLevelResponse;
import org.springframework.data.domain.Page;

/**
 * Service responsible for managing tail levels.
 *
 * <p>Provides operations to create, retrieve, update, and delete tail levels,
 * as well as paginated retrieval with optional search support.</p>
 */
public interface TailLevelService {

    /**
     * Retrieves a paginated list of tail levels.
     *
     * <p>The result may be optionally filtered using a search term.</p>
     *
     * @param request pagination, sorting, and optional search criteria
     * @return a page of tail level responses
     */
    Page<TailLevelResponse> getTailLevels(PageRequest request);

    /**
     * Retrieves a tail level by its identifier.
     *
     * @param id the identifier of the tail level
     * @return the corresponding tail level response
     * @throws IllegalArgumentException if the tail level does not exist
     */
    TailLevelResponse getTailLevelById(Long id);

    /**
     * Creates a new tail level.
     *
     * @param request the tail level creation request
     * @return the created tail level response
     * @throws IllegalArgumentException if a tail level with the same name already exists
     */
    TailLevelResponse createTailLevel(TailLevel request);

    /**
     * Updates an existing tail level.
     *
     * @param id the identifier of the tail level to update
     * @param request the updated tail level data
     * @return the updated tail level response
     * @throws IllegalArgumentException if the tail level does not exist
     * @throws IllegalArgumentException if another tail level with the same name already exists
     */
    TailLevelResponse updateTailLevel(Long id, TailLevel request);

    /**
     * Deletes a tail level by its identifier.
     *
     * <p>Only tail levels marked as deletable may be removed.</p>
     *
     * @param id the identifier of the tail level
     * @throws TailLevelNotFoundException if the tail level does not exist
     */
    void deleteTailLevel(Long id) throws TailLevelNotFoundException;
}
