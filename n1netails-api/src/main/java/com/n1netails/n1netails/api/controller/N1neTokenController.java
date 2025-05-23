package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.model.request.CreateTokenRequest;
import com.n1netails.n1netails.api.model.response.HttpErrorResponse;
import com.n1netails.n1netails.api.model.response.N1neTokenResponse;
import com.n1netails.n1netails.api.service.N1neTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "N1ne Token Controller", description = "Operations related to N1ne Tokens")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = {"/api/n1ne-token"}, produces = APPLICATION_JSON)
public class N1neTokenController {

    private final N1neTokenService n1neTokenService;

    @Operation(summary = "Create a new token", responses = {
            @ApiResponse(responseCode = "200", description = "Token created",
                    content = @Content(schema = @Schema(implementation = N1neTokenResponse.class)))
    })
    @PostMapping(consumes = APPLICATION_JSON)
    public ResponseEntity<N1neTokenResponse> create(CreateTokenRequest createTokenRequest) {
        N1neTokenResponse n1neTokenResponse = n1neTokenService.create(createTokenRequest);
        return ResponseEntity.ok(n1neTokenResponse);
    }

    @Operation(summary = "Get all tokens", responses = {
            @ApiResponse(responseCode = "200", description = "List of tokens",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = N1neTokenResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<N1neTokenResponse>> getAll() {
        List<N1neTokenResponse> n1neTokenResponseList = n1neTokenService.getAll();
        return ResponseEntity.ok(n1neTokenResponseList);
    }

    @Operation(summary = "Get token by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Token found",
                    content = @Content(schema = @Schema(implementation = N1neTokenResponse.class))),
            @ApiResponse(responseCode = "404", description = "Token not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<N1neTokenResponse> getById(Long id) {
        N1neTokenResponse n1neTokenResponse = n1neTokenService.getById(id);
        return ResponseEntity.ok(n1neTokenResponse);
    }

    @Operation(summary = "Revoke token by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Revoke submitted"),
            @ApiResponse(responseCode = "404", description = "Token not found")
    })
    @PutMapping("/revoke/{id}")
    public ResponseEntity<Void> revoke(Long id) {
        n1neTokenService.revoke(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Revoke token by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Revoke submitted"),
            @ApiResponse(responseCode = "404", description = "Token not found")
    })
    @PutMapping("/revoke/{id}")
    public ResponseEntity<Void> enable(Long id) {
        n1neTokenService.enable(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete token by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Token deleted"),
            @ApiResponse(responseCode = "404", description = "Token not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(Long id) {
        n1neTokenService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
