package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.ai.llm.LlmService;
import com.n1netails.n1netails.api.ai.llm.LlmServiceFactory;
import com.n1netails.n1netails.api.exception.type.N1NoteAlreadyExistsException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.ai.LlmPromptRequest;
import com.n1netails.n1netails.api.model.ai.LlmPromptResponse;
import com.n1netails.n1netails.api.model.entity.NoteEntity;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.repository.NoteRepository;
import com.n1netails.n1netails.api.repository.TailRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.InvestigationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("investigationService")
public class InvestigationServiceImpl implements InvestigationService {

    private final LlmServiceFactory llmServiceFactory;
    private final TailRepository tailRepository;
    private final NoteRepository noteRepository;
    private final UserRepository userRepository;

    @Override
    public LlmPromptResponse investigateWithLlm(LlmPromptRequest llmRequest) throws TailNotFoundException, UserNotFoundException, N1NoteAlreadyExistsException {

        LlmService llmService = this.llmServiceFactory.get(llmRequest.getProvider());
        TailEntity tailEntity = this.tailRepository.findById(llmRequest.getTailId())
                .orElseThrow(() -> new TailNotFoundException("The requested tail does not exist."));

        StringBuilder metadataBuilder = new StringBuilder();
        tailEntity.getCustomVariables().forEach(tailVariableEntity ->
                metadataBuilder
                        .append(tailVariableEntity.getKey())
                        .append(": ")
                        .append(tailVariableEntity.getValue())
                        .append("\n")
        );
        String metadata = metadataBuilder.toString();

        String prompt = String.format("""
        Title: %s
        Description: %s
        Details: %s
        Metadata: %s

        Prompt: %s
        """,
                nullToNA(tailEntity.getTitle()),
                nullToNA(tailEntity.getDescription()),
                nullToNA(tailEntity.getDetails()),
                nullToNA(metadata),
                nullToNA(llmRequest.getPrompt())
        );

        String promptResult = llmService.completePrompt(llmRequest.getModel(), prompt);

        LlmPromptResponse llmResponse = new LlmPromptResponse();
        llmResponse.setCompletion(promptResult);
        llmResponse.setTailId(tailEntity.getId());
        llmResponse.setProvider("openai");
        llmResponse.setModel(llmRequest.getModel());
        llmResponse.setUserId(llmRequest.getUserId());
        llmResponse.setOrganizationId(llmRequest.getOrganizationId());

        saveN1Note(llmResponse, tailEntity);

        return llmResponse;
    }

    private String nullToNA(String value) {
        return (value == null || value.trim().isEmpty()) ? "N/A" : value;
    }

    private void saveN1Note(LlmPromptResponse llmPromptResponse, TailEntity tailEntity) throws UserNotFoundException, N1NoteAlreadyExistsException {

        Optional<NoteEntity> optionalNoteEntity = this.noteRepository.findFirstByTailIdAndN1IsTrueOrderByCreatedAtDesc(tailEntity.getId());
        if (optionalNoteEntity.isPresent()) throw new N1NoteAlreadyExistsException("Unable to create n1 note as one exists already.");

        UsersEntity user = this.userRepository.findById(tailEntity.getAssignedUserId())
                .orElseThrow(() -> new UserNotFoundException("User who requested to add new note does not exist."));

        NoteEntity noteEntity = new NoteEntity();
        noteEntity.setTail(tailEntity);
        noteEntity.setUser(user);
        noteEntity.setContent(llmPromptResponse.getCompletion());
        noteEntity.setCreatedAt(Instant.now());
        noteEntity.setHuman(false);
        noteEntity.setN1(true);
        noteEntity.setLlmProvider("openai");
        noteEntity.setLlmModel(llmPromptResponse.getModel());
        noteEntity.setOrganization(tailEntity.getOrganization());

        // save note
        log.info("Saving n1 note");
        this.noteRepository.save(noteEntity);
    }
}
