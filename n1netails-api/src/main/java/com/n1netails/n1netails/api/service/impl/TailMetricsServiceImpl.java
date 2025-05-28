package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.repository.TailRepository;
import com.n1netails.n1netails.api.service.TailMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("tailMetricsService")
public class TailMetricsServiceImpl implements TailMetricsService {

    private final TailRepository tailRepository;

    @Override
    public List<TailResponse> tailAlertsToday() {
        // todo update the service impl
        // implement the tail alerts that showed up on the current day
        return List.of();
    }

    @Override
    public List<TailResponse> tailAlertsResolved() {
        // todo update the service impl
        // implement the number of tail alerts resolved
        return List.of();
    }

    @Override
    public List<TailResponse> tailAlertsNotResolved() {
        // todo update the service impl
        // implement the number of tail alerts not resolved
        return List.of();
    }

    @Override
    public long tailAlertsMTTR() {
        // todo update the service impl
        // implement mean time to resolve an alert tail
        // reference TailEntity timestamp & resolvedTimestamp
        return 0;
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
