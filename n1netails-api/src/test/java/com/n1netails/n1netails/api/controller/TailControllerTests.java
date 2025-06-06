package com.n1netails.n1netails.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.ResolveTailRequest;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
import com.n1netails.n1netails.api.model.response.TailResponse;
import com.n1netails.n1netails.api.model.dto.TailSummary; // For ResolveTailRequest
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.service.TailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TailController.class)
@ExtendWith(MockitoExtension.class)
class TailControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TailService tailService;

    @MockBean
    private AuthorizationService authorizationService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private UserPrincipal n1netailsUserPrincipal;
    private UserPrincipal orgUserPrincipal;
    private UserPrincipal adminUserPrincipal;
    private UsersEntity n1netailsUserEntity;
    private UsersEntity orgUserEntity;
    private TailResponse tailResponse1;
    private TailResponse tailResponse2;

    @BeforeEach
    void setUp() {
        n1netailsUserPrincipal = new UserPrincipal(1L, "n1user", "ROLE_USER", new ArrayList<>());
        orgUserPrincipal = new UserPrincipal(2L, "orguser", "ROLE_USER", new ArrayList<>());
        adminUserPrincipal = new UserPrincipal(3L, "admin", "ROLE_ADMIN", new ArrayList<>());

        OrganizationEntity n1Org = new OrganizationEntity(10L, "n1netails", null, null);
        OrganizationEntity otherOrg = new OrganizationEntity(20L, "otherorg", null, null);

        n1netailsUserEntity = new UsersEntity();
        n1netailsUserEntity.setId(1L);
        n1netailsUserEntity.setUsername("n1user");
        Set<OrganizationEntity> n1Orgs = new HashSet<>();
        n1Orgs.add(n1Org);
        n1netailsUserEntity.setOrganizations(n1Orgs);

        orgUserEntity = new UsersEntity();
        orgUserEntity.setId(2L);
        orgUserEntity.setUsername("orguser");
        Set<OrganizationEntity> otherOrgsSet = new HashSet<>();
        otherOrgsSet.add(otherOrg);
        orgUserEntity.setOrganizations(otherOrgsSet);


        tailResponse1 = new TailResponse(1L, "Tail 1", "Desc 1", Instant.now(), null, 1L, "n1user", "details", "HIGH", "ALERT", "NEW", Collections.emptyMap(), 10L); // n1netails org, owned by n1user
        tailResponse2 = new TailResponse(2L, "Tail 2", "Desc 2", Instant.now(), null, 2L, "orguser", "details", "LOW", "LOG", "NEW", Collections.emptyMap(), 20L); // other org, owned by orguser

        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(adminUserPrincipal); // Default to admin
        when(userRepository.findUserById(1L)).thenReturn(n1netailsUserEntity);
        when(userRepository.findUserById(2L)).thenReturn(orgUserEntity);
    }

    // --- getById Tests ---
    @Test
    void getById_asN1neTailsUser_ownerAccess() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(n1netailsUserPrincipal);
        when(tailService.getTailById(1L)).thenReturn(tailResponse1);
        when(authorizationService.isTailOwner(n1netailsUserPrincipal, 1L)).thenReturn(true);

        mockMvc.perform(get("/ninetails/tail/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
        verify(authorizationService).isTailOwner(n1netailsUserPrincipal, 1L);
    }

    @Test
    void getById_asN1neTailsUser_ownerDenied() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(n1netailsUserPrincipal);
        when(tailService.getTailById(2L)).thenReturn(tailResponse2); // Tail from other org
        when(authorizationService.isTailOwner(n1netailsUserPrincipal, 2L)).thenReturn(false);


        mockMvc.perform(get("/ninetails/tail/2")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isForbidden()); // AccessDeniedException
         verify(authorizationService).isTailOwner(n1netailsUserPrincipal, 2L);
    }

    @Test
    void getById_asOrgUser_belongsToOrg() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(orgUserPrincipal);
        when(tailService.getTailById(2L)).thenReturn(tailResponse2);
        when(authorizationService.belongsToOrganization(orgUserPrincipal, 20L)).thenReturn(true);

        mockMvc.perform(get("/ninetails/tail/2")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
        verify(authorizationService).belongsToOrganization(orgUserPrincipal, 20L);
    }

    @Test
    void getById_asOrgUser_notBelongsToOrg() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(orgUserPrincipal);
        when(tailService.getTailById(1L)).thenReturn(tailResponse1); // Tail from n1netails org
        when(authorizationService.belongsToOrganization(orgUserPrincipal, 10L)).thenReturn(false);

        mockMvc.perform(get("/ninetails/tail/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isForbidden());
        verify(authorizationService).belongsToOrganization(orgUserPrincipal, 10L);
    }

    @Test
    void getById_asAdminUser() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(adminUserPrincipal);
        when(tailService.getTailById(1L)).thenReturn(tailResponse1);

        mockMvc.perform(get("/ninetails/tail/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk());
        verify(authorizationService, never()).isTailOwner(any(), anyLong());
        verify(authorizationService, never()).belongsToOrganization(any(), anyLong());
    }

    // --- getTailsByPage Tests ---
    @Test
    void getTailsByPage_asN1neTailsUser() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(n1netailsUserPrincipal);
        TailPageRequest request = new TailPageRequest();
        // Mock service to return a page of all tails, controller will filter via request modification
        PageImpl<TailResponse> page = new PageImpl<>(Arrays.asList(tailResponse1, tailResponse2), PageRequest.of(0,10), 2);
        when(tailService.getTails(any(TailPageRequest.class))).thenReturn(page);

        mockMvc.perform(post("/ninetails/tail/page")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Verify the request was modified to include assignedUserId
        verify(tailService).getTails(argThat(req -> req.getAssignedUserId() != null && req.getAssignedUserId().equals(n1netailsUserPrincipal.getId())));
    }

    @Test
    void getTailsByPage_asOrgUser() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(orgUserPrincipal);
        TailPageRequest request = new TailPageRequest();
        PageImpl<TailResponse> page = new PageImpl<>(Arrays.asList(tailResponse1, tailResponse2), PageRequest.of(0,10), 2);
        when(tailService.getTails(any(TailPageRequest.class))).thenReturn(page);

        mockMvc.perform(post("/ninetails/tail/page")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(tailService).getTails(argThat(req -> req.getOrganizationIds() != null && req.getOrganizationIds().contains(20L)));
    }


    // --- getTop9NewestTails Tests ---
    @Test
    void getTop9NewestTails_asN1neTailsUser() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(n1netailsUserPrincipal);
        when(tailService.getTop9NewestTails()).thenReturn(Arrays.asList(tailResponse1, tailResponse2));
        when(authorizationService.isTailOwner(n1netailsUserPrincipal, 1L)).thenReturn(true);
        when(authorizationService.isTailOwner(n1netailsUserPrincipal, 2L)).thenReturn(false);


        mockMvc.perform(get("/ninetails/tail/top9")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$.length()").value(1)); // Only tailResponse1
    }

    @Test
    void getTop9NewestTails_asOrgUser() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(orgUserPrincipal);
        when(tailService.getTop9NewestTails()).thenReturn(Arrays.asList(tailResponse1, tailResponse2));
        // tailResponse1 has orgId 10L, tailResponse2 has orgId 20L. orgUserEntity belongs to org 20L.

        mockMvc.perform(get("/ninetails/tail/top9")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(2L))
                .andExpect(jsonPath("$.length()").value(1)); // Only tailResponse2
    }

    // --- markTailResolved Tests ---
    @Test
    void markTailResolved_asN1neTailsUser_isOwner() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(n1netailsUserPrincipal);
        // tailResponse1 is owned by n1netailsUser (ID 1L) and in org 10L
        when(tailService.getTailById(1L)).thenReturn(tailResponse1);
        when(authorizationService.isTailOwner(n1netailsUserPrincipal, 1L)).thenReturn(true);

        ResolveTailRequest request = new ResolveTailRequest(n1netailsUserPrincipal.getId(), new TailSummary(1L, null,null,null,null,null,null,null,null,10L), "Resolved");

        mockMvc.perform(post("/ninetails/tail/mark/resolved")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
        verify(tailService).markResolved(any(ResolveTailRequest.class));
    }

    @Test
    void markTailResolved_asOrgUser_isOrgAdmin() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(orgUserPrincipal);
        // tailResponse2 is owned by orgUser (ID 2L) and in org 20L
        when(tailService.getTailById(2L)).thenReturn(tailResponse2);
        when(authorizationService.isTailOwner(orgUserPrincipal, 2L)).thenReturn(false); // Not owner
        when(authorizationService.isOrganizationAdmin(orgUserPrincipal, 20L)).thenReturn(true); // Is org admin

        ResolveTailRequest request = new ResolveTailRequest(orgUserPrincipal.getId(), new TailSummary(2L, null,null,null,null,null,null,null,null,20L), "Resolved by org admin");

        mockMvc.perform(post("/ninetails/tail/mark/resolved")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());
        verify(tailService).markResolved(any(ResolveTailRequest.class));
    }

    @Test
    void markTailResolved_asOrgUser_accessDenied() throws Exception {
        when(authorizationService.getCurrentUserPrincipal(anyString())).thenReturn(orgUserPrincipal);
        // tailResponse1 is owned by n1netailsUser (ID 1L) and in org 10L
        // orgUserPrincipal (ID 2L) is trying to resolve it.
        when(tailService.getTailById(1L)).thenReturn(tailResponse1);
        when(authorizationService.isTailOwner(orgUserPrincipal, 1L)).thenReturn(false);
        when(authorizationService.isOrganizationAdmin(orgUserPrincipal, 10L)).thenReturn(false);

        ResolveTailRequest request = new ResolveTailRequest(orgUserPrincipal.getId(), new TailSummary(1L, null,null,null,null,null,null,null,null,10L), "Attempt to resolve");

        mockMvc.perform(post("/ninetails/tail/mark/resolved")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
        verify(tailService, never()).markResolved(any(ResolveTailRequest.class));
    }
}
