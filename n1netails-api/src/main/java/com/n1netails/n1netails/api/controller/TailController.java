package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.*;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.core.TailStatus;
import com.n1netails.n1netails.api.model.request.ResolveTailRequest;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
import com.n1netails.n1netails.api.model.response.HttpErrorResponse;
import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.service.TailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@Slf4j
@RequiredArgsConstructor
@Tag(name = "Tail Controller", description = "Operations related to Tails")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = {"/ninetails/tail"}, produces = APPLICATION_JSON_VALUE)
public class TailController {

    private final TailService tailService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "Get tail by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Tail found",
                    content = @Content(schema = @Schema(implementation = TailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tail not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TailResponse> getById(@PathVariable Long id, @RequestHeader("Authorization") String authorizationHeader) throws UserNotFoundException, UnauthorizedException, TailNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        return ResponseEntity.ok(tailService.getTailById(id, currentUser));
    }

    @Operation(summary = "Get tails by page", responses = {
            @ApiResponse(responseCode = "200", description = "Page of tails",
                    content = @Content(schema = @Schema(implementation = Page.class))) // Note: Ideally, you'd use a Page<TailResponse> schema
    })
    @PostMapping("/page")
    public ResponseEntity<Page<TailResponse>> getTailsByPage(@RequestBody TailPageRequest request, @RequestHeader("Authorization") String authorizationHeader) throws TailTypeNotFoundException, TailLevelNotFoundException, TailStatusNotFoundException, UserNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        return ResponseEntity.ok(tailService.getTails(request, currentUser));
    }

    @Operation(summary = "Get top 9 newest tails", responses = {
            @ApiResponse(responseCode = "200", description = "List of top 9 newest tails",
                    content = @Content(schema = @Schema(implementation = TailResponse.class)))
    })
    @GetMapping("/top9")
    public ResponseEntity<List<TailResponse>> getTop9NewestTails(@RequestHeader("Authorization") String authorizationHeader) throws UserNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        return ResponseEntity.ok(tailService.getTop9NewestTails(currentUser));
    }

    @Operation(summary = "Mark tail as resolved", responses = {
            @ApiResponse(responseCode = "204", description = "Tail resolved"),
            @ApiResponse(responseCode = "404", description = "Tail or tail status not found")
    })
    @PostMapping("/mark/resolved")
    public ResponseEntity<Void> markTailResolved(@RequestHeader("Authorization") String authorizationHeader, @RequestBody ResolveTailRequest request) throws TailNotFoundException, TailStatusNotFoundException, UserNotFoundException, UnauthorizedException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        tailService.markResolved(request, currentUser);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Resolve all NEW tails for current user", responses = {
            @ApiResponse(responseCode = "204", description = "Tails resolved"),
            @ApiResponse(responseCode = "404", description = "Tail status not found")
    })
    @PostMapping("/resolve-all")
    public ResponseEntity<Void> resolveAll(@RequestHeader("Authorization") String authorizationHeader) throws UserNotFoundException, TailStatusNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        tailService.resolveAll(currentUser);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update tail status", responses = {
            @ApiResponse(responseCode = "204", description = "Tail status updated"),
            @ApiResponse(responseCode = "404", description = "Tail or tail status not found")
    })
    @PutMapping("/update/status")
    public ResponseEntity<Void> updateTailStatus(@RequestHeader("Authorization") String authorizationHeader, @RequestBody ResolveTailRequest request) throws TailNotFoundException, TailStatusNotFoundException, UserNotFoundException, UnauthorizedException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        tailService.updateStatus(request, currentUser);
        return ResponseEntity.noContent().build();
    }
}
