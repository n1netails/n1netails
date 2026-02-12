package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.N1NoteAlreadyExistsException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.ai.LlmPromptRequest;
import com.n1netails.n1netails.api.model.ai.LlmPromptResponse;

/**
 * Service responsible for performing LLM-based investigations on tails.
 *
 * <p>
 * This service manages the process of building prompts, calling the AI (LLM),
 * and saving the investigation results as notes linked to a specific trail
 * </p>
 *
 * <p>
 * Investigations may produce an initial system-generated (N1) note
 * or subsequent follow-up notes depending on existing context.
 * </p>
 */
public interface InvestigationService {

    /**
     * Performs an initial LLM-based investigation for a tail.
     *
     * <p>
     * The investigation prompt is constructed using the tail's
     * title, description, details, metadata, and the provided
     * user prompt. The final AI response is stored as a
     * system-generated (N1) note.
     * </p>
     *
     * @param llmRequest the request containing tail, user, model,
     *                   provider, and prompt details
     * @return the LLM completion result associated with the tail
     * @throws TailNotFoundException if the referenced tail does not exist
     * @throws UserNotFoundException if the associated user cannot be resolved
     * @throws N1NoteAlreadyExistsException if an initial N1 note already exists
     */
    LlmPromptResponse investigateWithLlm(LlmPromptRequest llmRequest) throws TailNotFoundException, UserNotFoundException, N1NoteAlreadyExistsException;

    /**
     * Uses the LLM and previous notes to update or add information to an existing trail.
     *
     * <p>
     * If no initial system-generated (N1) note exists, this method
     * delegates to {@link #investigateWithLlm(LlmPromptRequest)}.
     * Otherwise, the system adds recent notes to the prompt
     * so the AI understands the history of the conversation
     * </p>
     *
     * @param llmRequest the request containing tail, user, model,
     *                   provider, and prompt details
     * @return the LLM completion result associated with the tail
     * @throws TailNotFoundException if the referenced tail does not exist
     * @throws UserNotFoundException if the associated user cannot be resolved
     * @throws N1NoteAlreadyExistsException if an invalid N1 note state is detected
     */
    LlmPromptResponse promptTail(LlmPromptRequest llmRequest) throws TailNotFoundException, UserNotFoundException, N1NoteAlreadyExistsException;
}
