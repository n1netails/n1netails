package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.response.*;

import java.time.DateTimeException;
import java.util.List;

/**
 * Service providing metrics, statistics, and analytical views for tail alerts.
 *
 * <p>Provides read-only data for dashboards and reports, including alert counts,
 * trend summaries, resolution stats, and dataset views.</p>
 */
public interface TailMetricsService {

    /**
     * Retrieves tail alerts created today based on the user's timezone.
     *
     * @param timezone the user's timezone identifier
     * @param userId optional user identifier to scope alerts
     * @param organizationIds optional organization identifiers to scope alerts
     * @return a list of tail responses created today; may be empty if no alerts exist
     * @throws DateTimeException if the timezone identifier is invalid
     */
    List<TailResponse> tailAlertsToday(String timezone, Long userId, List<Long> organizationIds);

    /**
     * Counts tail alerts created today based on the user's timezone.
     *
     * @param timezone the user's timezone identifier
     * @param userId optional user identifier to scope alerts
     * @param organizationIds optional organization identifiers to scope alerts
     * @return the number of alerts created today (zero if none exist)
     * @throws DateTimeException if the timezone identifier is invalid
     */
    long countAlertsToday(String timezone, Long userId, List<Long> organizationIds);

    /**
     * Retrieves all resolved tail alerts.
     *
     * @param userId optional user identifier to scope alerts
     * @param organizationIds optional organization identifiers to scope alerts
     * @return a list of resolved tail responses; may be empty if none exist
     */
    List<TailResponse> tailAlertsResolved(Long userId, List<Long> organizationIds);

    /**
     * Counts all resolved tail alerts.
     *
     * @param userId optional user identifier to scope alerts
     * @param organizationIds optional organization identifiers to scope alerts
     * @return the number of resolved alerts (zero if none exist)
     */
    long countAlertsResolved(Long userId, List<Long> organizationIds);

    /**
     * Retrieves all unresolved tail alerts.
     *
     * @param userId optional user identifier to scope alerts
     * @param organizationIds optional organization identifiers to scope alerts
     * @return a list of unresolved tail responses; may be empty if none exist
     */
    List<TailResponse> tailAlertsNotResolved(Long userId, List<Long> organizationIds);

    /**
     * Counts all unresolved tail alerts.
     *
     * @param userId optional user identifier to scope alerts
     * @param organizationIds optional organization identifiers to scope alerts
     * @return the number of unresolved alerts (zero if none exist)
     */
    long countAlertsNotResolved(Long userId, List<Long> organizationIds);

    /**
     * Calculates the Mean Time To Resolution (MTTR) for resolved alerts.
     *
     * <p>The value represents the average resolution duration in seconds.</p>
     *
     * @param userId optional user identifier to scope alerts
     * @param organizationIds optional organization identifiers to scope alerts
     * @return the average MTTR in seconds, or {@code 0} if no resolved alerts exist
     */
    long tailAlertsMTTR(Long userId, List<Long> organizationIds);

    /**
     * Retrieves the number of tail alerts per hour for the last 9 hours.
     *
     * @param timezone the user's timezone identifier
     * @param userId optional user identifier to scope alerts
     * @param organizationIds optional organization identifiers to scope alerts
     * @return hourly alert count dataset; counts may be zero for all hours
     * @throws DateTimeException if the timezone identifier is invalid
     */
    TailAlertsPerHourResponse getTailAlertsPerHour(String timezone, Long userId, List<Long> organizationIds);

    /**
     * Retrieves a monthly summary of tail alerts grouped by level.
     *
     * <p>The summary spans the last 29 days.</p>
     *
     * @param timezone the user's timezone identifier
     * @param userId optional user identifier to scope alerts
     * @param organizationIds optional organization identifiers to scope alerts
     * @return monthly summary dataset; datasets may contain zero values
     * @throws DateTimeException if the timezone identifier is invalid
     */
    TailMonthlySummaryResponse getTailMonthlySummary(String timezone, Long userId, List<Long> organizationIds);

    /**
     * Retrieves the MTTR trend for the last 7 days.
     *
     * <p>The response contains daily average MTTR values in hours.</p>
     *
     * @param userId optional user identifier to scope alerts
     * @param organizationIds optional organization identifiers to scope alerts
     * @return MTTR dataset for the last 7 days; never {@code null}
     */
    TailDatasetMttrResponse getTailMTTRLast7Days(Long userId, List<Long> organizationIds);

    /**
     * Retrieves hourly tail alert counts grouped by alert level.
     *
     * @param timezone the user's timezone identifier
     * @param userId optional user identifier to scope alerts
     * @param organizationIds optional organization identifiers to scope alerts
     * @return hourly alert counts grouped by level; datasets may contain zero values
     * @throws DateTimeException if the timezone identifier is invalid
     */
    TailAlertsHourlyByLevelResponse getTailAlertsHourlyByLevel(String timezone, Long userId, List<Long> organizationIds);

    /**
     * Retrieves a breakdown of tail alerts by resolution status.
     *
     * @param userId optional user identifier to scope alerts
     * @param organizationIds optional organization identifiers to scope alerts
     * @return resolution status distribution dataset; counts may be zero
     */
    TailResolutionStatusResponse getTailResolutionStatus(Long userId, List<Long> organizationIds);
}
