package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.model.core.TailType;
import com.n1netails.n1netails.api.model.response.HttpErrorResponse;
import com.n1netails.n1netails.api.model.response.TailTypeResponse;
import com.n1netails.n1netails.api.service.TailTypeService;
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
@Tag(name = "Tail Type Controller", description = "Operations related to Tail Types")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = {"/api/tail-type"}, produces = APPLICATION_JSON)
public class TailTypeController {

    private final TailTypeService tailTypeService;

    @Operation(summary = "Get all tail types", responses = {
            @ApiResponse(responseCode = "200", description = "List of tail types",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TailTypeResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<TailTypeResponse>> getTailTypes() {
        return ResponseEntity.ok(tailTypeService.getTailTypes());
    }

    @Operation(summary = "Get tail type by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Tail type found",
                    content = @Content(schema = @Schema(implementation = TailTypeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tail type not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TailTypeResponse> getTailTypeById(@PathVariable Long id) {
        return ResponseEntity.ok(tailTypeService.getTailTypeById(id));
    }

    @Operation(summary = "Create a new tail type", responses = {
            @ApiResponse(responseCode = "200", description = "Tail type created",
                    content = @Content(schema = @Schema(implementation = TailTypeResponse.class)))
    })
    @PostMapping(consumes = APPLICATION_JSON)
    public ResponseEntity<TailTypeResponse> createTailType(@RequestBody TailType request) {
        return ResponseEntity.ok(tailTypeService.createTailType(request));
    }

    @Operation(summary = "Update tail type by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Tail type updated",
                    content = @Content(schema = @Schema(implementation = TailTypeResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tail type not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON)
    public ResponseEntity<TailTypeResponse> updateTailType(@PathVariable Long id, @RequestBody TailType request) {
        return ResponseEntity.ok(tailTypeService.updateTailType(id, request));
    }

    @Operation(summary = "Delete tail type by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Tail type deleted"),
            @ApiResponse(responseCode = "404", description = "Tail type not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTailType(@PathVariable Long id) {
        tailTypeService.deleteTailType(id);
        return ResponseEntity.noContent().build();
    }
}
