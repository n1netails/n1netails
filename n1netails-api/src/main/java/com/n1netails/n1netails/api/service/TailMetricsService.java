package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.response.TailAlertsPerHourResponse;
import com.n1netails.n1netails.api.model.response.TailMonthlySummaryResponse;
import com.n1netails.n1netails.api.model.response.TailResponse;

import java.util.List;

public interface TailMetricsService {

    List<TailResponse> tailAlertsToday(String timezone);
    long countAlertsToday(String timezone);

    List<TailResponse> tailAlertsResolved();
    long countAlertsResolved();

    List<TailResponse> tailAlertsNotResolved();
    long countAlertsNotResolved();

    long tailAlertsMTTR();

    TailAlertsPerHourResponse getTailAlertsPerHour(String timezone);

    TailMonthlySummaryResponse getTailMonthlySummary(String timezone);

    TailDatasetResponse getTailMTTRLast7Days();
}
