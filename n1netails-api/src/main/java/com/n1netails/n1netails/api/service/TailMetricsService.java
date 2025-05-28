package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.response.TailResponse;

import java.util.List;

public interface TailMetricsService {

    List<TailResponse> tailAlertsToday();
    List<TailResponse> tailAlertsResolved();
    List<TailResponse> tailAlertsNotResolved();
    long tailAlertsMTTR();
}
