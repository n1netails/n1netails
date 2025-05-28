package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.service.TailMetricsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TailMetricsControllerTest {

    @Mock
    private TailMetricsService tailMetricsService;

    @InjectMocks
    private TailMetricsController tailMetricsController;

    @Test
    void testGetTailAlertsToday() {
        // Arrange
        List<TailResponse> expectedResponse = Collections.singletonList(new TailResponse());
        when(tailMetricsService.tailAlertsToday()).thenReturn(expectedResponse);

        // Act
        List<TailResponse> actualResponse = tailMetricsController.getTailAlertsToday();

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(tailMetricsService).tailAlertsToday();
    }

    @Test
    void testCountTailAlertsToday() {
        // Arrange
        long expectedResponse = 5L;
        when(tailMetricsService.countAlertsToday()).thenReturn(expectedResponse);

        // Act
        long actualResponse = tailMetricsController.countTailAlertsToday();

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(tailMetricsService).countAlertsToday();
    }

    @Test
    void testGetTailAlertsResolved() {
        // Arrange
        List<TailResponse> expectedResponse = Collections.singletonList(new TailResponse());
        when(tailMetricsService.tailAlertsResolved()).thenReturn(expectedResponse);

        // Act
        List<TailResponse> actualResponse = tailMetricsController.getTailAlertsResolved();

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(tailMetricsService).tailAlertsResolved();
    }

    @Test
    void testCountTailAlertsResolved() {
        // Arrange
        long expectedResponse = 3L;
        when(tailMetricsService.countAlertsResolved()).thenReturn(expectedResponse);

        // Act
        long actualResponse = tailMetricsController.countTailAlertsResolved();

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(tailMetricsService).countAlertsResolved();
    }

    @Test
    void testGetTailAlertsNotResolved() {
        // Arrange
        List<TailResponse> expectedResponse = Collections.singletonList(new TailResponse());
        when(tailMetricsService.tailAlertsNotResolved()).thenReturn(expectedResponse);

        // Act
        List<TailResponse> actualResponse = tailMetricsController.getTailAlertsNotResolved();

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(tailMetricsService).tailAlertsNotResolved();
    }

    @Test
    void testCountTailAlertsNotResolved() {
        // Arrange
        long expectedResponse = 2L;
        when(tailMetricsService.countAlertsNotResolved()).thenReturn(expectedResponse);

        // Act
        long actualResponse = tailMetricsController.countTailAlertsNotResolved();

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(tailMetricsService).countAlertsNotResolved();
    }

    @Test
    void testGetTailAlertsMTTR() {
        // Arrange
        long expectedResponse = 1000L;
        when(tailMetricsService.tailAlertsMTTR()).thenReturn(expectedResponse);

        // Act
        long actualResponse = tailMetricsController.getTailAlertsMTTR();

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(tailMetricsService).tailAlertsMTTR();
    }
}
