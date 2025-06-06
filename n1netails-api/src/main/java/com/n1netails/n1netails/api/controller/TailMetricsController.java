package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.model.request.TimezoneRequest;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.TimezoneRequest;
import com.n1netails.n1netails.api.model.response.*;
import com.n1netails.n1netails.api.repository.UserRepository;
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
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.n1netails.n1netails.api.constant.ControllerConstant.APPLICATION_JSON;

@Slf4j
@RequiredArgsConstructor
@Tag(name = "Tail Metrics Controller", description = "Operations related to Tail Metrics")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping(value = "/ninetails/metrics/tails", produces = APPLICATION_JSON)
public class TailMetricsController {

    private final TailMetricsService tailMetricsService;
    private final AuthorizationService authorizationService;
    private final UserRepository userRepository;

    private List<TailResponse> filterTailResponseList(UserPrincipal currentUser, List<TailResponse> tails) {
        if (tails == null || tails.isEmpty()) {
            return Collections.emptyList();
        }
        if (currentUser.getRole().equals("ROLE_USER")) {
            UsersEntity user = userRepository.findUserById(currentUser.getId());
            boolean isN1neTailsOrgMember = user.getOrganizations().stream().anyMatch(org -> "n1netails".equalsIgnoreCase(org.getName()));

            if (isN1neTailsOrgMember) {
                return tails.stream()
                        .filter(tail -> tail.getAssignedUserId() != null && tail.getAssignedUserId().equals(currentUser.getId()))
                        .collect(Collectors.toList());
            } else {
                List<Long> userOrgIds = user.getOrganizations().stream()
                        .map(org -> org.getId())
                        .collect(Collectors.toList());
                return tails.stream()
                        .filter(tail -> tail.getOrganizationId() != null && userOrgIds.contains(tail.getOrganizationId()))
                        .collect(Collectors.toList());
            }
        }
        return tails;
    }

