package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.dto.Note;
import com.n1netails.n1netails.api.model.request.NotePageRequest;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.service.NoteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.n1netails.n1netails.api.exception.type.*;
import com.n1netails.n1netails.api.model.response.HttpErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;


import java.util.List;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Note Controller", description = "Operations related to Notes")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = {"/ninetails/note"}, produces = APPLICATION_JSON)
public class NoteController {

    private final NoteService noteService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "Create a new note for a tail", responses = {
            @ApiResponse(responseCode = "201", description = "Note created successfully",
                    content = @Content(schema = @Schema(implementation = Note.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized to create note for this tail",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not assigned to tail or lacking permissions",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tail or User not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "N1 note already exists for this tail",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Note> createNote(@RequestHeader("Authorization") String authorizationHeader,
                                           @RequestBody Note noteRequest)
            throws UserNotFoundException, TailNotFoundException, UnauthorizedException, N1NoteAlreadyExistsException, NoteNoContentException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        log.info("User {} attempting to create note for tail {}", currentUser.getUsername(), noteRequest.getTailId());

        if (!authorizationService.isUserAssignedToTail(currentUser, noteRequest.getTailId())) {
            log.warn("User {} is not authorized to create a note for tail {}", currentUser.getUsername(), noteRequest.getTailId());
            throw new UnauthorizedException("User is not authorized to create notes for this tail.");
        }

        noteRequest.setUserId(currentUser.getId());
        noteRequest.setUsername(currentUser.getUsername());

        if (noteRequest.getContent().isBlank()) throw new NoteNoContentException("No content was provided in the note.");

        Note createdNote = noteService.add(noteRequest);
        log.info("Note created with ID {} for tail {} by user {}", createdNote.getId(), createdNote.getTailId(), currentUser.getUsername());
        return new ResponseEntity<>(createdNote, HttpStatus.CREATED);
    }

    @Operation(summary = "Get a note by its ID", responses = {
            @ApiResponse(responseCode = "200", description = "Note found",
                    content = @Content(schema = @Schema(implementation = Note.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized for this note's tail",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Note not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@RequestHeader("Authorization") String authorizationHeader,
                                            @PathVariable Long id)
            throws UserNotFoundException, NoteNotFoundException, UnauthorizedException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        log.debug("User {} attempting to fetch note with ID {}", currentUser.getUsername(), id);

        Note note = noteService.getById(id);

        if (!authorizationService.isUserAssignedToTail(currentUser, note.getTailId())) {
            log.warn("User {} is not authorized to access note {} for tail {}", currentUser.getUsername(), id, note.getTailId());
            throw new UnauthorizedException("User is not authorized to access this note.");
        }
        log.info("User {} fetched note with ID {}", currentUser.getUsername(), id);
        return ResponseEntity.ok(note);
    }

    @Operation(summary = "Get all notes for a specific tail", responses = {
            @ApiResponse(responseCode = "200", description = "List of notes for the tail"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized for this tail"),
            @ApiResponse(responseCode = "404", description = "Tail not found")
    })
    @GetMapping("/tail/{tailId}")
    public ResponseEntity<List<Note>> getAllNotesByTailId(@RequestHeader("Authorization") String authorizationHeader,
                                                         @PathVariable Long tailId)
            throws UserNotFoundException, TailNotFoundException, UnauthorizedException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        log.debug("User {} attempting to fetch all notes for tail {}", currentUser.getUsername(), tailId);

        if (!authorizationService.isUserAssignedToTail(currentUser, tailId)) {
            log.warn("User {} is not authorized to access notes for tail {}", currentUser.getUsername(), tailId);
            throw new UnauthorizedException("User is not authorized to access notes for this tail.");
        }

        List<Note> notes = noteService.getAllByTailId(tailId);
        log.info("User {} fetched {} notes for tail {}", currentUser.getUsername(), notes.size(), tailId);
        return ResponseEntity.ok(notes);
    }

    @Operation(summary = "Get the last 9 notes for a specific tail", responses = {
            @ApiResponse(responseCode = "200", description = "List of last 9 notes"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized for this tail"),
            @ApiResponse(responseCode = "404", description = "Tail not found")
    })
    @GetMapping("/tail/{tailId}/latest")
    public ResponseEntity<List<Note>> getLast9NotesByTailId(@RequestHeader("Authorization") String authorizationHeader,
                                                             @PathVariable Long tailId)
            throws UserNotFoundException, TailNotFoundException, UnauthorizedException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        log.debug("User {} attempting to fetch latest notes for tail {}", currentUser.getUsername(), tailId);

        if (!authorizationService.isUserAssignedToTail(currentUser, tailId)) {
            log.warn("User {} is not authorized to access notes for tail {}", currentUser.getUsername(), tailId);
            throw new UnauthorizedException("User is not authorized to access notes for this tail.");
        }

        List<Note> notes = noteService.getLast9NotesByTailId(tailId);
        log.info("User {} fetched {} latest notes for tail {}", currentUser.getUsername(), notes.size(), tailId);
        return ResponseEntity.ok(notes);
    }

    @Operation(summary = "Get notes for a specific tail with pagination", responses = {
            @ApiResponse(responseCode = "200", description = "Page of notes"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized for this tail")
    })
    @PostMapping("/tail/page")
    public ResponseEntity<Page<Note>> getNotesByTailIdPaginated(@RequestHeader("Authorization") String authorizationHeader,
                                                               @RequestBody NotePageRequest request)
            throws UserNotFoundException, UnauthorizedException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        log.debug("User {} attempting to fetch paginated notes for tail {}", currentUser.getUsername(), request.getTailId());

        if (request.getTailId() == null) {
             log.warn("Tail ID is null in note page request by user {}", currentUser.getUsername());
            throw new IllegalArgumentException("Tail ID cannot be null in NotePageRequest.");
        }

        if (!authorizationService.isUserAssignedToTail(currentUser, request.getTailId())) {
            log.warn("User {} is not authorized to access notes for tail {}", currentUser.getUsername(), request.getTailId());
            throw new UnauthorizedException("User is not authorized to access notes for this tail.");
        }

        Page<Note> notesPage = noteService.getNotesByTailId(request);
        log.info("User {} fetched page {} of notes for tail {}", currentUser.getUsername(), request.getPage(), request.getTailId());
        return ResponseEntity.ok(notesPage);
    }

    @Operation(summary = "Get the N1 note for a specific tail", responses = {
            @ApiResponse(responseCode = "200", description = "N1 Note found",
                    content = @Content(schema = @Schema(implementation = Note.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Forbidden - User not authorized for this tail"),
            @ApiResponse(responseCode = "404", description = "N1 Note or Tail not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @GetMapping("/tail/{tailId}/isN1")
    public ResponseEntity<Note> getN1NoteByTailId(@RequestHeader("Authorization") String authorizationHeader,
                                                 @PathVariable Long tailId)
            throws UserNotFoundException, NoteNotFoundException, UnauthorizedException, TailNotFoundException {
        log.info("Fetching N1 Note");

        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        log.debug("User {} attempting to fetch N1 note for tail {}", currentUser.getUsername(), tailId);

        if (!authorizationService.isUserAssignedToTail(currentUser, tailId)) {
            log.warn("User {} is not authorized to access N1 note for tail {}", currentUser.getUsername(), tailId);
            throw new UnauthorizedException("User is not authorized to access the N1 note for this tail.");
        }

        Note n1Note = noteService.getIsN1ByTailId(tailId);
        log.info("User {} fetched N1 note with ID {} for tail {}", currentUser.getUsername(), n1Note.getId(), tailId);
        return ResponseEntity.ok(n1Note);
    }
}
