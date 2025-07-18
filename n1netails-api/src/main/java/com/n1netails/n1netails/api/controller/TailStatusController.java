package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.TailStatusNotFoundException;
import com.n1netails.n1netails.api.model.core.TailStatus;
import com.n1netails.n1netails.api.model.response.HttpErrorResponse;
import com.n1netails.n1netails.api.model.response.TailLevelResponse;
import com.n1netails.n1netails.api.model.request.PageRequest;
import com.n1netails.n1netails.api.model.response.TailStatusResponse;
import com.n1netails.n1netails.api.service.TailStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Tail Status Controller", description = "Operations related to Tail Status")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(path = {"/ninetails/tail-status"}, produces = APPLICATION_JSON)
public class TailStatusController {

    private final TailStatusService tailStatusService;

    @Operation(summary = "Get tail status list", responses = {
            @ApiResponse(responseCode = "200", description = "Paginated result containing tail status (Spring Data Page format). List of tail status",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TailStatusResponse.class))))
    })
    @GetMapping
    public ResponseEntity<Page<TailStatusResponse>> getTailStatusList(@ParameterObject PageRequest request) {
        return ResponseEntity.ok(tailStatusService.getTailStatusList(request));
    }

    @Operation(summary = "Get tail status by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Tail status found",
                    content = @Content(schema = @Schema(implementation = TailStatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tail status not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<TailStatusResponse> getTailStatusById(@PathVariable Long id) {
        return ResponseEntity.ok(tailStatusService.getTailStatusById(id));
    }

    @Operation(summary = "Create a new tail status", responses = {
            @ApiResponse(responseCode = "200", description = "Tail status created",
                    content = @Content(schema = @Schema(implementation = TailLevelResponse.class)))
    })
    @PostMapping(consumes = APPLICATION_JSON)
    public ResponseEntity<TailStatusResponse> createTailStatus(@RequestBody TailStatus request) {
        return ResponseEntity.ok(tailStatusService.createTailStatus(request));
    }

    @Operation(summary = "Update tail status by ID", responses = {
            @ApiResponse(responseCode = "200", description = "Tail status updated",
                    content = @Content(schema = @Schema(implementation = TailStatusResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tail status not found",
                    content = @Content(schema = @Schema(implementation = HttpErrorResponse.class)))
    })
    @PutMapping(value = "/{id}", consumes = APPLICATION_JSON)
    public ResponseEntity<TailStatusResponse> updateTailStatus(@PathVariable Long id, @RequestBody TailStatus request) {
        return ResponseEntity.ok(tailStatusService.updateTailStatus(id, request));
    }

    @Operation(summary = "Delete tail status by ID", responses = {
            @ApiResponse(responseCode = "204", description = "Tail status deleted"),
            @ApiResponse(responseCode = "404", description = "Tail status not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTailStatus(@PathVariable Long id) throws TailStatusNotFoundException {
        tailStatusService.deleteTailStatus(id);
        return ResponseEntity.noContent().build();
    }
}