    @Operation(summary = "Get tail alerts that occurred today.", responses = {
            @ApiResponse(responseCode = "200", description = "List of tail alerts",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TailResponse.class))))
    })
    @PostMapping("/today")
    public List<TailResponse> getTailAlertsToday(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody TimezoneRequest timezoneRequest) {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        List<TailResponse> tails = tailMetricsService.tailAlertsToday(timezoneRequest.getTimezone());
        return filterTailResponseList(currentUser, tails);
    }

    @Operation(summary = "Count tail alerts that occurred today.", responses = {
            @ApiResponse(responseCode = "200", description = "Total count of tail alerts",
                    content = @Content(schema = @Schema(implementation = long.class)))
    })
    @PostMapping("/today/count")
    public long countTailAlertsToday(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody TimezoneRequest timezoneRequest) {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        List<TailResponse> tails = tailMetricsService.tailAlertsToday(timezoneRequest.getTimezone());
        List<TailResponse> filteredTails = filterTailResponseList(currentUser, tails);
        return filteredTails.size();
    }

    @Operation(summary = "Get tail alerts that are resolved.", responses = {
            @ApiResponse(responseCode = "200", description = "List of tail alerts",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TailResponse.class))))
    })
    @GetMapping("/resolved")
    public List<TailResponse> getTailAlertsResolved(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        List<TailResponse> tails = tailMetricsService.tailAlertsResolved();
        return filterTailResponseList(currentUser, tails);
    }

    @Operation(summary = "Count tail alerts that are resolved.", responses = {
            @ApiResponse(responseCode = "200", description = "Total count of tail alerts",
                    content = @Content(schema = @Schema(implementation = long.class)))
    })
    @GetMapping("/resolved/count")
    public long countTailAlertsResolved(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        List<TailResponse> tails = tailMetricsService.tailAlertsResolved();
        List<TailResponse> filteredTails = filterTailResponseList(currentUser, tails);
        return filteredTails.size();
    }

    @Operation(summary = "Get tail alerts that are not resolved.", responses = {
            @ApiResponse(responseCode = "200", description = "List of tail alerts",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TailResponse.class))))
    })
    @GetMapping("/not-resolved")
    public List<TailResponse> getTailAlertsNotResolved(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        List<TailResponse> tails = tailMetricsService.tailAlertsNotResolved();
        return filterTailResponseList(currentUser, tails);
    }

    @Operation(summary = "Count tail alerts that are not resolved.", responses = {
            @ApiResponse(responseCode = "200", description = "Total count of tail alerts",
                    content = @Content(schema = @Schema(implementation = long.class)))
    })
    @GetMapping("/not-resolved/count")
    public long countTailAlertsNotResolved(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        List<TailResponse> tails = tailMetricsService.tailAlertsNotResolved();
        List<TailResponse> filteredTails = filterTailResponseList(currentUser, tails);
        return filteredTails.size();
    }

    @Operation(summary = "Tail Alert Mean Time to Resolve.", responses = {
            @ApiResponse(responseCode = "200", description = "mean time to resolve",
                    content = @Content(schema = @Schema(implementation = long.class)))
    })
    @GetMapping("/mttr")
    public long getTailAlertsMTTR(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        List<TailResponse> resolvedTails = tailMetricsService.tailAlertsResolved();
        List<TailResponse> filteredTails = filterTailResponseList(currentUser, resolvedTails);

        if (filteredTails.isEmpty()) {
            return 0L;
        }

        long totalDurationMillis = 0;
        int count = 0;
        for (TailResponse tail : filteredTails) {
            if (tail.getTimestamp() != null && tail.getResolvedTimestamp() != null) {
                totalDurationMillis += Duration.between(tail.getTimestamp(), tail.getResolvedTimestamp()).toMillis();
                count++;
            }
        }
        return count > 0 ? totalDurationMillis / count : 0L;
    }

    @Operation(summary = "Get MTTR for the last 7 days.", responses = {
            @ApiResponse(responseCode = "200", description = "MTTR data for the last 7 days",
                    content = @Content(schema = @Schema(implementation = TailDatasetMttrResponse.class)))
    })
    @GetMapping("/mttr/last-7-days")
    public TailDatasetMttrResponse getTailMTTRLast7Days(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        // Fetch all resolved tails, then filter. This could be inefficient.
        List<TailResponse> allResolvedTails = tailMetricsService.tailAlertsResolved();
        List<TailResponse> filteredResolvedTails = filterTailResponseList(currentUser, allResolvedTails);

        Map<LocalDate, List<Duration>> dailyDurations = new LinkedHashMap<>();
        LocalDate today = LocalDate.now(ZoneId.systemDefault()); // Consider timezone from request if available
        for (int i = 0; i < 7; i++) {
            dailyDurations.put(today.minusDays(i), new ArrayList<>());
        }

        for (TailResponse tail : filteredResolvedTails) {
            if (tail.getTimestamp() != null && tail.getResolvedTimestamp() != null) {
                LocalDate resolvedDate = tail.getResolvedTimestamp().atZone(ZoneId.systemDefault()).toLocalDate();
                if (dailyDurations.containsKey(resolvedDate)) {
                    dailyDurations.get(resolvedDate).add(Duration.between(tail.getTimestamp(), tail.getResolvedTimestamp()));
                }
            }
        }

        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        for (int i = 6; i >= 0; i--) { // Iterate from 6 days ago to today for correct chart order
            LocalDate date = today.minusDays(i);
            labels.add(date.toString());
            List<Duration> durationsOnDate = dailyDurations.get(date);
            if (durationsOnDate != null && !durationsOnDate.isEmpty()) {
                long totalMillis = 0;
                for (Duration d : durationsOnDate) {
                    totalMillis += d.toMillis();
                }
                data.add((double) totalMillis / durationsOnDate.size());
            } else {
                data.add(0.0);
            }
        }
        return new TailDatasetMttrResponse(labels, data);
    }

    @Operation(summary = "Get tail alerts count per hour for the last 9 hours.", responses = {
            @ApiResponse(responseCode = "200", description = "Hourly tail alert counts",
                    content = @Content(schema = @Schema(implementation = TailAlertsPerHourResponse.class)))
    })
    @PostMapping("/hourly")
    public TailAlertsPerHourResponse getTailAlertsHourly(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody TimezoneRequest timezoneRequest) {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);
        // Fetches tails for today, then filters.
        List<TailResponse> tailsToday = tailMetricsService.tailAlertsToday(timezoneRequest.getTimezone());
        List<TailResponse> filteredTailsToday = filterTailResponseList(currentUser, tailsToday);

        Map<Integer, Integer> hourlyCounts = new LinkedHashMap<>();
        ZoneId zoneId = ZoneId.of(timezoneRequest.getTimezone()); // Use specified timezone
        Instant now = Instant.now();

        // Initialize labels and counts for the last 9 hours
        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();
        for (int i = 8; i >= 0; i--) { // Last 9 hours including current
            Instant hourStart = now.truncatedTo(ChronoUnit.HOURS).minus(i, ChronoUnit.HOURS);
            labels.add(String.valueOf(hourStart.atZone(zoneId).getHour())); // Or format as needed
            hourlyCounts.put(hourStart.atZone(zoneId).getHour(), 0);
        }

        Instant nineHoursAgo = now.minus(9, ChronoUnit.HOURS);

        for (TailResponse tail : filteredTailsToday) {
            if (tail.getTimestamp() != null && tail.getTimestamp().isAfter(nineHoursAgo)) {
                int hour = tail.getTimestamp().atZone(zoneId).getHour();
                hourlyCounts.computeIfPresent(hour, (k, v) -> v + 1);
            }
        }

        // Populate data list in the correct order of labels
        for (String labelHourStr : labels) {
            data.add(hourlyCounts.getOrDefault(Integer.parseInt(labelHourStr), 0));
        }

        return new TailAlertsPerHourResponse(labels, data);
    }

    @Operation(summary = "Get a monthly summary of tail alerts for the past 28 days, categorized by level.", responses = {
            @ApiResponse(responseCode = "200", description = "Monthly tail alert summary",
                    content = @Content(schema = @Schema(implementation = TailMonthlySummaryResponse.class)))
    })
    @PostMapping("/monthly-summary")
    public TailMonthlySummaryResponse getTailMonthlySummary(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody TimezoneRequest timezoneRequest) {
        UserPrincipal currentUser = authorizationService.getCurrentUserPrincipal(authorizationHeader);

        // Placeholder: Ideally, fetch tails for the last 28 days.
        // Using tailAlertsToday as a stand-in, actual implementation would need a date range method.
        List<TailResponse> recentTails = tailMetricsService.tailAlertsToday(timezoneRequest.getTimezone()); // Needs to be last 28 days
        List<TailResponse> filteredTails = filterTailResponseList(currentUser, recentTails);

        // Generate labels for the last 28 days
        List<String> labels = new ArrayList<>();
        LocalDate today = LocalDate.now(ZoneId.of(timezoneRequest.getTimezone()));
        for (int i = 0; i < 28; i++) {
            labels.add(today.minusDays(i).toString());
        }
        Collections.reverse(labels); // Show oldest to newest

        // Group tails by level, then by date
        Map<String, Map<LocalDate, Integer>> countsByLevelThenDate = new HashMap<>();
        for (TailResponse tail : filteredTails) {
            if (tail.getTimestamp() != null && tail.getLevel() != null) {
                LocalDate tailDate = tail.getTimestamp().atZone(ZoneId.of(timezoneRequest.getTimezone())).toLocalDate();
                 if (!tailDate.isBefore(today.minusDays(28))) { // Check if the tail is within the last 28 days
                    countsByLevelThenDate.computeIfAbsent(tail.getLevel(), k -> new LinkedHashMap<>())
                                         .merge(tailDate, 1, Integer::sum);
                }
            }
        }

        List<TailDatasetResponse> datasets = new ArrayList<>();
        // Assuming TailLevel.name is what's stored in TailResponse.level
        // We need a list of all possible levels to ensure consistent dataset order if some levels have no tails
        // For now, derive levels from the data found
        Set<String> distinctLevels = filteredTails.stream().map(TailResponse::getLevel).filter(Objects::nonNull).collect(Collectors.toSet());

        for (String level : distinctLevels) {
            List<Integer> levelData = new ArrayList<>();
            Map<LocalDate, Integer> dateCountsForLevel = countsByLevelThenDate.getOrDefault(level, Collections.emptyMap());
            for (String dateLabel : labels) {
                levelData.add(dateCountsForLevel.getOrDefault(LocalDate.parse(dateLabel), 0));
            }
            datasets.add(new TailDatasetResponse(level, levelData));
        }

        return new TailMonthlySummaryResponse(labels, datasets);
    }
}
