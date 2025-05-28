package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.service.TailMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/metrics/tails")
@RequiredArgsConstructor
public class TailMetricsController {

    private final TailMetricsService tailMetricsService;

    @GetMapping("/today")
    public List<TailResponse> getTailAlertsToday() {
        return tailMetricsService.tailAlertsToday();
    }

    @GetMapping("/today/count")
    public long countTailAlertsToday() {
        return tailMetricsService.countAlertsToday();
    }

    @GetMapping("/resolved")
    public List<TailResponse> getTailAlertsResolved() {
        return tailMetricsService.tailAlertsResolved();
    }

    @GetMapping("/resolved/count")
    public long countTailAlertsResolved() {
        return tailMetricsService.countAlertsResolved();
    }

    @GetMapping("/not-resolved")
    public List<TailResponse> getTailAlertsNotResolved() {
        return tailMetricsService.tailAlertsNotResolved();
    }

    @GetMapping("/not-resolved/count")
    public long countTailAlertsNotResolved() {
        return tailMetricsService.countAlertsNotResolved();
    }

    @GetMapping("/mttr")
    public long getTailAlertsMTTR() {
        return tailMetricsService.tailAlertsMTTR();
    }
}
