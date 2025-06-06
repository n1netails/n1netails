package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.exception.type.UnauthorizedException;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.entity.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.ResolveTailRequest;
import com.n1netails.n1netails.api.model.request.TailPageRequest;
import com.n1netails.n1netails.api.model.core.TailStatus;
import com.n1netails.n1netails.api.model.dto.TailSummary;
import com.n1netails.n1netails.api.repository.NoteRepository;
import com.n1netails.n1netails.api.repository.TailLevelRepository;
import com.n1netails.n1netails.api.repository.TailRepository;
import com.n1netails.n1netails.api.repository.TailStatusRepository;
import com.n1netails.n1netails.api.repository.TailTypeRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.AuthorizationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TailServiceImplTest {

    @Mock
    private TailRepository tailRepository;
    @Mock
    private UserRepository usersRepository;
    @Mock
    private TailLevelRepository levelRepository; // Added, though not directly used in auth logic
    @Mock
    private TailTypeRepository typeRepository;   // Added, though not directly used in auth logic
    @Mock
    private TailStatusRepository statusRepository;
    @Mock
    private NoteRepository noteRepository;
    @Mock
    private AuthorizationService authorizationService;

    @InjectMocks
    private TailServiceImpl tailService;

    private UserPrincipal mockUserPrincipal;
    private TailEntity mockTailEntity;
    private static final String N1NETAILS_ORG_NAME = "n1netails";
    private static final String OTHER_ORG_NAME = "otherOrg";
    private static final Long USER_ID = 1L;
    private static final Long OTHER_USER_ID = 2L;
    private static final Long TAIL_ID = 100L;
    private static final Long N1NETAILS_ORG_ID = 10L;
    private static final Long OTHER_ORG_ID = 20L;

    @BeforeEach
    void setUp() {
        mockUserPrincipal = new UserPrincipal();
        mockUserPrincipal.setId(USER_ID);
        // Basic setup, will be overridden in tests as needed
        mockUserPrincipal.setOrganizationName(OTHER_ORG_NAME);
        mockUserPrincipal.setOrganizationIds(Collections.singletonList(OTHER_ORG_ID));


        mockTailEntity = new TailEntity();
        mockTailEntity.setId(TAIL_ID);
        mockTailEntity.setAssignedUserId(USER_ID); // Default owner
        mockTailEntity.setOrganizationId(OTHER_ORG_ID); // Default org
        // Mock other necessary fields for TailEntity if setTailResponse requires them
        // For now, focusing on IDs used in auth.
         mockTailEntity.setLevel(new com.n1netails.n1netails.api.model.entity.TailLevelEntity());
         mockTailEntity.setType(new com.n1netails.n1netails.api.model.entity.TailTypeEntity());
         mockTailEntity.setStatus(new com.n1netails.n1netails.api.model.entity.TailStatusEntity());
         // Mock user for setTailResponse -> getAssignedUsername
         UsersEntity mockUser = new UsersEntity();
         mockUser.setUsername("testuser");
         when(usersRepository.findUserById(anyLong())).thenReturn(mockUser);

    }

    // --- Tests for getTailById ---

    @Test
    void getTailById_userInOrg_notN1neTails_shouldAllow() {
        //when(authorizationService.getCurrentUserPrincipal()).thenReturn(mockUserPrincipal); // Removed
        when(tailRepository.findById(TAIL_ID)).thenReturn(Optional.of(mockTailEntity));
        when(authorizationService.belongsToOrganization(mockUserPrincipal, OTHER_ORG_ID)).thenReturn(true);

        assertNotNull(tailService.getTailById(TAIL_ID, mockUserPrincipal)); // Pass mockUserPrincipal
        verify(authorizationService).belongsToOrganization(mockUserPrincipal, OTHER_ORG_ID);
    }

    @Test
    void getTailById_userNotInOrg_shouldThrowUnauthorized() {
        //when(authorizationService.getCurrentUserPrincipal()).thenReturn(mockUserPrincipal); // Removed
        when(tailRepository.findById(TAIL_ID)).thenReturn(Optional.of(mockTailEntity));
        when(authorizationService.belongsToOrganization(mockUserPrincipal, OTHER_ORG_ID)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> tailService.getTailById(TAIL_ID, mockUserPrincipal)); // Pass mockUserPrincipal
        verify(authorizationService).belongsToOrganization(mockUserPrincipal, OTHER_ORG_ID);
    }

    @Test
    void getTailById_userInN1neTails_isOwner_shouldAllow() {
        mockUserPrincipal.setOrganizationName(N1NETAILS_ORG_NAME);
        mockUserPrincipal.setOrganizationIds(Collections.singletonList(N1NETAILS_ORG_ID));
        mockTailEntity.setOrganizationId(N1NETAILS_ORG_ID); // Tail also in n1netails org
        // USER_ID is already owner by default in setUp

        //when(authorizationService.getCurrentUserPrincipal()).thenReturn(mockUserPrincipal); // Removed
        when(tailRepository.findById(TAIL_ID)).thenReturn(Optional.of(mockTailEntity));
        when(authorizationService.belongsToOrganization(mockUserPrincipal, N1NETAILS_ORG_ID)).thenReturn(true);
        when(authorizationService.isTailOwner(mockUserPrincipal, mockTailEntity)).thenReturn(true);


        assertNotNull(tailService.getTailById(TAIL_ID, mockUserPrincipal)); // Pass mockUserPrincipal
        verify(authorizationService).isTailOwner(mockUserPrincipal, mockTailEntity);
    }

    @Test
    void getTailById_userInN1neTails_notOwner_shouldThrowUnauthorized() {
        mockUserPrincipal.setOrganizationName(N1NETAILS_ORG_NAME);
        mockUserPrincipal.setOrganizationIds(Collections.singletonList(N1NETAILS_ORG_ID));
        mockTailEntity.setOrganizationId(N1NETAILS_ORG_ID);
        mockTailEntity.setAssignedUserId(OTHER_USER_ID); // Different owner

        //when(authorizationService.getCurrentUserPrincipal()).thenReturn(mockUserPrincipal); // Removed
        when(tailRepository.findById(TAIL_ID)).thenReturn(Optional.of(mockTailEntity));
        when(authorizationService.belongsToOrganization(mockUserPrincipal, N1NETAILS_ORG_ID)).thenReturn(true);
        when(authorizationService.isTailOwner(mockUserPrincipal, mockTailEntity)).thenReturn(false);


        assertThrows(UnauthorizedException.class, () -> tailService.getTailById(TAIL_ID, mockUserPrincipal)); // Pass mockUserPrincipal
        verify(authorizationService).isTailOwner(mockUserPrincipal, mockTailEntity);
    }

    @Test
    void getTailById_tailNotFound_shouldThrowTailNotFound() {
        when(tailRepository.findById(TAIL_ID)).thenReturn(Optional.empty());
        // No need to mock authorizationService as it won't be reached
        assertThrows(TailNotFoundException.class, () -> tailService.getTailById(TAIL_ID, mockUserPrincipal)); // Pass mockUserPrincipal
    }

    // --- Tests for getTails ---
    @Test
    void getTails_userInN1neTails_callsRepoWithUserId() throws Exception {
        mockUserPrincipal.setOrganizationName(N1NETAILS_ORG_NAME);
        //when(authorizationService.getCurrentUserPrincipal()).thenReturn(mockUserPrincipal); // Removed
        Page<TailSummary> emptyPage = new PageImpl<>(Collections.emptyList());
        when(tailRepository.findAllBySearchTermAndTailFilters(anyString(), anyList(), anyList(), anyList(), eq(USER_ID), any(Pageable.class)))
                .thenReturn(emptyPage);
        // Mock status/type/level repositories if filters are non-empty
        when(statusRepository.findAll()).thenReturn(Collections.emptyList());
        when(typeRepository.findAll()).thenReturn(Collections.emptyList());
        when(levelRepository.findAll()).thenReturn(Collections.emptyList());


        TailPageRequest request = new TailPageRequest(); // configure as needed
        request.setPage(0);
        request.setSize(10);

        tailService.getTails(request, mockUserPrincipal); // Pass mockUserPrincipal

        verify(tailRepository).findAllBySearchTermAndTailFilters(anyString(), anyList(), anyList(), anyList(), eq(USER_ID), any(Pageable.class));
    }

    @Test
    void getTails_userInOtherOrg_callsRepoWithOrgIds() throws Exception {
        // mockUserPrincipal is already OTHER_ORG_NAME with OTHER_ORG_ID
        //when(authorizationService.getCurrentUserPrincipal()).thenReturn(mockUserPrincipal); // Removed
        Page<TailSummary> emptyPage = new PageImpl<>(Collections.emptyList());
        when(tailRepository.findAllBySearchTermAndTailFilters(anyString(), anyList(), anyList(), anyList(), eq(mockUserPrincipal.getOrganizationIds()), any(Pageable.class)))
                .thenReturn(emptyPage);
        when(statusRepository.findAll()).thenReturn(Collections.emptyList());
        when(typeRepository.findAll()).thenReturn(Collections.emptyList());
        when(levelRepository.findAll()).thenReturn(Collections.emptyList());

        TailPageRequest request = new TailPageRequest();
        request.setPage(0);
        request.setSize(10);

        tailService.getTails(request, mockUserPrincipal); // Pass mockUserPrincipal

        verify(tailRepository).findAllBySearchTermAndTailFilters(anyString(), anyList(), anyList(), anyList(), eq(mockUserPrincipal.getOrganizationIds()), any(Pageable.class));
    }

    @Test
    void getTails_userInOtherOrg_noOrgs_returnsEmptyPage() throws Exception {
        mockUserPrincipal.setOrganizationIds(Collections.emptyList()); // User has no orgs
        //when(authorizationService.getCurrentUserPrincipal()).thenReturn(mockUserPrincipal); // Removed
        // No need to mock tailRepository call as it should be skipped or return empty
        when(statusRepository.findAll()).thenReturn(Collections.emptyList());
        when(typeRepository.findAll()).thenReturn(Collections.emptyList());
        when(levelRepository.findAll()).thenReturn(Collections.emptyList());


        TailPageRequest request = new TailPageRequest();
        request.setPage(0);
        request.setSize(10);

        Page<TailResponse> result = tailService.getTails(request, mockUserPrincipal); // Pass mockUserPrincipal

        assertTrue(result.isEmpty());
        // Verify that the specific repository method for org IDs was NOT called with non-empty list
        // Or verify it was called with an empty list if that's the new behavior
         verify(tailRepository, never()).findAllBySearchTermAndTailFilters(anyString(), anyList(), anyList(), anyList(), argThat(list -> !list.isEmpty()), any(Pageable.class));
    }

    // --- Tests for getTop9NewestTails ---
    @Test
    void getTop9NewestTails_userInN1neTails_callsRepoWithUserId() {
        mockUserPrincipal.setOrganizationName(N1NETAILS_ORG_NAME);
        //when(authorizationService.getCurrentUserPrincipal()).thenReturn(mockUserPrincipal); // Removed
        Page<TailSummary> emptyPage = new PageImpl<>(Collections.emptyList());
        when(tailRepository.findTop9ByAssignedUserIdOrderByTimestampDesc(eq(USER_ID), any(Pageable.class)))
                .thenReturn(emptyPage);

        tailService.getTop9NewestTails(mockUserPrincipal); // Pass mockUserPrincipal

        verify(tailRepository).findTop9ByAssignedUserIdOrderByTimestampDesc(eq(USER_ID), any(Pageable.class));
    }

    @Test
    void getTop9NewestTails_userInOtherOrg_callsRepoWithOrgIds() {
        //when(authorizationService.getCurrentUserPrincipal()).thenReturn(mockUserPrincipal); // Removed
        Page<TailSummary> emptyPage = new PageImpl<>(Collections.emptyList());
        when(tailRepository.findTop9ByOrganizationIdInOrderByTimestampDesc(eq(mockUserPrincipal.getOrganizationIds()), any(Pageable.class)))
                .thenReturn(emptyPage);

        tailService.getTop9NewestTails(mockUserPrincipal); // Pass mockUserPrincipal

        verify(tailRepository).findTop9ByOrganizationIdInOrderByTimestampDesc(eq(mockUserPrincipal.getOrganizationIds()), any(Pageable.class));
    }

    @Test
    void getTop9NewestTails_userInOtherOrg_noOrgs_returnsEmptyList() {
        mockUserPrincipal.setOrganizationIds(Collections.emptyList());
        //when(authorizationService.getCurrentUserPrincipal()).thenReturn(mockUserPrincipal); // Removed
        // No mock for repo call as it might be skipped or called with empty list

        List<TailResponse> result = tailService.getTop9NewestTails(mockUserPrincipal); // Pass mockUserPrincipal

        assertTrue(result.isEmpty());
        verify(tailRepository, never()).findTop9ByOrganizationIdInOrderByTimestampDesc(argThat(list -> !list.isEmpty()), any(Pageable.class));
    }

    // --- Tests for updateStatus ---
    @Test
    void updateStatus_userIsOwner_shouldAllow() {
        //when(authorizationService.getCurrentUserPrincipal()).thenReturn(mockUserPrincipal); // Removed
        when(tailRepository.findById(TAIL_ID)).thenReturn(Optional.of(mockTailEntity));
        // USER_ID is owner by default (mockTailEntity.getAssignedUserId() == USER_ID)
        when(authorizationService.isSelf(mockUserPrincipal, USER_ID)).thenReturn(true);
        // Mock status repository for status update part
        when(statusRepository.findTailStatusByName(anyString())).thenReturn(Optional.empty()); // Simulate new status
        when(statusRepository.save(any(com.n1netails.n1netails.api.model.entity.TailStatusEntity.class))).thenAnswer(i -> i.getArgument(0));


        TailStatus newStatus = new TailStatus();
        newStatus.setName("NEW_STATUS");
        assertNotNull(tailService.updateStatus(TAIL_ID, newStatus, mockUserPrincipal)); // Pass mockUserPrincipal
        verify(authorizationService).isSelf(mockUserPrincipal, USER_ID);
        verify(tailRepository).save(any(TailEntity.class));
    }

    @Test
    void updateStatus_userIsOrgAdmin_shouldAllow() {
        mockTailEntity.setAssignedUserId(OTHER_USER_ID); // Not owner
        //when(authorizationService.getCurrentUserPrincipal()).thenReturn(mockUserPrincipal); // Removed
        when(tailRepository.findById(TAIL_ID)).thenReturn(Optional.of(mockTailEntity));
        when(authorizationService.isSelf(mockUserPrincipal, OTHER_USER_ID)).thenReturn(false);
        when(authorizationService.isOrganizationAdmin(mockUserPrincipal, OTHER_ORG_ID)).thenReturn(true);
        when(statusRepository.findTailStatusByName(anyString())).thenReturn(Optional.empty());
        when(statusRepository.save(any(com.n1netails.n1netails.api.model.entity.TailStatusEntity.class))).thenAnswer(i -> i.getArgument(0));


        TailStatus newStatus = new TailStatus();
        newStatus.setName("NEW_STATUS");
        assertNotNull(tailService.updateStatus(TAIL_ID, newStatus, mockUserPrincipal)); // Pass mockUserPrincipal
        verify(authorizationService).isOrganizationAdmin(mockUserPrincipal, OTHER_ORG_ID);
        verify(tailRepository).save(any(TailEntity.class));
    }

    @Test
    void updateStatus_notOwnerNorAdmin_shouldThrowUnauthorized() {
        mockTailEntity.setAssignedUserId(OTHER_USER_ID); // Not owner
        //when(authorizationService.getCurrentUserPrincipal()).thenReturn(mockUserPrincipal); // Removed
        when(tailRepository.findById(TAIL_ID)).thenReturn(Optional.of(mockTailEntity));
        when(authorizationService.isSelf(mockUserPrincipal, OTHER_USER_ID)).thenReturn(false);
        when(authorizationService.isOrganizationAdmin(mockUserPrincipal, OTHER_ORG_ID)).thenReturn(false);

        TailStatus newStatus = new TailStatus();
        newStatus.setName("NEW_STATUS");
        assertThrows(UnauthorizedException.class, () -> tailService.updateStatus(TAIL_ID, newStatus, mockUserPrincipal)); // Pass mockUserPrincipal
        verify(tailRepository, never()).save(any(TailEntity.class));
    }

    @Test
    void updateStatus_tailNotFound_shouldThrowTailNotFound() {
        when(tailRepository.findById(TAIL_ID)).thenReturn(Optional.empty());
        TailStatus newStatus = new TailStatus();
        newStatus.setName("NEW_STATUS");
        assertThrows(TailNotFoundException.class, () -> tailService.updateStatus(TAIL_ID, newStatus, mockUserPrincipal)); // Pass mockUserPrincipal
    }


    // --- Tests for markResolved ---
    @Test
    void markResolved_userIsAssignedUser_shouldAllow() throws Exception {
        // USER_ID is assigned by default in mockTailEntity
        ResolveTailRequest request = new ResolveTailRequest(new TailSummary(TAIL_ID, null, null, null, null, USER_ID, null, null, null), USER_ID, "Resolved");
        UsersEntity resolverUser = new UsersEntity();
        resolverUser.setId(USER_ID);

        //when(authorizationService.getCurrentUserPrincipal()).thenReturn(mockUserPrincipal); // Removed
        when(tailRepository.findById(TAIL_ID)).thenReturn(Optional.of(mockTailEntity));
        when(authorizationService.isSelf(mockUserPrincipal, USER_ID)).thenReturn(true);
        when(usersRepository.findUserById(USER_ID)).thenReturn(resolverUser);
        when(statusRepository.findTailStatusByName("RESOLVED")).thenReturn(Optional.of(new com.n1netails.n1netails.api.model.entity.TailStatusEntity()));


        tailService.markResolved(request, mockUserPrincipal); // Pass mockUserPrincipal

        verify(authorizationService).isSelf(mockUserPrincipal, USER_ID);
        verify(tailRepository).save(mockTailEntity);
        verify(noteRepository).save(any(com.n1netails.n1netails.api.model.entity.NoteEntity.class));
    }

    @Test
    void markResolved_userIsOrgAdmin_shouldAllow() throws Exception {
        mockTailEntity.setAssignedUserId(OTHER_USER_ID); // Current user is not assigned
        ResolveTailRequest request = new ResolveTailRequest(new TailSummary(TAIL_ID, null, null, null, null, OTHER_USER_ID, null, null, null), USER_ID, "Resolved by admin");
        UsersEntity resolverUser = new UsersEntity(); // User performing resolution via request
        resolverUser.setId(USER_ID);


        //when(authorizationService.getCurrentUserPrincipal()).thenReturn(mockUserPrincipal); // Removed
        when(tailRepository.findById(TAIL_ID)).thenReturn(Optional.of(mockTailEntity));
        when(authorizationService.isSelf(mockUserPrincipal, OTHER_USER_ID)).thenReturn(false);
        when(authorizationService.isOrganizationAdmin(mockUserPrincipal, OTHER_ORG_ID)).thenReturn(true);
        when(usersRepository.findUserById(USER_ID)).thenReturn(resolverUser);
        when(statusRepository.findTailStatusByName("RESOLVED")).thenReturn(Optional.of(new com.n1netails.n1netails.api.model.entity.TailStatusEntity()));


        tailService.markResolved(request, mockUserPrincipal); // Pass mockUserPrincipal

        verify(authorizationService).isOrganizationAdmin(mockUserPrincipal, OTHER_ORG_ID);
        verify(tailRepository).save(mockTailEntity);
        verify(noteRepository).save(any(com.n1netails.n1netails.api.model.entity.NoteEntity.class));
    }

    @Test
    void markResolved_notAssignedNorAdmin_shouldThrowUnauthorized() {
        mockTailEntity.setAssignedUserId(OTHER_USER_ID); // Current user is not assigned
        ResolveTailRequest request = new ResolveTailRequest(new TailSummary(TAIL_ID, null, null, null, null, OTHER_USER_ID, null, null, null), USER_ID, "Attempted resolve");

        //when(authorizationService.getCurrentUserPrincipal()).thenReturn(mockUserPrincipal); // Removed
        when(tailRepository.findById(TAIL_ID)).thenReturn(Optional.of(mockTailEntity));
        when(authorizationService.isSelf(mockUserPrincipal, OTHER_USER_ID)).thenReturn(false);
        when(authorizationService.isOrganizationAdmin(mockUserPrincipal, OTHER_ORG_ID)).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> tailService.markResolved(request, mockUserPrincipal)); // Pass mockUserPrincipal
        verify(tailRepository, never()).save(any(TailEntity.class));
    }

    @Test
    void markResolved_tailNotFound_shouldThrowTailNotFound() {
        ResolveTailRequest request = new ResolveTailRequest(new TailSummary(TAIL_ID, null, null, null, null, USER_ID, null, null, null), USER_ID, "Resolve attempt");
        when(tailRepository.findById(TAIL_ID)).thenReturn(Optional.empty());

        assertThrows(TailNotFoundException.class, () -> tailService.markResolved(request, mockUserPrincipal)); // Pass mockUserPrincipal
    }
}
