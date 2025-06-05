package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.request.TimezoneRequest;
import com.n1netails.n1netails.api.model.response.*;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.service.TailMetricsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Tail Metrics Controller", description = "Operations related to Tail Metrics")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(value = "/ninetails/metrics/tails", produces = APPLICATION_JSON)
public class TailMetricsController {

    private final TailMetricsService tailMetricsService;
    private final AuthorizationService authorizationService;

    @Operation(summary = "Get tail alerts that occurred today.", responses = {
            @ApiResponse(responseCode = "200", description = "List of tail alerts",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TailResponse.class))))
    })
    @PostMapping("/today")
    public List<TailResponse> getTailAlertsToday(@RequestHeader(AUTHORIZATION) String authorizationHeader,
                                                 @RequestBody TimezoneRequest timezoneRequest) throws UserNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        return tailMetricsService.tailAlertsToday(timezoneRequest.getTimezone(), currentUser);
    }

    @Operation(summary = "Count tail alerts that occurred today.", responses = {
            @ApiResponse(responseCode = "200", description = "Total count of tail alerts",
                    content = @Content(schema = @Schema(implementation = long.class)))
    })
    @PostMapping("/today/count")
    public long countTailAlertsToday(@RequestHeader(AUTHORIZATION) String authorizationHeader,
                                     @RequestBody TimezoneRequest timezoneRequest) throws UserNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        return tailMetricsService.countAlertsToday(timezoneRequest.getTimezone(), currentUser);
    }

    @Operation(summary = "Get tail alerts that are resolved.", responses = {
            @ApiResponse(responseCode = "200", description = "List of tail alerts",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TailResponse.class))))
    })
    @GetMapping("/resolved")
    public List<TailResponse> getTailAlertsResolved(@RequestHeader(AUTHORIZATION) String authorizationHeader) throws UserNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        return tailMetricsService.tailAlertsResolved(currentUser);
    }

    @Operation(summary = "Count tail alerts that are resolved.", responses = {
            @ApiResponse(responseCode = "200", description = "Total count of tail alerts",
                    content = @Content(schema = @Schema(implementation = long.class)))
    })
    @GetMapping("/resolved/count")
    public long countTailAlertsResolved(@RequestHeader(AUTHORIZATION) String authorizationHeader) throws UserNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        return tailMetricsService.countAlertsResolved(currentUser);
    }

    @Operation(summary = "Get tail alerts that are not resolved.", responses = {
            @ApiResponse(responseCode = "200", description = "List of tail alerts",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TailResponse.class))))
    })
    @GetMapping("/not-resolved")
    public List<TailResponse> getTailAlertsNotResolved(@RequestHeader(AUTHORIZATION) String authorizationHeader) throws UserNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        return tailMetricsService.tailAlertsNotResolved(currentUser);
    }

    @Operation(summary = "Count tail alerts that are not resolved.", responses = {
            @ApiResponse(responseCode = "200", description = "Total count of tail alerts",
                    content = @Content(schema = @Schema(implementation = long.class)))
    })
    @GetMapping("/not-resolved/count")
    public long countTailAlertsNotResolved(@RequestHeader(AUTHORIZATION) String authorizationHeader) throws UserNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        return tailMetricsService.countAlertsNotResolved(currentUser);
    }

    @Operation(summary = "Tail Alert Mean Time to Resolve.", responses = {
            @ApiResponse(responseCode = "200", description = "mean time to resolve",
                    content = @Content(schema = @Schema(implementation = long.class)))
    })
    @GetMapping("/mttr")
    public long getTailAlertsMTTR(@RequestHeader(AUTHORIZATION) String authorizationHeader) throws UserNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        return tailMetricsService.tailAlertsMTTR(currentUser);
    }

    @Operation(summary = "Get MTTR for the last 7 days.", responses = {
            @ApiResponse(responseCode = "200", description = "MTTR data for the last 7 days",
                    content = @Content(schema = @Schema(implementation = TailDatasetMttrResponse.class)))
    })
    @GetMapping("/mttr/last-7-days")
    public TailDatasetMttrResponse getTailMTTRLast7Days(@RequestHeader(AUTHORIZATION) String authorizationHeader) throws UserNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        return tailMetricsService.getTailMTTRLast7Days(currentUser);
    }

    @Operation(summary = "Get tail alerts count per hour for the last 9 hours.", responses = {
            @ApiResponse(responseCode = "200", description = "Hourly tail alert counts",
                    content = @Content(schema = @Schema(implementation = TailAlertsPerHourResponse.class)))
    })
    @PostMapping("/hourly")
    public TailAlertsPerHourResponse getTailAlertsHourly(@RequestHeader(AUTHORIZATION) String authorizationHeader,
                                                         @RequestBody TimezoneRequest timezoneRequest) throws UserNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        return tailMetricsService.getTailAlertsPerHour(timezoneRequest.getTimezone(), currentUser);
    }

    @Operation(summary = "Get a monthly summary of tail alerts for the past 28 days, categorized by level.", responses = {
            @ApiResponse(responseCode = "200", description = "Monthly tail alert summary",
                    content = @Content(schema = @Schema(implementation = TailMonthlySummaryResponse.class)))
    })
    @PostMapping("/monthly-summary")
    public TailMonthlySummaryResponse getTailMonthlySummary(@RequestHeader(AUTHORIZATION) String authorizationHeader,
                                                            @RequestBody TimezoneRequest timezoneRequest) throws UserNotFoundException {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        return tailMetricsService.getTailMonthlySummary(timezoneRequest.getTimezone(), currentUser);
    }
}
