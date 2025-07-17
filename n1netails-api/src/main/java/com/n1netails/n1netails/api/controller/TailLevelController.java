package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.TailLevelNotFoundException;
import com.n1netails.n1netails.api.model.core.TailLevel;
import com.n1netails.n1netails.api.model.response.HttpErrorResponse;
import com.n1netails.n1netails.api.model.request.PageRequest;
import com.n1netails.n1netails.api.model.response.TailLevelResponse;
import com.n1netails.n1netails.api.service.TailLevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@Tag(name = "Tail Level Controller", description = "Operations related to Tail Levels")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = {"/ninetails/tail-level"}, produces = APPLICATION_JSON)
public class TailLevelController {

    private final TailLevelService tailLevelService;

    @Operation(summary = "Get all tail levels", responses = {
            @ApiResponse(responseCode = "200", description = "List of tail levels",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TailLevelResponse.class))))
    })
    @GetMapping
    public ResponseEntity<Page<TailLevelResponse>> getTailLevels(PageRequest request) {
        return ResponseEntity.ok(tailLevelService.getTailLevels(request));
    }

    @Operation(summary = "Get tail level by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Tail level found",
                    content = @Content(schema = @Schema(implementation = TailLevelResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tail level not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TailLevelResponse> getTailLevelById(@PathVariable Long id) {
        return ResponseEntity.ok(tailLevelService.getTailLevelById(id));
    }

    @Operation(summary = "Create a new tail level", responses = {
            @ApiResponse(responseCode = "200", description = "Tail level created",
                    content = @Content(schema = @Schema(implementation = TailLevelResponse.class)))
    })
    @PostMapping(consumes = APPLICATION_JSON)
    public ResponseEntity<TailLevelResponse> createTailLevel(@RequestBody TailLevel request) {
        return ResponseEntity.ok(tailLevelService.createTailLevel(request));
    }

    @Operation(summary = "Update tail level by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Tail level updated",
                    content = @Content(schema = @Schema(implementation = TailLevelResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tail level not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON)
    public ResponseEntity<TailLevelResponse> updateTailLevel(@PathVariable Long id, @RequestBody TailLevel request) {
        return ResponseEntity.ok(tailLevelService.updateTailLevel(id, request));
    }

    @Operation(summary = "Delete tail level by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Tail level deleted"),
            @ApiResponse(responseCode = "404", description = "Tail level not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTailLevel(@PathVariable Long id) throws TailLevelNotFoundException {
        tailLevelService.deleteTailLevel(id);
        return ResponseEntity.noContent().build();
    }
}
