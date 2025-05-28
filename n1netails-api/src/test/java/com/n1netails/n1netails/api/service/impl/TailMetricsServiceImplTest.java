package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.entity.TailLevelEntity;
import com.n1netails.n1netails.api.model.entity.TailStatusEntity;
import com.n1netails.n1netails.api.model.entity.TailTypeEntity;
import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.repository.TailRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TailMetricsServiceImplTest {

    @Mock
    private TailRepository tailRepository;

    @InjectMocks
    private TailMetricsServiceImpl tailMetricsService;

    private TailEntity tailEntity1;
    private TailEntity tailEntity2;

    private Instant startOfDay;
    private Instant endOfDay;

    @BeforeEach
    void setUp() {
        tailEntity1 = new TailEntity();
        tailEntity1.setId(1L);
        tailEntity1.setTitle("Test Tail 1");
        tailEntity1.setDescription("Description 1");
        tailEntity1.setTimestamp(Instant.now().minus(1, ChronoUnit.HOURS));
        tailEntity1.setDetails("Details 1");
        TailLevelEntity level1 = new TailLevelEntity();
        level1.setName("CRITICAL");
        tailEntity1.setLevel(level1);
        TailTypeEntity type1 = new TailTypeEntity();
        type1.setName("ALERT");
        tailEntity1.setType(type1);
        TailStatusEntity status1 = new TailStatusEntity();
        status1.setName("NEW");
        tailEntity1.setStatus(status1);
        tailEntity1.setAssignedUserId(100L);

        tailEntity2 = new TailEntity();
        tailEntity2.setId(2L);
        tailEntity2.setTitle("Test Tail 2");
        tailEntity2.setTimestamp(Instant.now().minus(2, ChronoUnit.HOURS));
        tailEntity2.setResolvedTimestamp(Instant.now());
        TailStatusEntity statusResolved = new TailStatusEntity();
        statusResolved.setName("RESOLVED");
        tailEntity2.setStatus(statusResolved);


        // For tailAlertsToday verification, though we use any(Instant.class) in mock
        startOfDay = LocalDate.now().atStartOfDay().toInstant(ZoneOffset.UTC);
        endOfDay = LocalDate.now().atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);
    }

    @Test
    void tailAlertsToday_shouldReturnTailResponses_whenTailsExist() {
        when(tailRepository.findByTimestampBetween(any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(tailEntity1));

        List<TailResponse> responses = tailMetricsService.tailAlertsToday();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        TailResponse response = responses.get(0);
        assertEquals(tailEntity1.getId(), response.getId());
        assertEquals(tailEntity1.getTitle(), response.getTitle());
        assertEquals("CRITICAL", response.getLevel());
        verify(tailRepository).findByTimestampBetween(any(Instant.class), any(Instant.class));
    }

    @Test
    void tailAlertsToday_shouldReturnEmptyList_whenNoTailsExist() {
        when(tailRepository.findByTimestampBetween(any(Instant.class), any(Instant.class)))
                .thenReturn(Collections.emptyList());

        List<TailResponse> responses = tailMetricsService.tailAlertsToday();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(tailRepository).findByTimestampBetween(any(Instant.class), any(Instant.class));
    }
    
    @Test
    void tailAlertsToday_shouldReturnEmptyList_whenRepositoryReturnsNull() {
        when(tailRepository.findByTimestampBetween(any(Instant.class), any(Instant.class)))
                .thenReturn(null);

        List<TailResponse> responses = tailMetricsService.tailAlertsToday();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(tailRepository).findByTimestampBetween(any(Instant.class), any(Instant.class));
    }

    @Test
    void tailAlertsResolved_shouldReturnResolvedTailResponses() {
        tailEntity1.getStatus().setName("RESOLVED"); // Ensure this entity is resolved for the test
        when(tailRepository.findAllByStatusName("RESOLVED")).thenReturn(List.of(tailEntity1));

        List<TailResponse> responses = tailMetricsService.tailAlertsResolved();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(tailEntity1.getId(), responses.get(0).getId());
        assertEquals("RESOLVED", responses.get(0).getStatus());
        verify(tailRepository).findAllByStatusName("RESOLVED");
    }
    
    @Test
    void tailAlertsResolved_shouldReturnEmptyList_whenNoResolvedTailsExist() {
        when(tailRepository.findAllByStatusName("RESOLVED")).thenReturn(Collections.emptyList());

        List<TailResponse> responses = tailMetricsService.tailAlertsResolved();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(tailRepository).findAllByStatusName("RESOLVED");
    }

    @Test
    void tailAlertsResolved_shouldReturnEmptyList_whenRepositoryReturnsNull() {
        when(tailRepository.findAllByStatusName("RESOLVED")).thenReturn(null);

        List<TailResponse> responses = tailMetricsService.tailAlertsResolved();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(tailRepository).findAllByStatusName("RESOLVED");
    }

    @Test
    void tailAlertsNotResolved_shouldReturnNotResolvedTailResponses() {
        when(tailRepository.findAllByStatusNameNot("RESOLVED")).thenReturn(List.of(tailEntity1)); // tailEntity1 is NEW

        List<TailResponse> responses = tailMetricsService.tailAlertsNotResolved();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(tailEntity1.getId(), responses.get(0).getId());
        assertEquals("NEW", responses.get(0).getStatus());
        verify(tailRepository).findAllByStatusNameNot("RESOLVED");
    }

    @Test
    void tailAlertsNotResolved_shouldReturnEmptyList_whenAllTailsAreResolved() {
        when(tailRepository.findAllByStatusNameNot("RESOLVED")).thenReturn(Collections.emptyList());

        List<TailResponse> responses = tailMetricsService.tailAlertsNotResolved();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(tailRepository).findAllByStatusNameNot("RESOLVED");
    }
    
    @Test
    void tailAlertsNotResolved_shouldReturnEmptyList_whenRepositoryReturnsNull() {
        when(tailRepository.findAllByStatusNameNot("RESOLVED")).thenReturn(null);

        List<TailResponse> responses = tailMetricsService.tailAlertsNotResolved();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
        verify(tailRepository).findAllByStatusNameNot("RESOLVED");
    }

    @Test
    void tailAlertsMTTR_shouldReturnZero_whenRepositoryReturnsEmptyList() {
        when(tailRepository.findAllByResolvedTimestampIsNotNull()).thenReturn(Collections.emptyList());
        long mttr = tailMetricsService.tailAlertsMTTR();
        assertEquals(0, mttr);
        verify(tailRepository).findAllByResolvedTimestampIsNotNull();
    }
    
    @Test
    void tailAlertsMTTR_shouldReturnZero_whenRepositoryReturnsNull() {
        when(tailRepository.findAllByResolvedTimestampIsNotNull()).thenReturn(null);
        long mttr = tailMetricsService.tailAlertsMTTR();
        assertEquals(0, mttr);
        verify(tailRepository).findAllByResolvedTimestampIsNotNull();
    }

    @Test
    void tailAlertsMTTR_shouldCalculateCorrectly_forValidTails() {
        Instant t1_created = Instant.now().minus(2, ChronoUnit.HOURS);
        Instant t1_resolved = Instant.now().minus(1, ChronoUnit.HOURS); // 1 hour duration
        tailEntity1.setTimestamp(t1_created);
        tailEntity1.setResolvedTimestamp(t1_resolved);

        Instant t2_created = Instant.now().minus(4, ChronoUnit.HOURS);
        Instant t2_resolved = Instant.now().minus(2, ChronoUnit.HOURS); // 2 hours duration
        tailEntity2.setTimestamp(t2_created);
        tailEntity2.setResolvedTimestamp(t2_resolved);

        when(tailRepository.findAllByResolvedTimestampIsNotNull()).thenReturn(List.of(tailEntity1, tailEntity2));

        long mttr = tailMetricsService.tailAlertsMTTR();
        long expectedDuration1 = Duration.between(t1_created, t1_resolved).getSeconds();
        long expectedDuration2 = Duration.between(t2_created, t2_resolved).getSeconds();
        assertEquals((expectedDuration1 + expectedDuration2) / 2, mttr);
        verify(tailRepository).findAllByResolvedTimestampIsNotNull();
    }

    @Test
    void tailAlertsMTTR_shouldHandleNullTimestamps_andCalculateForValidTails() {
        Instant t1_created = Instant.now().minus(2, ChronoUnit.HOURS);
        Instant t1_resolved = Instant.now().minus(1, ChronoUnit.HOURS); // 1 hour duration
        tailEntity1.setTimestamp(t1_created);
        tailEntity1.setResolvedTimestamp(t1_resolved);

        tailEntity2.setTimestamp(Instant.now()); // Valid created
        tailEntity2.setResolvedTimestamp(null);   // Null resolved

        TailEntity tailEntity3 = new TailEntity();
        tailEntity3.setTimestamp(null); // Null created
        tailEntity3.setResolvedTimestamp(Instant.now()); // Valid resolved

        when(tailRepository.findAllByResolvedTimestampIsNotNull()).thenReturn(List.of(tailEntity1, tailEntity2, tailEntity3));

        long mttr = tailMetricsService.tailAlertsMTTR();
        long expectedDuration1 = Duration.between(t1_created, t1_resolved).getSeconds();
        assertEquals(expectedDuration1, mttr); // Only tailEntity1 is valid
        verify(tailRepository).findAllByResolvedTimestampIsNotNull();
    }

    @Test
    void tailAlertsMTTR_shouldReturnZero_whenNoTailsHaveBothTimestampsNonNull() {
        tailEntity1.setTimestamp(Instant.now());
        tailEntity1.setResolvedTimestamp(null);

        tailEntity2.setTimestamp(null);
        tailEntity2.setResolvedTimestamp(Instant.now());
        
        TailEntity tailEntity3 = new TailEntity();
        tailEntity3.setTimestamp(null);
        tailEntity3.setResolvedTimestamp(null);


        when(tailRepository.findAllByResolvedTimestampIsNotNull()).thenReturn(List.of(tailEntity1, tailEntity2, tailEntity3));
        long mttr = tailMetricsService.tailAlertsMTTR();
        assertEquals(0, mttr);
        verify(tailRepository).findAllByResolvedTimestampIsNotNull();
    }
    
    @Test
    void mapToTailResponse_fullMappingVerification() {
        // This test implicitly tests mapToTailResponse via a public method
        TailEntity fullEntity = new TailEntity();
        fullEntity.setId(10L);
        fullEntity.setTitle("Full Test Title");
        fullEntity.setDescription("Full test description.");
        fullEntity.setTimestamp(Instant.parse("2023-01-01T10:00:00Z"));
        fullEntity.setResolvedTimestamp(Instant.parse("2023-01-01T11:00:00Z"));
        fullEntity.setDetails("These are the details.");
        fullEntity.setAssignedUserId(55L);

        TailLevelEntity level = new TailLevelEntity();
        level.setName("INFO");
        fullEntity.setLevel(level);

        TailTypeEntity type = new TailTypeEntity();
        type.setName("EVENT");
        fullEntity.setType(type);

        TailStatusEntity status = new TailStatusEntity();
        status.setName("IN_PROGRESS");
        fullEntity.setStatus(status);

        when(tailRepository.findAllByStatusName("IN_PROGRESS")).thenReturn(List.of(fullEntity));

        // Using tailAlertsResolved as an example, any public method would do
        // For this test, we change the status to what the method queries for
        List<TailResponse> responses = tailMetricsService.tailAlertsResolved(); 
        // To make it work with tailAlertsResolved, let's change status to RESOLVED for this specific test case
        status.setName("RESOLVED");
        fullEntity.setStatus(status);
        when(tailRepository.findAllByStatusName("RESOLVED")).thenReturn(List.of(fullEntity));
        responses = tailMetricsService.tailAlertsResolved();


        assertNotNull(responses);
        assertEquals(1, responses.size());
        TailResponse response = responses.get(0);

        assertEquals(fullEntity.getId(), response.getId());
        assertEquals(fullEntity.getTitle(), response.getTitle());
        assertEquals(fullEntity.getDescription(), response.getDescription());
        assertEquals(fullEntity.getTimestamp(), response.getTimestamp());
        assertEquals(fullEntity.getResolvedTimestamp(), response.getResolvedTimestamp());
        assertEquals(fullEntity.getDetails(), response.getDetails());
        assertEquals(fullEntity.getAssignedUserId(), response.getAssignedUserId());
        assertEquals("RESOLVED", response.getStatus()); // Status is RESOLVED
        assertEquals("INFO", response.getLevel());
        assertEquals("EVENT", response.getType());
        assertNull(response.getAssignedUsername()); // As per current mapping logic
        assertNull(response.getMetadata()); // As per current mapping logic
    }
    
    @Test
    void mapToTailResponse_handlesNullNestedEntities() {
        TailEntity minimalEntity = new TailEntity();
        minimalEntity.setId(20L);
        minimalEntity.setTitle("Minimal Test");
        // Level, Type, Status are null

        when(tailRepository.findByTimestampBetween(any(Instant.class), any(Instant.class)))
                .thenReturn(List.of(minimalEntity));
        
        List<TailResponse> responses = tailMetricsService.tailAlertsToday();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        TailResponse response = responses.get(0);

        assertEquals(minimalEntity.getId(), response.getId());
        assertEquals(minimalEntity.getTitle(), response.getTitle());
        assertNull(response.getLevel());
        assertNull(response.getType());
        assertNull(response.getStatus());
    }
}
