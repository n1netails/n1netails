package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.response.*;

import java.util.List;

public interface TailMetricsService {

    List<TailResponse> tailAlertsToday(String timezone, Long userId, List<Long> organizationIds);
    long countAlertsToday(String timezone, Long userId, List<Long> organizationIds);

    List<TailResponse> tailAlertsResolved(Long userId, List<Long> organizationIds);
    long countAlertsResolved(Long userId, List<Long> organizationIds);

    List<TailResponse> tailAlertsNotResolved(Long userId, List<Long> organizationIds);
    long countAlertsNotResolved(Long userId, List<Long> organizationIds);

    long tailAlertsMTTR(Long userId, List<Long> organizationIds);

    TailAlertsPerHourResponse getTailAlertsPerHour(String timezone, Long userId, List<Long> organizationIds);

    TailMonthlySummaryResponse getTailMonthlySummary(String timezone, Long userId, List<Long> organizationIds);

    TailDatasetMttrResponse getTailMTTRLast7Days(Long userId, List<Long> organizationIds);
}
