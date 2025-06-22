package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.ai.llm.LlmService;
import com.n1netails.n1netails.api.ai.llm.LlmServiceFactory;
import com.n1netails.n1netails.api.exception.type.N1NoteAlreadyExistsException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.ai.LlmPromptRequest;
import com.n1netails.n1netails.api.model.ai.LlmPromptResponse;
import com.n1netails.n1netails.api.model.ai.openai.request.PromptInput;
import com.n1netails.n1netails.api.model.entity.NoteEntity;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.repository.NoteRepository;
import com.n1netails.n1netails.api.repository.TailRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.InvestigationService;
import com.n1netails.n1netails.api.service.NoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("investigationService")
public class InvestigationServiceImpl implements InvestigationService {

    private final LlmServiceFactory llmServiceFactory;
    private final TailRepository tailRepository;
    private final NoteRepository noteRepository;
    private final NoteService noteService;
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

    @Override
    public LlmPromptResponse promptTail(LlmPromptRequest llmRequest) throws TailNotFoundException, UserNotFoundException, N1NoteAlreadyExistsException {
        log.info("PROMPT TAIL");
        LlmService llmService = this.llmServiceFactory.get(llmRequest.getProvider());
        log.info("retrieving tail entity");
        TailEntity tailEntity = this.tailRepository.findById(llmRequest.getTailId())
                .orElseThrow(() -> new TailNotFoundException("The requested tail does not exist."));
        log.info("retrieving tail user");
        UsersEntity user = this.userRepository.findById(tailEntity.getAssignedUserId())
                .orElseThrow(() -> new UserNotFoundException("User who requested prompt does not exist."));

        log.info("retrieving note entity");
        Optional<NoteEntity> optionalNoteEntity = this.noteRepository.findFirstByTailIdAndN1IsTrueOrderByCreatedAtDesc(tailEntity.getId());
        if (optionalNoteEntity.isEmpty()) return this.investigateWithLlm(llmRequest);
        else {
            NoteEntity n1Note = optionalNoteEntity.get();
            log.info("attempting to get latest 9 notes");
            List<NoteEntity> latest9Notes = this.noteRepository.findTop9ByTailIdOrderByCreatedAtDesc(tailEntity.getId());
            log.info("latest 9 notes length: {}", latest9Notes.size());
            latest9Notes = latest9Notes.stream().filter(note -> !note.isN1()).toList();
            log.info("latest 9 notes length after removing n1: {}", latest9Notes.size());


            List<PromptInput> inputs = new ArrayList<>();

            PromptInput n1Input = new PromptInput();
            n1Input.setRole("system engineer");
            n1Input.setContent(n1Note.getContent());
            inputs.add(n1Input);

            // currently getting the latest 9 notes for the request this could be modified to improve the prompt response
            latest9Notes.forEach(noteEntity -> {
                PromptInput input = new PromptInput();
                if (noteEntity.isHuman()) {
                    input.setRole(user.getUsername());
                    input.setContent(noteEntity.getContent());
                } else {
                    input.setRole("system engineer");
                    input.setContent(noteEntity.getContent());
                }
                inputs.add(input);
            });

            PromptInput promptInput = new PromptInput();
            promptInput.setRole(user.getUsername());
            promptInput.setContent(llmRequest.getPrompt());
            inputs.add(promptInput);

            log.info("INPUTS: {}", inputs);
            String promptResult = llmService.completePrompt(llmRequest.getModel(), inputs.toString());

            LlmPromptResponse llmResponse = new LlmPromptResponse();
            llmResponse.setCompletion(promptResult);
            llmResponse.setTailId(tailEntity.getId());
            llmResponse.setProvider("openai");
            llmResponse.setModel(llmRequest.getModel());
            llmResponse.setUserId(llmRequest.getUserId());
            llmResponse.setOrganizationId(llmRequest.getOrganizationId());

            saveNote(llmResponse, tailEntity);
            return llmResponse;
        }
    }

    private String nullToNA(String value) {
        return (value == null || value.trim().isEmpty()) ? "N/A" : value;
    }

    private void saveN1Note(LlmPromptResponse llmPromptResponse, TailEntity tailEntity) throws UserNotFoundException, N1NoteAlreadyExistsException {

        Optional<NoteEntity> optionalNoteEntity = this.noteRepository.findFirstByTailIdAndN1IsTrueOrderByCreatedAtDesc(tailEntity.getId());
        if (optionalNoteEntity.isPresent()) throw new N1NoteAlreadyExistsException("Unable to create n1 note as one exists already.");

        NoteEntity noteEntity = getNoteEntity(llmPromptResponse, tailEntity);
        noteEntity.setN1(true);
        // save note
        log.info("Saving n1 note");
        this.noteRepository.save(noteEntity);
    }

    private void saveNote(LlmPromptResponse llmPromptResponse, TailEntity tailEntity) throws UserNotFoundException {

        NoteEntity noteEntity = getNoteEntity(llmPromptResponse, tailEntity);
        noteEntity.setN1(false);
        // save note
        log.info("Saving note");
        this.noteRepository.save(noteEntity);
    }

    private NoteEntity getNoteEntity(LlmPromptResponse llmPromptResponse, TailEntity tailEntity) throws UserNotFoundException {
        UsersEntity user = this.userRepository.findById(tailEntity.getAssignedUserId())
                .orElseThrow(() -> new UserNotFoundException("User who requested to add new note does not exist."));

        NoteEntity noteEntity = new NoteEntity();
        noteEntity.setTail(tailEntity);
        noteEntity.setUser(user);
        noteEntity.setContent(llmPromptResponse.getCompletion());
        noteEntity.setCreatedAt(Instant.now());
        noteEntity.setHuman(false);
        noteEntity.setLlmProvider("openai");
        noteEntity.setLlmModel(llmPromptResponse.getModel());
        noteEntity.setOrganization(tailEntity.getOrganization());
        return noteEntity;
    }
}
