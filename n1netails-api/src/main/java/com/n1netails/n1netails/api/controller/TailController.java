package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.TailLevelNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailStatusNotFoundException;
import com.n1netails.n1netails.api.exception.type.TailTypeNotFoundException;
import com.n1netails.n1netails.api.model.request.ResolveTailRequest;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
import com.n1netails.n1netails.api.model.request.TailRequest;
import com.n1netails.n1netails.api.model.response.HttpErrorResponse;
import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.repository.UserRepository;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
    private final UserRepository userRepository;

    @Operation(summary = "Get tail by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Tail found",
                    content = @Content(schema = @Schema(implementation = TailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tail not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TailResponse> getById(@PathVariable Long id, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        TailResponse tail = tailService.getTailById(id);

        if (currentUser.getRole().equals("ROLE_USER")) {
            UsersEntity user = userRepository.findUserById(currentUser.getId());
            boolean isN1neTailsOrgMember = user.getOrganizations().stream().anyMatch(org -> "n1netails".equalsIgnoreCase(org.getName()));

            if (isN1neTailsOrgMember) {
                if (!authorizationService.isTailOwner(currentUser, tail.getAssignedUserId())) {
                    throw new AccessDeniedException("User does not own this tail");
                }
            } else {
                if (!authorizationService.belongsToOrganization(currentUser, tail.getOrganizationId())) {
                    throw new AccessDeniedException("User does not belong to the tail's organization");
                }
            }
        }
        return ResponseEntity.ok(tail);
    }

    @Operation(summary = "Get tails by page", responses = {
            @ApiResponse(responseCode = "200", description = "Page of tails",
                    content = @Content(schema = @Schema(implementation = Page.class))) // Note: Ideally, you'd use a Page<TailResponse> schema
    })
    @PostMapping("/page")
    public ResponseEntity<Page<TailResponse>> getTailsByPage(@RequestBody TailPageRequest request, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws TailTypeNotFoundException, TailLevelNotFoundException, TailStatusNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);

        if (currentUser.getRole().equals("ROLE_USER")) {
            UsersEntity user = userRepository.findUserById(currentUser.getId());
            boolean isN1neTailsOrgMember = user.getOrganizations().stream().anyMatch(org -> "n1netails".equalsIgnoreCase(org.getName()));

            if (isN1neTailsOrgMember) {
                request.setAssignedUserId(currentUser.getId());
            } else {
                request.setOrganizationIds(user.getOrganizations().stream().map(org -> org.getId()).collect(Collectors.toList()));
            }
        }
        return ResponseEntity.ok(tailService.getTails(request));
    }

    @Operation(summary = "Get top 9 newest tails", responses = {
            @ApiResponse(responseCode = "200", description = "List of top 9 newest tails",
                    content = @Content(schema = @Schema(implementation = TailResponse.class)))
    })
    @GetMapping("/top9")
    public ResponseEntity<List<TailResponse>> getTop9NewestTails(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        List<TailResponse> tails = tailService.getTop9NewestTails();

        if (currentUser.getRole().equals("ROLE_USER")) {
            UsersEntity user = userRepository.findUserById(currentUser.getId());
            boolean isN1neTailsOrgMember = user.getOrganizations().stream().anyMatch(org -> "n1netails".equalsIgnoreCase(org.getName()));

            if (isN1neTailsOrgMember) {
                tails = tails.stream()
                        .filter(tail -> authorizationService.isTailOwner(currentUser, tail.getAssignedUserId()))
                        .collect(Collectors.toList());
            } else {
                List<Long> userOrgIds = user.getOrganizations().stream().map(org -> org.getId()).collect(Collectors.toList());
                tails = tails.stream()
                        .filter(tail -> userOrgIds.contains(tail.getOrganizationId()))
                        .collect(Collectors.toList());
            }
        }
        return ResponseEntity.ok(tails);
    }

    @Operation(summary = "Mark tail as resolved", responses = {
            @ApiResponse(responseCode = "204", description = "Tail resolved"),
            @ApiResponse(responseCode = "404", description = "Tail or tail status not found")
    })
    @PostMapping("/mark/resolved")
    public ResponseEntity<Void> markTailResolved(@RequestBody ResolveTailRequest request, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) throws TailNotFoundException, TailStatusNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        // Using request.getTailSummary().getId() as per ResolveTailRequest structure
        TailResponse tail = tailService.getTailById(request.getTailSummary().getId());

        if (currentUser.getRole().equals("ROLE_USER")) {
            if (!authorizationService.isTailOwner(currentUser, tail.getAssignedUserId()) &&
                !authorizationService.isOrganizationAdmin(currentUser, tail.getOrganizationId())) {
                throw new AccessDeniedException("User is not authorized to mark this tail as resolved");
            }
        }
        tailService.markResolved(request);
        return ResponseEntity.noContent().build();
    }
}
