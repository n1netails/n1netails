package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.dto.TailLevelAndTimestamp;
import com.n1netails.n1netails.api.model.dto.TailTimestampAndResolvedTimestamp;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.entity.TailLevelEntity;
import com.n1netails.n1netails.api.model.response.*;
import com.n1netails.n1netails.api.repository.TailLevelRepository;
import com.n1netails.n1netails.api.repository.TailRepository;
import com.n1netails.n1netails.api.service.TailMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDate; // Added for getTailMTTRLast7Days

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("tailMetricsService")
public class TailMetricsServiceImpl implements TailMetricsService {

    private final TailRepository tailRepository;
    private final TailLevelRepository tailLevelRepository;

    @Override
    public List<TailResponse> tailAlertsToday(String timezoneIdString) {

        ZoneId userZone = ZoneId.of(timezoneIdString); // Handle potential exceptions in real code
        Instant startOfDayUserTzAsUtc = getStartOfDayInUTC(userZone);
        Instant endOfDayUserTzAsUtc = getEndOfDayInUTC(userZone);

        List<TailEntity> tailEntities = tailRepository.findByTimestampBetween(startOfDayUserTzAsUtc, endOfDayUserTzAsUtc);
        if (tailEntities == null || tailEntities.isEmpty()) {
            return List.of();
        }
        return tailEntities.stream()
                .map(this::mapToTailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long countAlertsToday(String timezoneIdString) {
        ZoneId userZone = ZoneId.of(timezoneIdString); // Handle potential exceptions in real code
        Instant startOfDayUserTzAsUtc = getStartOfDayInUTC(userZone);
        Instant endOfDayUserTzAsUtc = getEndOfDayInUTC(userZone);

        log.info("Counting alerts for user timezone {}. UTC Start: {}, UTC End: {}", timezoneIdString, startOfDayUserTzAsUtc, endOfDayUserTzAsUtc);
        return tailRepository.countByTimestampBetween(startOfDayUserTzAsUtc, endOfDayUserTzAsUtc);
    }

    @Override
    public List<TailResponse> tailAlertsResolved() {
        List<TailEntity> tailEntities = tailRepository.findAllByStatusName("RESOLVED");
        if (tailEntities == null || tailEntities.isEmpty()) {
            return List.of();
        }
        return tailEntities.stream()
                .map(this::mapToTailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long countAlertsResolved() {
        return tailRepository.countByStatusName("RESOLVED");
    }

    @Override
    public List<TailResponse> tailAlertsNotResolved() {
        List<TailEntity> tailEntities = tailRepository.findAllByStatusNameNot("RESOLVED");
        if (tailEntities == null || tailEntities.isEmpty()) {
            return List.of();
        }
        return tailEntities.stream()
                .map(this::mapToTailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long countAlertsNotResolved() {
        return tailRepository.countByStatusNameNot("RESOLVED");
    }

    @Override
    public long tailAlertsMTTR() {
        log.info("tailAlertsMTTR");
        List<TailTimestampAndResolvedTimestamp> resolvedTimestampList = tailRepository.findOnlyTimestampAndResolvedTimestampIsNotNull();
        log.info("retrieved list of tail entities");
        if (resolvedTimestampList == null || resolvedTimestampList.isEmpty()) {
            return 0;
        }

        long totalDurationInSeconds = 0;
        int validTailsCount = 0;

        for (TailTimestampAndResolvedTimestamp resolvedTimestamp : resolvedTimestampList) {
            if (resolvedTimestamp.getTimestamp() != null && resolvedTimestamp.getResolvedTimestamp() != null) {
                Duration duration = Duration.between(resolvedTimestamp.getTimestamp(), resolvedTimestamp.getResolvedTimestamp());
                totalDurationInSeconds += duration.getSeconds();
                validTailsCount++;
            }
        }

        if (validTailsCount == 0) {
            return 0;
        }

        return totalDurationInSeconds / validTailsCount;
    }

    @Override
    public TailAlertsPerHourResponse getTailAlertsPerHour(String timezoneIdString) {
        ZoneId userZone = ZoneId.of(timezoneIdString);
        ZonedDateTime userZonedNow = ZonedDateTime.now(userZone);
        Instant nowUtc = userZonedNow.toInstant(); // Current time in UTC
        Instant nineHoursAgoUtc = nowUtc.minus(9, ChronoUnit.HOURS); // 9 hours ago in UTC

        log.info("Fetching tail alerts for user timezone {}. Querying UTC from {} to {}", timezoneIdString, nineHoursAgoUtc, nowUtc);

        List<Instant> timestamps = tailRepository.findOnlyTimestampsBetween(nineHoursAgoUtc, nowUtc);
        timestamps.forEach(instant -> log.info("ts: {}", instant));

        log.info("fetch complete, {} timestamps found", timestamps.size());

        List<Integer> data = new ArrayList<>(Collections.nCopies(10, 0));
        List<String> labels = new ArrayList<>();

        // Labels should be in user's local time hour
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:00").withZone(userZone);

        // Generate labels for the last 9 hours
        for (int i = 9; i >= 0; i--) {
            // Generate labels based on user's local time
            ZonedDateTime labelTime = userZonedNow.minusHours(i).truncatedTo(ChronoUnit.HOURS);
            labels.add(formatter.format(labelTime));
        }

        // bucket alerts by hours
        Instant hourlyBucketStartUtc = nineHoursAgoUtc.truncatedTo(ChronoUnit.HOURS);
        for (Instant timestamp : timestamps) {
            long hoursDifference = ChronoUnit.HOURS.between(hourlyBucketStartUtc, timestamp.truncatedTo(ChronoUnit.HOURS));
            int index = (int) hoursDifference;
            if (index >= 0 && index < data.size()) {
                data.set(index, data.get(index) + 1);
            } else {
                log.warn("Timestamp {} is outside the 9-hour UTC window calculation relative to {}.", timestamp, hourlyBucketStartUtc);
            }
        }

        log.info("Returning TailAlertsPerHourResponse for timezone {} with {} labels and data points: {}", timezoneIdString, labels.size(), data);
        return new TailAlertsPerHourResponse(labels, data);
    }

    @Override
    public TailMonthlySummaryResponse getTailMonthlySummary(String timezoneIdString) {
        log.info("getTailMonthlySummary");

        ZoneId userZone = ZoneId.of(timezoneIdString);
        LocalDate today = LocalDate.now(userZone);
        List<String> labels = new ArrayList<>();
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.US);

        for (int i = 0; i < 29; i++) {
            labels.add(today.minusDays(28 - i).format(dayFormatter));
        }

        Instant endDate = today.plusDays(1).atStartOfDay().atZone(userZone).toInstant();
        Instant startDate = today.minusDays(28).atStartOfDay().atZone(userZone).toInstant();

        log.info("attempting to get list of tail levels and timestamps between");
        List<TailLevelAndTimestamp> tailLevelAndTimestampList = tailRepository.findOnlyLevelAndTimestampsBetween(startDate, endDate);

        log.info("attempting to get list of tail level entities");
        List<TailLevelEntity> allLevels = tailLevelRepository.findAll();

        Map<String, Map<String, Integer>> levelDateCounts = new HashMap<>();
        DateTimeFormatter tailDateFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.US).withZone(ZoneId.systemDefault());

        for (TailLevelAndTimestamp tail: tailLevelAndTimestampList) {
            if (tail.getTimestamp() != null && tail.getLevel() != null && tail.getLevel().getName() != null) {
                String tailDateStr = tailDateFormatter.format(tail.getTimestamp());
                String levelName = tail.getLevel().getName();
                levelDateCounts.computeIfAbsent(levelName, k -> new HashMap<>())
                        .merge(tailDateStr, 1, Integer::sum);
            } else {
                log.warn("Level {} is outside range. timestamp {}", tail.getLevel(), tail.getTimestamp());
            }
        }

        List<TailDatasetResponse> datasets = new ArrayList<>();
        List<String> standardLevels = Arrays.asList("INFO", "SUCCESS", "WARN", "ERROR", "CRITICAL");
        Map<String, Integer> kudaCounts = new HashMap<>(); // For "Kuda" category

        for (TailLevelEntity levelEntity : allLevels) {
            String levelName = levelEntity.getName();
            if (standardLevels.contains(levelName)) {
                List<Integer> levelData = new ArrayList<>();
                for (String label : labels) {
                    levelData.add(levelDateCounts.getOrDefault(levelName, Collections.emptyMap()).getOrDefault(label, 0));
                }
                datasets.add(new TailDatasetResponse(levelName, levelData));
            } else {
                // Aggregate into Kuda
                for (String label : labels) {
                    kudaCounts.merge(label, levelDateCounts.getOrDefault(levelName, Collections.emptyMap()).getOrDefault(label, 0), Integer::sum);
                }
            }
        }

        // Add Kuda dataset if it has any counts
        List<Integer> kudaData = new ArrayList<>();
        boolean hasKudaData = false;
        for (String label : labels) {
            int count = kudaCounts.getOrDefault(label, 0);
            kudaData.add(count);
            if (count > 0) {
                hasKudaData = true;
            }
        }
        if (hasKudaData || allLevels.stream().anyMatch(l -> !standardLevels.contains(l.getName()))) {
            datasets.add(new TailDatasetResponse("Kuda", kudaData));
        }


        // Order datasets
        datasets.sort(Comparator.comparingInt(ds -> {
            switch (ds.getLabel()) {
                case "INFO": return 0;
                case "SUCCESS": return 1;
                case "WARN": return 2;
                case "ERROR": return 3;
                case "CRITICAL": return 4;
                case "Kuda": return 5;
                default: return 6; // Should not happen with current logic
            }
        }));

        return new TailMonthlySummaryResponse(labels, datasets);
    }

    @Override
    public TailDatasetMttrResponse getTailMTTRLast7Days() {
        log.info("Calculating MTTR for the last 7 days");

        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS).truncatedTo(ChronoUnit.DAYS);
        // Query for tails created in the last 7 days that also have a resolved timestamp.
        List<TailTimestampAndResolvedTimestamp> recentTails = tailRepository.findAllByTimestampAfterAndResolvedTimestampIsNotNull(sevenDaysAgo);

        Map<LocalDate, List<Duration>> dailyResolutionTimes = new HashMap<>();
        ZoneId systemZone = ZoneId.systemDefault(); // Or a specific zone if required

        for (TailTimestampAndResolvedTimestamp tail : recentTails) {
            // Both timestamp and resolvedTimestamp are guaranteed to be non-null by the query
            LocalDate creationDay = tail.getTimestamp().atZone(systemZone).toLocalDate();
            // Additional check to ensure the creationDay is within the last 7 days from today,
            // as sevenDaysAgo is fixed at the start of the method.
            if (!creationDay.isBefore(LocalDate.now(systemZone).minusDays(7))) {
                Duration resolutionDuration = Duration.between(tail.getTimestamp(), tail.getResolvedTimestamp());
                dailyResolutionTimes.computeIfAbsent(creationDay, k -> new ArrayList<>()).add(resolutionDuration);
            }
        }

        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd", Locale.US);
        LocalDate today = LocalDate.now(systemZone);

        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            labels.add(day.format(dateFormatter));

            List<Duration> durationsForDay = dailyResolutionTimes.getOrDefault(day, Collections.emptyList());
            if (durationsForDay.isEmpty()) {
                data.add(0.0);
            } else {
                // Calculate average MTTR in hours
                double totalSeconds = durationsForDay.stream().mapToLong(Duration::getSeconds).sum();
                double averageMttrHours = (totalSeconds / (double) durationsForDay.size()) / 3600.0;
                // Round to 2 decimal places
                data.add(Math.round(averageMttrHours * 100.0) / 100.0);
            }
        }
        log.info("Returning TailDatasetResponse for MTTR Last 7 Days with {} labels and {} data points.", labels.size(), data.size());
        return new TailDatasetMttrResponse(labels, data);
    }

    private TailResponse mapToTailResponse(TailEntity entity) {
        if (entity == null) {
            return null;
        }
        TailResponse response = new TailResponse();
        response.setId(entity.getId());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setTimestamp(entity.getTimestamp());
        response.setResolvedTimestamp(entity.getResolvedTimestamp());
        response.setDetails(entity.getDetails());
        response.setLevel(entity.getLevel() != null ? entity.getLevel().getName() : null);
        response.setType(entity.getType() != null ? entity.getType().getName() : null);
        response.setStatus(entity.getStatus() != null ? entity.getStatus().getName() : null);
        response.setAssignedUserId(entity.getAssignedUserId());
        response.setAssignedUsername(null); // As per requirement
        response.setMetadata(null); // As per requirement
        return response;
    }

    // Removed getStartOfDay() and getEndOfDay() as per instructions
    private Instant getStartOfDayInUTC(ZoneId zoneId) {
        return LocalDate.now(zoneId)
                .atStartOfDay()
                .atZone(zoneId)
                .toInstant();
    }

    private Instant getEndOfDayInUTC(ZoneId zoneId) {
        return LocalDate.now(zoneId)
                .atTime(LocalTime.MAX)
                .atZone(zoneId)
                .toInstant();
    }

    private Instant getYesterdayStartOfDay() {
        return LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    private Instant getTomorrowStartOfDay() {
        return LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }
}
