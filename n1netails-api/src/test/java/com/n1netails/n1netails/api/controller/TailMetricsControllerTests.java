package com.n1netails.n1netails.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.TimezoneRequest;
import com.n1netails.n1netails.api.model.response.*;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.service.TailMetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TailMetricsController.class)
@ExtendWith(MockitoExtension.class)
class TailMetricsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TailMetricsService tailMetricsService;

    @MockBean
    private AuthorizationService authorizationService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal n1netailsUserPrincipal;
    private UserPrincipal orgUserPrincipal;
    private UsersEntity n1netailsUserEntity;
    private UsersEntity orgUserEntity;

    private TailResponse tailN1Owned; // Owned by n1netailsUser, orgId 10L
    private TailResponse tailOrgOwned;  // Owned by orgUser, orgId 20L
    private TailResponse tailN1OtherOrg; // Owned by n1netailsUser, but in orgId 30L (for testing n1user only sees own)
    private TailResponse tailOrgOtherOwner; // Owned by different user in orgUser's org (orgId 20L)

    @BeforeEach
    void setUp() {
        n1netailsUserPrincipal = new UserPrincipal(1L, "n1user", "ROLE_USER", new ArrayList<>());
        orgUserPrincipal = new UserPrincipal(2L, "orguser", "ROLE_USER", new ArrayList<>());

        OrganizationEntity n1Org = new OrganizationEntity(10L, "n1netails", null, null);
        OrganizationEntity otherOrg = new OrganizationEntity(20L, "otherorg", null, null);
        OrganizationEntity anotherOrg = new OrganizationEntity(30L, "anotherorg", null, null);


        n1netailsUserEntity = new UsersEntity();
        n1netailsUserEntity.setId(1L);
        n1netailsUserEntity.setUsername("n1user");
        Set<OrganizationEntity> n1Orgs = new HashSet<>();
        n1Orgs.add(n1Org); // n1netails user is primarily in "n1netails" org
        n1Orgs.add(anotherOrg); // Can also be part of another org
        n1netailsUserEntity.setOrganizations(n1Orgs);


        orgUserEntity = new UsersEntity();
        orgUserEntity.setId(2L);
        orgUserEntity.setUsername("orguser");
        Set<OrganizationEntity> otherOrgsSet = new HashSet<>();
        otherOrgsSet.add(otherOrg);
        orgUserEntity.setOrganizations(otherOrgsSet);

        Instant now = Instant.now();
        tailN1Owned = new TailResponse(1L, "N1 Tail 1", "Desc", now.minus(1, ChronoUnit.HOURS), now, 1L, "n1user", "details", "HIGH", "ALERT", "RESOLVED", Collections.emptyMap(), 10L);
        tailOrgOwned = new TailResponse(2L, "Org Tail 1", "Desc", now.minus(2, ChronoUnit.HOURS), now.minus(1, ChronoUnit.HOURS), 2L, "orguser", "details", "LOW", "LOG", "RESOLVED", Collections.emptyMap(), 20L);
        tailN1OtherOrg = new TailResponse(3L, "N1 Tail Other Org", "Desc", now.minus(3, ChronoUnit.HOURS), null, 1L, "n1user", "details", "MED", "EVENT", "NEW", Collections.emptyMap(), 30L);
        tailOrgOtherOwner = new TailResponse(4L, "Org Tail Other Owner", "Desc", now.minus(4, ChronoUnit.HOURS), null, 3L, "otheruser", "details", "HIGH", "ALERT", "NEW", Collections.emptyMap(), 20L);


        when(userRepository.findUserById(1L)).thenReturn(n1netailsUserEntity);
        when(userRepository.findUserById(2L)).thenReturn(orgUserEntity);
    }

    private TimezoneRequest defaultTimezoneRequest() {
        return new TimezoneRequest(ZoneId.systemDefault().getId());
    }

    // --- Test getTailAlertsToday ---
    @Test
    void getTailAlertsToday_asN1User_filtersOwned() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(n1netailsUserPrincipal);
        when(tailMetricsService.tailAlertsToday(anyString())).thenReturn(Arrays.asList(tailN1Owned, tailOrgOwned, tailN1OtherOrg));

        mockMvc.perform(post("/ninetails/metrics/tails/today")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defaultTimezoneRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // tailN1Owned, tailN1OtherOrg (both assigned to user 1)
                .andExpect(jsonPath("$[?(@.id==1)]").exists())
                .andExpect(jsonPath("$[?(@.id==3)]").exists());
    }

    @Test
    void getTailAlertsToday_asOrgUser_filtersByOrg() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(orgUserPrincipal);
        when(tailMetricsService.tailAlertsToday(anyString())).thenReturn(Arrays.asList(tailN1Owned, tailOrgOwned, tailN1OtherOrg, tailOrgOtherOwner));

        mockMvc.perform(post("/ninetails/metrics/tails/today")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defaultTimezoneRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // tailOrgOwned, tailOrgOtherOwner (both in org 20)
                .andExpect(jsonPath("$[?(@.id==2)]").exists())
                .andExpect(jsonPath("$[?(@.id==4)]").exists());
    }

    // --- Test countTailAlertsToday ---
    @Test
    void countTailAlertsToday_asN1User() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(n1netailsUserPrincipal);
        when(tailMetricsService.tailAlertsToday(anyString())).thenReturn(Arrays.asList(tailN1Owned, tailOrgOwned, tailN1OtherOrg));

        mockMvc.perform(post("/ninetails/metrics/tails/today/count")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defaultTimezoneRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void countTailAlertsToday_asOrgUser() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(orgUserPrincipal);
        when(tailMetricsService.tailAlertsToday(anyString())).thenReturn(Arrays.asList(tailN1Owned, tailOrgOwned, tailN1OtherOrg, tailOrgOtherOwner));

        mockMvc.perform(post("/ninetails/metrics/tails/today/count")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defaultTimezoneRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    // --- Test getTailAlertsResolved ---
    @Test
    void getTailAlertsResolved_asN1User() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(n1netailsUserPrincipal);
        // tailN1Owned is resolved and owned by user 1. tailOrgOwned is resolved but not owned by user 1.
        when(tailMetricsService.tailAlertsResolved()).thenReturn(Arrays.asList(tailN1Owned, tailOrgOwned));

        mockMvc.perform(get("/ninetails/metrics/tails/resolved")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(tailN1Owned.getId()));
    }

    @Test
    void getTailAlertsResolved_asOrgUser() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(orgUserPrincipal);
         // tailN1Owned (org 10), tailOrgOwned (org 20)
        when(tailMetricsService.tailAlertsResolved()).thenReturn(Arrays.asList(tailN1Owned, tailOrgOwned, tailOrgOtherOwner));
        // orgUserPrincipal is in org 20. tailOrgOwned and tailOrgOtherOwner (if resolved) should be returned.
        // Let's assume tailOrgOtherOwner is NOT resolved for this test, so only tailOrgOwned.
        List<TailResponse> resolvedTails = Arrays.asList(tailN1Owned, tailOrgOwned); // tailOrgOtherOwner is not resolved
        when(tailMetricsService.tailAlertsResolved()).thenReturn(resolvedTails);


        mockMvc.perform(get("/ninetails/metrics/tails/resolved")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(tailOrgOwned.getId()));
    }

    // --- Test getTailAlertsMTTR (Simplified test focusing on filtering) ---
    @Test
    void getTailAlertsMTTR_asN1User_calculatesBasedOnFiltered() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(n1netailsUserPrincipal);
        // tailN1Owned is resolved, duration 1 hour. tailOrgOwned is resolved, duration 1 hour.
        // n1user should only see tailN1Owned.
        when(tailMetricsService.tailAlertsResolved()).thenReturn(Arrays.asList(tailN1Owned, tailOrgOwned));

        mockMvc.perform(get("/ninetails/metrics/tails/mttr")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(Duration.between(tailN1Owned.getTimestamp(), tailN1Owned.getResolvedTimestamp()).toMillis())));
    }


    // --- Test getTailMTTRLast7Days (Focus on filtering, simplified data for recalculation) ---
    @Test
    void getTailMTTRLast7Days_asOrgUser_recalculatesBasedOnFiltered() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(orgUserPrincipal);
        // tailOrgOwned is resolved today. tailN1Owned also resolved today.
        // orgUser should only see tailOrgOwned.
        List<TailResponse> allResolved = Arrays.asList(tailN1Owned, tailOrgOwned);
        when(tailMetricsService.tailAlertsResolved()).thenReturn(allResolved);

        mockMvc.perform(get("/ninetails/metrics/tails/mttr/last-7-days")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels", hasSize(7)))
                .andExpect(jsonPath("$.data", hasSize(7)))
                // Verify data for today (last element in 'data' list)
                .andExpect(jsonPath("$.data[6]").value((double)Duration.between(tailOrgOwned.getTimestamp(), tailOrgOwned.getResolvedTimestamp()).toMillis()));
    }

    // --- Test getTailAlertsHourly (Focus on filtering before hourly aggregation) ---
    @Test
    void getTailAlertsHourly_asN1User_aggregatesFiltered() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(n1netailsUserPrincipal);
        // tailN1Owned created 1h ago, tailN1OtherOrg 3h ago. tailOrgOwned 2h ago.
        // N1 user owns tailN1Owned and tailN1OtherOrg.
        List<TailResponse> tailsToday = Arrays.asList(tailN1Owned, tailOrgOwned, tailN1OtherOrg);
        when(tailMetricsService.tailAlertsToday(anyString())).thenReturn(tailsToday);

        TimezoneRequest tr = defaultTimezoneRequest();
        ZoneId currentZone = ZoneId.of(tr.getTimezone());
        int hourOfTailN1Owned = tailN1Owned.getTimestamp().atZone(currentZone).getHour();
        int hourOfTailN1OtherOrg = tailN1OtherOrg.getTimestamp().atZone(currentZone).getHour();

        mockMvc.perform(post("/ninetails/metrics/tails/hourly")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(tr)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels", hasSize(9)))
                .andExpect(jsonPath("$.data", hasSize(9)))
                // This part is tricky as labels are hours of day, data needs to match.
                // We expect two tails to contribute to the count.
                // The sum of data array should be 2.
                .andExpect(jsonPath("$.data.sum()", is(2.0))); // Using jsonPath sum for simplicity
    }

    // --- Test getTailMonthlySummary (Focus on filtering before summary generation) ---
    @Test
    void getTailMonthlySummary_asOrgUser_summarizesFiltered() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(orgUserPrincipal);
        // orgUser is in org 20. tailOrgOwned (LOW) and tailOrgOtherOwner (HIGH) are in org 20.
        // tailN1Owned (HIGH) is in org 10.
        List<TailResponse> recentTails = Arrays.asList(tailN1Owned, tailOrgOwned, tailOrgOtherOwner);
        // Assuming tailMetricsService.tailAlertsToday is used as a placeholder for "recent tails"
        when(tailMetricsService.tailAlertsToday(anyString())).thenReturn(recentTails);

        mockMvc.perform(post("/ninetails/metrics/tails/monthly-summary")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(defaultTimezoneRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.labels", hasSize(28))) // 28 day labels
                .andExpect(jsonPath("$.datasets", hasSize(2))) // Should be 2 datasets: LOW and HIGH from filtered tails
                .andExpect(jsonPath("$.datasets[?(@.label=='LOW')].data.sum()", is(1.0))) // tailOrgOwned
                .andExpect(jsonPath("$.datasets[?(@.label=='HIGH')].data.sum()", is(1.0))); // tailOrgOtherOwner
    }
}
