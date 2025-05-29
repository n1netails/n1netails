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

        log.info("START OF DAY: {}", getStartOfDay());
        log.info("END OF DAY: {}", getEndOfDay());

        List<TailEntity> tailEntities = tailRepository.findByTimestampBetween(getStartOfDay(), getEndOfDay());
        if (tailEntities == null || tailEntities.isEmpty()) {
            return List.of();
        }
        return tailEntities.stream()
                .map(this::mapToTailResponse)
                .collect(Collectors.toList());
    }

    @Override
    public long countAlertsToday() {

        // todo remove
        ZoneId mountainTime = ZoneId.of("America/Denver");

        Instant startOfDay = getStartOfDay();
        ZonedDateTime mountainStartOfDay = startOfDay.atZone(mountainTime);
        log.info("START OF DAY: {} (Mountain Time: {})", startOfDay, mountainStartOfDay.toLocalTime());

        Instant endOfDay = getEndOfDay();
        ZonedDateTime mountainEndOfDay = endOfDay.atZone(mountainTime);
        log.info("END OF DAY: {} (Mountain Time: {})", endOfDay, mountainEndOfDay.toLocalTime());
        // todo remove end

        return tailRepository.countByTimestampBetween(getStartOfDay(), getEndOfDay());
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
    public TailAlertsPerHourResponse getTailAlertsPerHour() {
        // todo fix timezones
        Instant now = Instant.now();
//        Instant now = Instant.now().atZone(ZoneId.of("America/Denver")).toInstant();

        Instant nineHoursAgo = now.minus(9, ChronoUnit.HOURS);

        // todo remove
        ZoneId mountainTime = ZoneId.of("America/Denver");
        ZonedDateTime mountainNow = now.atZone(mountainTime);
        log.info("Now: {} (Mountain Time: {})", now, mountainNow.toLocalTime());

        ZonedDateTime mountainNineHoursAgo = nineHoursAgo.atZone(mountainTime);
        log.info("Nine hours ago: {} (Mountain Time: {})", nineHoursAgo, mountainNineHoursAgo.toLocalTime());
        // todo remove end

        // Log the time range for debugging
        log.info("Fetching tail alerts from {} to {}", nineHoursAgo, now);

        List<Instant> timestamps = tailRepository.findOnlyTimestampsBetween(nineHoursAgo, now);
        log.info("fetch complete");

        List<Integer> data = new ArrayList<>(Collections.nCopies(9, 0));
        List<String> labels = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:00").withZone(ZoneOffset.UTC);

        // Generate labels for the last 9 hours
        for (int i = 8; i >= 0; i--) { // Corrected loop for chronological order of labels
            Instant hourTimestamp = now.minus(i, ChronoUnit.HOURS).truncatedTo(ChronoUnit.HOURS);
            labels.add(formatter.format(hourTimestamp));
        }

        // Process entities and populate data
        for (Instant timestamp: timestamps) {
            // Calculate which hourly bucket this entity falls into relative to `nineHoursAgo`
            long hoursDifference = ChronoUnit.HOURS.between(nineHoursAgo.truncatedTo(ChronoUnit.HOURS), timestamp.truncatedTo(ChronoUnit.HOURS));
            int index = (int) hoursDifference;
            // Ensure the index is within the bounds of the data list (0 to 8)
            if (index >= 0 && index < data.size()) {
                data.set(index, data.get(index) + 1);
            } else {
                // Log if an entity's timestamp falls outside the expected range
                log.warn("TailEntity with timestamp {} is outside the 9-hour window calculation.", timestamp);
            }
        }

        log.info("Returning TailAlertsPerHourResponse with {} labels and data points: {}", labels.size(), data);
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

    private Instant getStartOfDay() {
        // todo fix issues with timezones
//        return LocalDate.now().atStartOfDay().atZone(ZoneId.of("America/Denver")).toInstant(); // .toInstant(ZoneOffset.UTC);
        return LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    private Instant getEndOfDay() {
        // todo fix issues with timezones
//        return LocalDate.now().atTime(LocalTime.MAX).atZone(ZoneId.of("America/Denver")).toInstant(); // .toInstant(ZoneOffset.UTC);
        return LocalDate.now().atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);
    }

    private Instant getYesterdayStartOfDay() {
        return LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    private Instant getTomorrowStartOfDay() {
        return LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }
}
