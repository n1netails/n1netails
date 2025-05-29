package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.response.TailAlertsPerHourResponse;
import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.repository.TailRepository;
import com.n1netails.n1netails.api.service.TailMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("tailMetricsService")
public class TailMetricsServiceImpl implements TailMetricsService {

    private final TailRepository tailRepository;

    @Override
    public List<TailResponse> tailAlertsToday() {
        // This method is not part of the current subtask requirements for timezone handling.
        // Assuming it might be updated later or is intentionally left as is.
        // For now, it will use the old way of fetching today's alerts if not specified otherwise.
        // If it needs to be timezone-aware, it would require a timezone parameter similar to countAlertsToday.
        log.info("Fetching all tail alerts for today (UTC based).");
        Instant startOfDayUtc = LocalDate.now(ZoneOffset.UTC).atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant endOfDayUtc = LocalDate.now(ZoneOffset.UTC).atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);

        List<TailEntity> tailEntities = tailRepository.findByTimestampBetween(startOfDayUtc, endOfDayUtc);
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
        List<TailEntity> tailEntities = tailRepository.findAllByResolvedTimestampIsNotNull();
        if (tailEntities == null || tailEntities.isEmpty()) {
            return 0;
        }

        long totalDurationInSeconds = 0;
        int validTailsCount = 0;

        for (TailEntity entity : tailEntities) {
            if (entity.getTimestamp() != null && entity.getResolvedTimestamp() != null) {
                Duration duration = Duration.between(entity.getTimestamp(), entity.getResolvedTimestamp());
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
        log.info("fetch complete, {} timestamps found", timestamps.size());

        List<Integer> data = new ArrayList<>(Collections.nCopies(9, 0));
        List<String> labels = new ArrayList<>();

        // Labels should be in user's local time hour
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:00").withZone(userZone);

        for (int i = 8; i >= 0; i--) {
            // Generate labels based on user's local time
            ZonedDateTime labelTime = userZonedNow.minus(i, ChronoUnit.HOURS).truncatedTo(ChronoUnit.HOURS);
            labels.add(formatter.format(labelTime));
        }

        // Process UTC timestamps from DB
        // The bucketing logic needs to align with the UTC hours defined by nineHoursAgoUtc and nowUtc
        // For each `timestamp` (which is UTC) from the DB:
        //   Determine which of the 9 hourly UTC buckets it falls into.
        //   The buckets are effectively [nineHoursAgoUtc, nineHoursAgoUtc+1H), [nineHoursAgoUtc+1H, nineHoursAgoUtc+2H), ...

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

    private Instant getYesterdayStartOfDay() {
        return LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    private Instant getTomorrowStartOfDay() {
        return LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }
}
