package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.response.TailAlertsPerHourResponse;
import com.n1netails.n1netails.api.repository.TailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TailMetricsServiceImplTest {

    @Mock
    private TailRepository tailRepository;

    @InjectMocks
    private TailMetricsServiceImpl tailMetricsService;

    private final String TEST_TIMEZONE_NY = "America/New_York";
    private final String TEST_TIMEZONE_PARIS = "Europe/Paris";

    @BeforeEach
    void setUp() {
        // Mock any general behavior if needed
    }

    @Test
    void countAlertsToday_shouldUseCorrectTimezoneConvertedToUTC() {
        // Given
        ZoneId testZone = ZoneId.of(TEST_TIMEZONE_NY);
        LocalDate todayInTestZone = LocalDate.now(testZone);
        Instant expectedStartOfDayUtc = todayInTestZone.atStartOfDay(testZone).toInstant();
        Instant expectedEndOfDayUtc = todayInTestZone.atTime(LocalTime.MAX).atZone(testZone).toInstant();

        when(tailRepository.countByTimestampBetween(any(Instant.class), any(Instant.class))).thenReturn(5L);

        // When
        long count = tailMetricsService.countAlertsToday(TEST_TIMEZONE_NY);

        // Then
        assertEquals(5L, count);

        ArgumentCaptor<Instant> startTimeCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> endTimeCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(tailRepository).countByTimestampBetween(startTimeCaptor.capture(), endTimeCaptor.capture());

        Instant actualStartOfDayUtc = startTimeCaptor.getValue();
        Instant actualEndOfDayUtc = endTimeCaptor.getValue();

        // Assert that the captured instants are within a small tolerance, e.g., a few seconds,
        // to account for execution time between calculating expected and actual values.
        // For start of day, it should be very close.
        // For end of day, it can vary slightly due to LocalTime.MAX precision.
        assertTrue(Math.abs(expectedStartOfDayUtc.toEpochMilli() - actualStartOfDayUtc.toEpochMilli()) < 1000,
                "Start of day UTC does not match. Expected: " + expectedStartOfDayUtc + ", Actual: " + actualStartOfDayUtc);

        // For end of day, compare up to seconds precision.
        // Truncating to seconds for comparison as LocalTime.MAX can have nanosecond differences.
        assertEquals(expectedEndOfDayUtc.truncatedTo(ChronoUnit.SECONDS), actualEndOfDayUtc.truncatedTo(ChronoUnit.SECONDS),
                "End of day UTC does not match. Expected: " + expectedEndOfDayUtc + ", Actual: " + actualEndOfDayUtc);
    }


    @Test
    void getTailAlertsPerHour_shouldUseCorrectTimezoneForLabelsAndQueryRange() {
        // Given
        ZoneId testZoneParis = ZoneId.of(TEST_TIMEZONE_PARIS);
        ZonedDateTime nowInParis = ZonedDateTime.now(testZoneParis);

        Instant nowUtc = nowInParis.toInstant();
        Instant nineHoursAgoUtc = nowUtc.minus(9, ChronoUnit.HOURS);

        // Sample timestamps in UTC for the last 9 hours
        List<Instant> mockTimestamps = Arrays.asList(
                nowUtc.minus(1, ChronoUnit.HOURS),
                nowUtc.minus(3, ChronoUnit.HOURS),
                nowUtc.minus(8, ChronoUnit.HOURS)
        );
        when(tailRepository.findOnlyTimestampsBetween(any(Instant.class), any(Instant.class))).thenReturn(mockTimestamps);

        // When
        TailAlertsPerHourResponse response = tailMetricsService.getTailAlertsPerHour(TEST_TIMEZONE_PARIS);

        // Then
        assertNotNull(response);
        assertNotNull(response.getLabels());
        assertNotNull(response.getData());
        assertEquals(9, response.getLabels().size());
        assertEquals(9, response.getData().size());

        // Verify repository call arguments
        ArgumentCaptor<Instant> startTimeCaptor = ArgumentCaptor.forClass(Instant.class);
        ArgumentCaptor<Instant> endTimeCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(tailRepository).findOnlyTimestampsBetween(startTimeCaptor.capture(), endTimeCaptor.capture());

        Instant actualQueryStartUtc = startTimeCaptor.getValue();
        Instant actualQueryEndUtc = endTimeCaptor.getValue();

        // Assert that the query window is roughly 9 hours
        assertTrue(Math.abs(ChronoUnit.HOURS.between(actualQueryStartUtc, actualQueryEndUtc) - 9) <= 1, // Allow for slight execution diff
                "Query window should be approximately 9 hours. Start: " + actualQueryStartUtc + ", End: " + actualQueryEndUtc);
        // More precise check: end time should be very close to nowUtc
        assertTrue(Math.abs(actualQueryEndUtc.toEpochMilli() - nowUtc.toEpochMilli()) < 1000);


        // Verify label formatting
        DateTimeFormatter expectedFormatter = DateTimeFormatter.ofPattern("HH:00").withZone(testZoneParis);
        // The last label should be the current hour in Paris time
        String expectedLastLabel = expectedFormatter.format(nowInParis.truncatedTo(ChronoUnit.HOURS));
        assertEquals(expectedLastLabel, response.getLabels().get(8));

        // The first label should be 8 hours before the current hour in Paris time
        String expectedFirstLabel = expectedFormatter.format(nowInParis.minus(8, ChronoUnit.HOURS).truncatedTo(ChronoUnit.HOURS));
        assertEquals(expectedFirstLabel, response.getLabels().get(0));

        // Example: Check if one of the labels matches an expected hour in Paris time
        // This confirms the DateTimeFormatter used the correct zone
        boolean parisTimeLabelFound = false;
        for (int i = 0; i < 9; i++) {
            ZonedDateTime labelTimeInParis = nowInParis.minus(8 - i, ChronoUnit.HOURS).truncatedTo(ChronoUnit.HOURS);
            String expectedLabel = expectedFormatter.format(labelTimeInParis);
            if (response.getLabels().get(i).equals(expectedLabel)) {
                parisTimeLabelFound = true;
            }
        }
        assertTrue(parisTimeLabelFound, "Labels do not seem to be formatted in Paris time.");
    }
}
