package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.response.*;

import java.util.List;

public interface TailMetricsService {

    List<TailResponse> tailAlertsToday(String timezone, UserPrincipal currentUser);
    long countAlertsToday(String timezone, UserPrincipal currentUser);

    List<TailResponse> tailAlertsResolved(UserPrincipal currentUser);
    long countAlertsResolved(UserPrincipal currentUser);

    List<TailResponse> tailAlertsNotResolved(UserPrincipal currentUser);
    long countAlertsNotResolved(UserPrincipal currentUser);

    long tailAlertsMTTR(UserPrincipal currentUser);

    TailAlertsPerHourResponse getTailAlertsPerHour(String timezone, UserPrincipal currentUser);

    TailMonthlySummaryResponse getTailMonthlySummary(String timezone, UserPrincipal currentUser);

    TailDatasetMttrResponse getTailMTTRLast7Days(UserPrincipal currentUser);
}
