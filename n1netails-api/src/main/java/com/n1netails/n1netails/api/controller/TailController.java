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

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Tail Controller", description = "Operations related to Tails")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = {"/api/tail"}, produces = APPLICATION_JSON)
public class TailController {

    private final TailService tailService;

    @Operation(summary = "Create a new tail", responses = {
            @ApiResponse(responseCode = "200", description = "Tail created",
                    content = @Content(schema = @Schema(implementation = TailResponse.class)))
    })
    @PostMapping(consumes = APPLICATION_JSON)
    public ResponseEntity<TailResponse> create(@RequestBody TailRequest request) {
        return ResponseEntity.ok(tailService.createTail(request));
    }

    @Operation(summary = "Get all tails", responses = {
            @ApiResponse(responseCode = "200", description = "List of tails",
                    content = @Content(schema = @Schema(implementation = TailResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<TailResponse>> getAll() {
        return ResponseEntity.ok(tailService.getTails());
    }

    @Operation(summary = "Get tail by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Tail found",
                    content = @Content(schema = @Schema(implementation = TailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tail not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TailResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(tailService.getTailById(id));
    }

    @Operation(summary = "Update tail by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Tail updated",
                    content = @Content(schema = @Schema(implementation = TailResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tail not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON)
    public ResponseEntity<TailResponse> update(@PathVariable Long id, @RequestBody TailRequest request) {
        return ResponseEntity.ok(tailService.updateTail(id, request));
    }

    @Operation(summary = "Delete tail by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Tail deleted"),
            @ApiResponse(responseCode = "404", description = "Tail not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tailService.deleteTail(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get tails by page", responses = {
            @ApiResponse(responseCode = "200", description = "Page of tails",
                    content = @Content(schema = @Schema(implementation = Page.class))) // Note: Ideally, you'd use a Page<TailResponse> schema
    })
    @PostMapping("/page")
    public ResponseEntity<Page<TailResponse>> getTailsByPage(@RequestBody TailPageRequest request) throws TailTypeNotFoundException, TailLevelNotFoundException, TailStatusNotFoundException {
        return ResponseEntity.ok(tailService.getTails(request));
    }

    @Operation(summary = "Get top 9 newest tails", responses = {
            @ApiResponse(responseCode = "200", description = "List of top 9 newest tails",
                    content = @Content(schema = @Schema(implementation = TailResponse.class)))
    })
    @GetMapping("/top9")
    public ResponseEntity<List<TailResponse>> getTop9NewestTails() {
        return ResponseEntity.ok(tailService.getTop9NewestTails());
    }

    @Operation(summary = "Mark tail as resolved", responses = {
            @ApiResponse(responseCode = "204", description = "Tail resolved"),
            @ApiResponse(responseCode = "404", description = "Tail or tail status not found")
    })
    @PostMapping("/mark/resolved")
    public ResponseEntity<Void> markTailResolved(@RequestBody ResolveTailRequest request) throws TailNotFoundException, TailStatusNotFoundException {
        tailService.markResolved(request);
        return ResponseEntity.noContent().build();
    }
}
