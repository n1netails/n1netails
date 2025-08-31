package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.*;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
import com.n1netails.n1netails.api.model.response.IsBookmarkedResponse;
import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.service.TailBookmarkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;

@RestController
@RequestMapping("/ninetails/bookmarks")
@RequiredArgsConstructor
@Tag(name = "Tail Bookmark Controller", description = "Operations related to tail bookmarks")
@SecurityRequirement(name = "bearerAuth")
public class TailBookmarkController {

    private final TailBookmarkService tailBookmarkService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "Bookmark a tail", responses = {
            @ApiResponse(responseCode = "200", description = "Tail bookmarked successfully"),
            @ApiResponse(responseCode = "404", description = "Tail or user not found"),
            @ApiResponse(responseCode = "409", description = "Tail already bookmarked")
    })
    @PostMapping("/{tailId}")
    public ResponseEntity<Void> bookmarkTail(@PathVariable Long tailId,
                                             @RequestHeader("Authorization") String authorizationHeader)
            throws UserNotFoundException, TailNotFoundException, TailAlreadyBookmarkedException, UnauthorizedException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        tailBookmarkService.bookmarkTail(currentUser.getId(), tailId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove a bookmark from a tail", responses = {
            @ApiResponse(responseCode = "204", description = "Bookmark removed successfully"),
            @ApiResponse(responseCode = "404", description = "Tail or user not found")
    })
    @DeleteMapping("/{tailId}")
    public ResponseEntity<Void> removeBookmark(@PathVariable Long tailId,
                                               @RequestHeader("Authorization") String authorizationHeader)
            throws UserNotFoundException, UnauthorizedException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        tailBookmarkService.removeBookmark(currentUser.getId(), tailId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all bookmarked tails for the current user", responses = {
            @ApiResponse(responseCode = "200", description = "List of bookmarked tails"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping
    public ResponseEntity<Page<TailResponse>> getUserBookmarks(@RequestBody TailPageRequest request, @RequestHeader("Authorization") String authorizationHeader)
            throws UserNotFoundException, UnauthorizedException, TailTypeNotFoundException, TailLevelNotFoundException, TailStatusNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        Page<TailResponse> bookmarks = tailBookmarkService.getUserBookmarks(request, currentUser);
        // todo handle page request
//        List<TailResponse> response = bookmarks.stream()
//                .map(this::toTailResponse)
//                .collect(Collectors.toList());
        return ResponseEntity.ok(bookmarks);
    }

    @Operation(summary = "Check if a tail is bookmarked by the current user", responses = {
            @ApiResponse(responseCode = "200", description = "Bookmark status"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{tailId}/exists")
    public ResponseEntity<IsBookmarkedResponse> isBookmarked(@PathVariable Long tailId,
                                                             @RequestHeader("Authorization") String authorizationHeader)
            throws UserNotFoundException, UnauthorizedException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        boolean isBookmarked = tailBookmarkService.isBookmarked(currentUser.getId(), tailId);
        IsBookmarkedResponse isBookmarkedResponse = new IsBookmarkedResponse();
        isBookmarkedResponse.setBookmarked(isBookmarked);
        return ResponseEntity.ok(isBookmarkedResponse);
    }

//    private TailResponse toTailResponse(TailEntity tailEntity) {
//        return new TailResponse(
//                tailEntity.getId(),
//                tailEntity.getTitle(),
//                tailEntity.getDescription(),
//                tailEntity.getTimestamp(),
//                tailEntity.getResolvedTimestamp(),
//                tailEntity.getAssignedUserId(),
//                null, // assignedUsername is not in TailEntity, so I'll leave it null
//                tailEntity.getDetails(),
//                tailEntity.getLevel() != null ? tailEntity.getLevel().getName() : null,
//                tailEntity.getType() != null ? tailEntity.getType().getName() : null,
//                tailEntity.getStatus() != null ? tailEntity.getStatus().getName() : null,
//                null, // metadata is not in TailEntity
//                tailEntity.getOrganization() != null ? tailEntity.getOrganization().getId() : null
//        );
//    }
}
