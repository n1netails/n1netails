package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.model.request.TimezoneRequest;
import com.n1netails.n1netails.api.model.response.TailAlertsPerHourResponse;
import com.n1netails.n1netails.api.model.response.TailMonthlySummaryResponse;
import com.n1netails.n1netails.api.model.response.TailResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Tail Metrics Controller", description = "Operations related to Tail Metrics")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(value = "/api/metrics/tails", produces = APPLICATION_JSON)
public class TailMetricsController {

    private final TailMetricsService tailMetricsService;

    @Operation(summary = "Get tail alerts that occurred today.", responses = {
            @ApiResponse(responseCode = "200", description = "List of tail alerts",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TailResponse.class))))
    })
    @PostMapping("/today")
    public List<TailResponse> getTailAlertsToday(@RequestBody TimezoneRequest timezoneRequest) {
        return tailMetricsService.tailAlertsToday(timezoneRequest.getTimezone());
    }

    @Operation(summary = "Count tail alerts that occurred today.", responses = {
            @ApiResponse(responseCode = "200", description = "Total count of tail alerts",
                    content = @Content(schema = @Schema(implementation = long.class)))
    })
    @PostMapping("/today/count")
    public long countTailAlertsToday(@RequestBody TimezoneRequest timezoneRequest) {
        return tailMetricsService.countAlertsToday(timezoneRequest.getTimezone());
    }

    @Operation(summary = "Get tail alerts that are resolved.", responses = {
            @ApiResponse(responseCode = "200", description = "List of tail alerts",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TailResponse.class))))
    })
    @GetMapping("/resolved")
    public List<TailResponse> getTailAlertsResolved() {
        return tailMetricsService.tailAlertsResolved();
    }

    @Operation(summary = "Count tail alerts that are resolved.", responses = {
            @ApiResponse(responseCode = "200", description = "Total count of tail alerts",
                    content = @Content(schema = @Schema(implementation = long.class)))
    })
    @GetMapping("/resolved/count")
    public long countTailAlertsResolved() {
        return tailMetricsService.countAlertsResolved();
    }

    @Operation(summary = "Get tail alerts that are not resolved.", responses = {
            @ApiResponse(responseCode = "200", description = "List of tail alerts",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TailResponse.class))))
    })
    @GetMapping("/not-resolved")
    public List<TailResponse> getTailAlertsNotResolved() {
        return tailMetricsService.tailAlertsNotResolved();
    }

    @Operation(summary = "Count tail alerts that are not resolved.", responses = {
            @ApiResponse(responseCode = "200", description = "Total count of tail alerts",
                    content = @Content(schema = @Schema(implementation = long.class)))
    })
    @GetMapping("/not-resolved/count")
    public long countTailAlertsNotResolved() {
        return tailMetricsService.countAlertsNotResolved();
    }

    @Operation(summary = "Tail Alert Mean Time to Resolve.", responses = {
            @ApiResponse(responseCode = "200", description = "mean time to resolve",
                    content = @Content(schema = @Schema(implementation = long.class)))
    })
    @GetMapping("/mttr")
    public long getTailAlertsMTTR() {
        return tailMetricsService.tailAlertsMTTR();
    }

    @Operation(summary = "Get tail alerts count per hour for the last 9 hours.", responses = {
            @ApiResponse(responseCode = "200", description = "Hourly tail alert counts",
                    content = @Content(schema = @Schema(implementation = TailAlertsPerHourResponse.class)))
    })
    @PostMapping("/hourly")
    public TailAlertsPerHourResponse getTailAlertsHourly(@RequestBody TimezoneRequest timezoneRequest) {
        return tailMetricsService.getTailAlertsPerHour(timezoneRequest.getTimezone());
    }

    @Operation(summary = "Get a monthly summary of tail alerts for the past 28 days, categorized by level.", responses = {
            @ApiResponse(responseCode = "200", description = "Monthly tail alert summary",
                    content = @Content(schema = @Schema(implementation = TailMonthlySummaryResponse.class)))
    })
    @PostMapping("/monthly-summary")
    public TailMonthlySummaryResponse getTailMonthlySummary(@RequestBody TimezoneRequest timezoneRequest) {
        return tailMetricsService.getTailMonthlySummary(timezoneRequest.getTimezone());
    }
}
