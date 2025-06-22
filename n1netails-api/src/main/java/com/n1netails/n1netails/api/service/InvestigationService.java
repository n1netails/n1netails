package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.N1NoteAlreadyExistsException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.ai.LlmPromptRequest;
import com.n1netails.n1netails.api.model.ai.LlmPromptResponse;

public interface InvestigationService {

    LlmPromptResponse investigateWithLlm(LlmPromptRequest llmRequest) throws TailNotFoundException, UserNotFoundException, N1NoteAlreadyExistsException;
}
