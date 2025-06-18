package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.ai.LlmRequest;
import com.n1netails.n1netails.api.model.ai.LlmResponse;
import com.n1netails.n1netails.api.model.response.N1neTokenResponse;
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

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Large Language Model Controller", description = "Operations related requests and responses for Large Language Models")
@RestController
@RequestMapping(path = {"/ninetails/llm"}, produces = APPLICATION_JSON)
public class LlmController {

    private final InvestigationService investigationService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "Investigate tail using llm provider", responses = {
            @ApiResponse(responseCode = "200", description = "Send tail data to llm provider to investigate the tail details",
                    content = @Content(schema = @Schema(implementation = N1neTokenResponse.class)))
    })
    @PostMapping(consumes = APPLICATION_JSON)
    public ResponseEntity<LlmResponse>  investigateTail(
            @RequestHeader(AUTHORIZATION) String authorizationHeader,
            @RequestBody LlmRequest llmRequest) throws UserNotFoundException, AccessDeniedException, TailNotFoundException {

        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        if (authorizationService.belongsToOrganization(currentUser, llmRequest.getOrganizationId())) {
            log.info("Investigating tail");
            LlmResponse llmResponse = this.investigationService.investigateWithLlm(llmRequest);
            return ResponseEntity.ok(llmResponse);
        } else {
            throw new AccessDeniedException("Investigate tail request access denied.");
        }
    }
}
