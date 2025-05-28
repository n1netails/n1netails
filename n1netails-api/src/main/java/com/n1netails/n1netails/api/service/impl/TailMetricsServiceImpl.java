package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.repository.TailRepository;
import com.n1netails.n1netails.api.service.TailMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
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
        List<TailEntity> tailEntities = tailRepository.findByTimestampBetween(getStartOfDay(), getEndOfDay());
        if (tailEntities == null || tailEntities.isEmpty()) {
            return List.of();
        }
        return tailEntities.stream()
                .map(this::mapToTailResponse)
                .collect(Collectors.toList());
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

    private Instant getStartOfDay() {
        return LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    private Instant getEndOfDay() {
        return LocalDate.now().atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);
    }

    private Instant getYesterdayStartOfDay() {
        return LocalDate.now().minusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }

    private Instant getTomorrowStartOfDay() {
        return LocalDate.now().plusDays(1).atStartOfDay().toInstant(ZoneOffset.UTC);
    }
}
