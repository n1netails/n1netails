package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.N1NoteAlreadyExistsException;
import com.n1netails.n1netails.api.exception.type.NoteNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.dto.Note;
import com.n1netails.n1netails.api.model.request.NotePageRequest;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Service responsible for managing notes associated with tails.
 *
 * <p>
 * Supports creating notes, retrieving by ID or tail,
 * pagination, and fetching system-generated (N1) notes.
 * </p>
 */
public interface NoteService {

    /**
     * Adds a new note to a tail.
     *
     * <p>
     * If the note is flagged as an N1 note, ensures that no other
     * N1 note exists for the same tail.
     * </p>
     *
     * @param note the note to add
     * @return the persisted note with ID and username populated
     * @throws TailNotFoundException if the associated tail does not exist
     * @throws UserNotFoundException if the associated user does not exist
     * @throws N1NoteAlreadyExistsException if an N1 note already exists for the tail
     */
    Note add(Note note) throws TailNotFoundException, UserNotFoundException, N1NoteAlreadyExistsException;

    /**
     * Retrieves a note by its identifier.
     *
     * @param id the note identifier
     * @return the note
     * @throws NoteNotFoundException if the note does not exist
     */
    Note getById(Long id) throws NoteNotFoundException;

    /**
     * Retrieves all notes associated with a specific tail.
     *
     * <p>
     * Returns the notes in ascending order of creation date.
     * If no notes exist for the specified tail, an empty list is returned.
     * The returned list is never {@code null}.
     * </p>
     *
     * @param tailId the identifier of the tail
     * @return a list of notes; may be empty if no notes exist
     */
    List<Note> getAllByTailId(Long tailId);

    /**
     * Retrieves the last 9 notes for a tail.
     *
     * @param tailId the tail identifier
     * @return the last 9 notes, ordered by creation date descending
     */
    List<Note> getLast9NotesByTailId(Long tailId);

    /**
     * Retrieves paginated notes for a tail.
     *
     * @param request the pagination and tail information
     * @return a page of notes
     */
    Page<Note> getNotesByTailId(NotePageRequest request);

    /**
     * Retrieves the system-generated (N1) note for a tail.
     *
     * @param tailId the tail identifier
     * @return the N1 note
     * @throws NoteNotFoundException if no N1 note exists for the tail
     */
    Note getIsN1ByTailId(Long tailId) throws NoteNotFoundException;

}
