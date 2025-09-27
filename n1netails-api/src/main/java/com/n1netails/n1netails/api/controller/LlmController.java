package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.N1NoteAlreadyExistsException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.ai.LlmPromptRequest;
import com.n1netails.n1netails.api.model.ai.LlmPromptResponse;
import com.n1netails.n1netails.api.model.response.HttpErrorResponse;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.service.InvestigationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Large Language Model Controller", description = "Operations related requests and responses for Large Language Models")
@RestController
@RequestMapping(path = {"/ninetails/llm"}, produces = APPLICATION_JSON_VALUE)
public class LlmController {

    private final InvestigationService investigationService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "Investigate tail using llm provider", responses = {
            @ApiResponse(responseCode = "200", description = "Send tail data to llm provider to investigate the tail details",
                    content = @Content(schema = @Schema(implementation = LlmPromptResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized to investigate tail",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not assigned to tail or lacking permissions",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tail or User not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "N1 note already exists for this tail",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @PostMapping(path = "/investigate", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<LlmPromptResponse>  investigateTail(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @RequestBody LlmPromptRequest llmRequest) throws UserNotFoundException, AccessDeniedException, TailNotFoundException, N1NoteAlreadyExistsException {

        log.info("Tail investigation request");
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        if (authorizationService.belongsToOrganization(currentUser, llmRequest.getOrganizationId())) {
            log.info("Investigating tail");
            LlmPromptResponse llmResponse = this.investigationService.investigateWithLlm(llmRequest);
            return ResponseEntity.ok(llmResponse);
        } else {
            throw new AccessDeniedException("Investigate tail request access denied.");
        }
    }

    @Operation(summary = "Prompt tail using llm provider", responses = {
            @ApiResponse(responseCode = "200", description = "Send prompt to llm provider to investigate the tail notes",
                    content = @Content(schema = @Schema(implementation = LlmPromptResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized to investigate tail",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not assigned to tail or lacking permissions",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tail or User not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @PostMapping(path = "/prompt", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<LlmPromptResponse>  promptTail(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @RequestBody LlmPromptRequest llmRequest) throws UserNotFoundException, AccessDeniedException, TailNotFoundException, N1NoteAlreadyExistsException {

        log.info("Tail prompt request");
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        if (authorizationService.belongsToOrganization(currentUser, llmRequest.getOrganizationId())) {
            log.info("Sending prompt request for tail");
            LlmPromptResponse llmResponse = this.investigationService.promptTail(llmRequest);
            return ResponseEntity.ok(llmResponse);
        } else {
            throw new AccessDeniedException("Prompt tail request access denied.");
        }
    }
}
