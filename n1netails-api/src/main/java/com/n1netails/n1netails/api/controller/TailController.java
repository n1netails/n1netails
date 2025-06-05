package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.*;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.request.ResolveTailRequest;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
// import com.n1netails.n1netails.api.model.request.TailRequest; // No longer used in this controller directly
import com.n1netails.n1netails.api.model.response.HttpErrorResponse;
import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.service.TailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.security.access.AccessDeniedException;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Tail Controller", description = "Operations related to Tails")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = {"/ninetails/tail"}, produces = APPLICATION_JSON)
public class TailController {

    private final TailService tailService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "Get tail by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Tail found",
                    content = @Content(schema = @Schema(implementation = TailResponse.class))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tail not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TailResponse> getById(@PathVariable Long id,
                                                @RequestHeader(AUTHORIZATION) String authorizationHeader) throws UserNotFoundException, AccessDeniedException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        TailResponse tail = tailService.getTailById(id);

        boolean canAccess = authorizationService.isTailOwner(currentUser, tail.getUserId()) ||
                            authorizationService.belongsToOrganization(currentUser, tail.getOrganizationId());

        if (!canAccess) {
            log.warn("User {} (ID: {}) attempted to access tail {} which they do not own or belong to its organization {}.",
                    currentUser.getUsername(), currentUser.getId(), id, tail.getOrganizationId());
            throw new AccessDeniedException("User cannot access this tail.");
        }
        log.info("User {} (ID: {}) accessed tail {}", currentUser.getUsername(), currentUser.getId(), id);
        return ResponseEntity.ok(tail);
    }

    @Operation(summary = "Get tails by page", responses = {
            @ApiResponse(responseCode = "200", description = "Page of tails",
                    content = @Content(schema = @Schema(implementation = Page.class)))
    })
    @PostMapping("/page")
    public ResponseEntity<Page<TailResponse>> getTailsByPage(@RequestBody TailPageRequest request,
                                                              @RequestHeader(AUTHORIZATION) String authorizationHeader)
            throws UserNotFoundException, TailTypeNotFoundException, TailLevelNotFoundException, TailStatusNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        log.info("User {} (ID: {}) requesting page of tails with request: {}", currentUser.getUsername(), currentUser.getId(), request);
        Page<TailResponse> tails = tailService.getTails(request, currentUser);
        return ResponseEntity.ok(tails);
    }

    @Operation(summary = "Get top 9 newest tails", responses = {
            @ApiResponse(responseCode = "200", description = "List of top 9 newest tails",
                    content = @Content(schema = @Schema(implementation = TailResponse.class)))
    })
    @GetMapping("/top9")
    public ResponseEntity<List<TailResponse>> getTop9NewestTails(@RequestHeader(AUTHORIZATION) String authorizationHeader) throws UserNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        log.info("User {} (ID: {}) requesting top 9 newest tails.", currentUser.getUsername(), currentUser.getId());
        List<TailResponse> tails = tailService.getTop9NewestTails(currentUser);
        return ResponseEntity.ok(tails);
    }

    @Operation(summary = "Mark tail as resolved", responses = {
            @ApiResponse(responseCode = "204", description = "Tail resolved"),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tail or tail status not found")
    })
    @PostMapping("/mark/resolved")
    public ResponseEntity<Void> markTailResolved(@RequestBody ResolveTailRequest request,
                                                 @RequestHeader(AUTHORIZATION) String authorizationHeader)
            throws UserNotFoundException, AccessDeniedException, TailNotFoundException, TailStatusNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        TailResponse tail = tailService.getTailById(request.getTailId()); // Fetch tail to check ownership/admin rights

        boolean isAssigned = tail.getAssignedUserId() != null && currentUser.getId().equals(tail.getAssignedUserId());
        boolean isAdmin = authorizationService.isOrganizationAdmin(currentUser, tail.getOrganizationId());

        if (!(isAssigned || isAdmin)) {
            log.warn("User {} (ID: {}) attempted to mark tail {} as resolved. User is not assigned ({}) nor admin of organization {}.",
                    currentUser.getUsername(), currentUser.getId(), request.getTailId(), tail.getAssignedUserId(), tail.getOrganizationId());
            throw new AccessDeniedException("User cannot mark this tail as resolved. Must be assigned user or organization admin.");
        }

        log.info("User {} (ID: {}) marking tail {} as resolved. IsAssigned: {}, IsAdmin: {}",
                currentUser.getUsername(), currentUser.getId(), request.getTailId(), isAssigned, isAdmin);
        tailService.markResolved(request);
        return ResponseEntity.noContent().build();
    }
}
