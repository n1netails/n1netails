package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.exception.type.OrganizationNotFoundException;
import com.n1netails.n1netails.api.model.entity.N1neTokenEntity;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.model.entity.TailLevelEntity;
import com.n1netails.n1netails.api.model.entity.TailStatusEntity;
import com.n1netails.n1netails.api.model.entity.TailTypeEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.api.repository.N1neTokenRepository;
import com.n1netails.n1netails.api.repository.OrganizationRepository;
import com.n1netails.n1netails.api.repository.TailLevelRepository;
import com.n1netails.n1netails.api.repository.TailRepository;
import com.n1netails.n1netails.api.repository.TailStatusRepository;
import com.n1netails.n1netails.api.repository.TailTypeRepository;
import com.n1netails.n1netails.api.service.EmailService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AlertServiceImplTest
 */
@ExtendWith(MockitoExtension.class)
public class AlertServiceImplTest {

    @Mock
    private TailRepository tailRepository;

    @Mock
    private TailLevelRepository tailLevelRepository;

    @Mock
    private TailTypeRepository tailTypeRepository;

    @Mock
    private TailStatusRepository tailStatusRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private N1neTokenRepository n1neTokenRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AlertServiceImpl alertService;
    private static final String NEW_TAIL_LEVEL_TRACE = "TRACE";
    private static final String NEW_TAIL_TYPE_MONITOR_ALERT = "MONITOR_ALERT";

    private static final UUID n1neTokenUUID = UUID.randomUUID();
    private static OrganizationEntity n1neDefaultOrganization;
    private static UsersEntity user;
    private static N1neTokenEntity n1neToken;
    private static TailLevelEntity infoTailLevel;
    private static TailTypeEntity systemAlertTailType;
    private static TailStatusEntity newTailStatus;
    private static KudaTailRequest fullPopulatedKudaTailRequest;
    private static KudaTailRequest notFullPopulatedKudaTailRequest;
    private static KudaTailRequest emptyInputKudaTailRequest;
    private static KudaTailRequest newValuesKudaTailRequest;

    private static TailLevelEntity newTailLevelTrace;
    private static TailTypeEntity newTailTypeMonitorAlert;

    @BeforeAll
    public static void setUp() {
        n1neDefaultOrganization = new OrganizationEntity(
                1L, "N1ne", "Default n1netails organization", "",
                new Date(), new Date(), new HashSet<>());
        user = new UsersEntity();
        user.setId(1L);
        user.setUserId("1");
        user.setUsername("user-01");
        user.setEmail("user-01@n1netails.com");

        n1neToken = new N1neTokenEntity();
        n1neToken.setToken(n1neTokenUUID);
        n1neToken.setUser(user);
        n1neToken.setOrganization(n1neDefaultOrganization);

        infoTailLevel = new TailLevelEntity();
        infoTailLevel.setName(AlertServiceImpl.INFO);

        systemAlertTailType = new TailTypeEntity();
        systemAlertTailType.setName(AlertServiceImpl.SYSTEM_ALERT);

        newTailStatus = new TailStatusEntity();
        newTailStatus.setName(AlertServiceImpl.NEW);

        fullPopulatedKudaTailRequest = new KudaTailRequest();
        fullPopulatedKudaTailRequest.setTitle("Kuda Title");
        fullPopulatedKudaTailRequest.setDescription("Kuda Description");
        fullPopulatedKudaTailRequest.setDetails("Kuda Details");
        fullPopulatedKudaTailRequest.setTimestamp(new Date().toInstant());
        fullPopulatedKudaTailRequest.setLevel(AlertServiceImpl.INFO);
        fullPopulatedKudaTailRequest.setType(AlertServiceImpl.SYSTEM_ALERT);
        fullPopulatedKudaTailRequest.setMetadata(Map.of("env", "prod", "test", "info"));

        notFullPopulatedKudaTailRequest = new KudaTailRequest();
        notFullPopulatedKudaTailRequest.setTitle("Kuda Title");
        notFullPopulatedKudaTailRequest.setDescription("Kuda Description");
        notFullPopulatedKudaTailRequest.setDetails("Kuda Details");
        notFullPopulatedKudaTailRequest.setTimestamp(new Date().toInstant());
        notFullPopulatedKudaTailRequest.setMetadata(Map.of("env", "prod", "test", "info"));

        emptyInputKudaTailRequest = new KudaTailRequest();
        emptyInputKudaTailRequest.setTitle("Kuda Title");
        emptyInputKudaTailRequest.setDescription("Kuda Description");
        emptyInputKudaTailRequest.setDetails("Kuda Details");
//        emptyInputKudaTailRequest.setTimestamp(new Date().toInstant());
        emptyInputKudaTailRequest.setLevel("");
        emptyInputKudaTailRequest.setType("");
//        emptyInputKudaTailRequest.setMetadata(Map.of("env", "prod", "test", "info"));

        newValuesKudaTailRequest = new KudaTailRequest();
        newValuesKudaTailRequest.setTitle("Kuda Title");
        newValuesKudaTailRequest.setDescription("Kuda Description");
        newValuesKudaTailRequest.setDetails("Kuda Details");
        newValuesKudaTailRequest.setTimestamp(new Date().toInstant());
        // Level and type not in db, will store these new values
        newValuesKudaTailRequest.setLevel(NEW_TAIL_LEVEL_TRACE);
        newValuesKudaTailRequest.setType(NEW_TAIL_TYPE_MONITOR_ALERT);
        newValuesKudaTailRequest.setMetadata(Map.of("env", "prod", "test", "info"));

        newTailLevelTrace = new TailLevelEntity();
        newTailLevelTrace.setName(NEW_TAIL_LEVEL_TRACE);

        newTailTypeMonitorAlert = new TailTypeEntity();
        newTailTypeMonitorAlert.setName(NEW_TAIL_TYPE_MONITOR_ALERT);
    }

//    @Test
//    public void testCreateTail_fullyPopulatedRequest_ShouldCreateNewTailWithFullyPopulatedValues() {
//        // Mock Data
//        when(n1neTokenRepository.findByToken(eq(n1neTokenUUID))).thenReturn(Optional.of(n1neToken));
//        when(tailLevelRepository.findTailLevelByName(eq(AlertServiceImpl.INFO))).thenReturn(Optional.of(infoTailLevel));
//        when(tailTypeRepository.findTailTypeByName(eq(AlertServiceImpl.SYSTEM_ALERT))).thenReturn(Optional.of(systemAlertTailType));
//        when(tailStatusRepository.findTailStatusByName(AlertServiceImpl.NEW)).thenReturn(Optional.of(newTailStatus));
//
//        // Action
//        alertService.createTail(n1neTokenUUID.toString(), fullPopulatedKudaTailRequest);
//
//        // Verify mock call
//        verify(tailLevelRepository, times(1)).findTailLevelByName(eq(AlertServiceImpl.INFO));
//        verify(tailTypeRepository, times(1)).findTailTypeByName(eq(AlertServiceImpl.SYSTEM_ALERT));
//        verify(tailStatusRepository, times(1)).findTailStatusByName(eq(AlertServiceImpl.NEW));
//        // Verify save with expected value
//        ArgumentCaptor<TailEntity> tailEntityArgumentCaptor = ArgumentCaptor.forClass(TailEntity.class);
//        verify(tailRepository, times(1)).save(tailEntityArgumentCaptor.capture());
//        verify(emailService, times(1)).sendAlertEmail(any(), any());
//
//        TailEntity actualSavedTailEntity = tailEntityArgumentCaptor.getValue();
//        assertEquals(fullPopulatedKudaTailRequest.getTitle(), actualSavedTailEntity.getTitle());
//        assertEquals(fullPopulatedKudaTailRequest.getDescription(), actualSavedTailEntity.getDescription());
//        assertEquals(fullPopulatedKudaTailRequest.getDetails(), actualSavedTailEntity.getDetails());
//        assertEquals(AlertServiceImpl.INFO, actualSavedTailEntity.getLevel().getName());
//        assertEquals(AlertServiceImpl.SYSTEM_ALERT, actualSavedTailEntity.getType().getName());
//        assertEquals(AlertServiceImpl.NEW, actualSavedTailEntity.getStatus().getName());
//        assertEquals(user.getId(), actualSavedTailEntity.getAssignedUserId());
//        assertEquals(n1neDefaultOrganization.getId(), actualSavedTailEntity.getOrganization().getId());
//        assertEquals(2, actualSavedTailEntity.getCustomVariables().size());
//    }
//
//    @Test
//    public void testCreateTail_NullRequestAndDefaultValuesNotInDB_ShouldCreateDefaultValuesAndTailWithDefaultValues() {
//        // Mock Data
//        when(n1neTokenRepository.findByToken(eq(n1neTokenUUID))).thenReturn(Optional.of(n1neToken));
//        when(tailLevelRepository.findTailLevelByName(any())).thenReturn(Optional.empty());
//        when(tailLevelRepository.save(any())).thenReturn(infoTailLevel);
//
//        when(tailTypeRepository.findTailTypeByName(any())).thenReturn(Optional.empty());
//        when(tailTypeRepository.save(any())).thenReturn(systemAlertTailType);
//
//        when(tailStatusRepository.save(any())).thenReturn(newTailStatus);
//
//        // Action
//        alertService.createTail(n1neTokenUUID.toString(), notFullPopulatedKudaTailRequest);
//
//        // Verify mock call
//        verify(tailLevelRepository, times(1)).findTailLevelByName(eq(null));
//        verify(tailLevelRepository, times(1)).findTailLevelByName(eq(AlertServiceImpl.INFO));
//
//        verify(tailTypeRepository, times(1)).findTailTypeByName(eq(null));
//        verify(tailTypeRepository, times(1)).findTailTypeByName(eq(AlertServiceImpl.SYSTEM_ALERT));
//
//        verify(tailStatusRepository, never()).findTailStatusByName(eq(null));
//
//        ArgumentCaptor<TailEntity> tailEntityArgumentCaptor = ArgumentCaptor.forClass(TailEntity.class);
//        verify(tailRepository, times(1)).save(tailEntityArgumentCaptor.capture());
//        verify(emailService, times(1)).sendAlertEmail(any(), any());
//
//        TailEntity actualSavedTailEntity = tailEntityArgumentCaptor.getValue();
//        assertEquals(fullPopulatedKudaTailRequest.getTitle(), actualSavedTailEntity.getTitle());
//        assertEquals(fullPopulatedKudaTailRequest.getDescription(), actualSavedTailEntity.getDescription());
//        assertEquals(fullPopulatedKudaTailRequest.getDetails(), actualSavedTailEntity.getDetails());
//        assertEquals(AlertServiceImpl.INFO, actualSavedTailEntity.getLevel().getName());
//        assertEquals(AlertServiceImpl.SYSTEM_ALERT, actualSavedTailEntity.getType().getName());
//        assertEquals(AlertServiceImpl.NEW, actualSavedTailEntity.getStatus().getName());
//        assertEquals(user.getId(), actualSavedTailEntity.getAssignedUserId());
//        assertEquals(n1neDefaultOrganization.getId(), actualSavedTailEntity.getOrganization().getId());
//        assertEquals(2, actualSavedTailEntity.getCustomVariables().size());
//    }
//
//    @Test
//    public void testCreateTail_BlankRequestAndDefaultValuesNotInDB_ShouldCreateDefaultValuesAndTailWithDefaultValues() {
//        when(n1neTokenRepository.findByToken(eq(n1neTokenUUID))).thenReturn(Optional.of(n1neToken));
//
//        when(tailLevelRepository.findTailLevelByName(eq(""))).thenReturn(Optional.empty());
//        when(tailLevelRepository.findTailLevelByName(eq(AlertServiceImpl.INFO))).thenReturn(Optional.empty());
//        when(tailLevelRepository.save(any())).thenReturn(infoTailLevel);
//
//        when(tailTypeRepository.findTailTypeByName(eq(""))).thenReturn(Optional.empty());
//        when(tailTypeRepository.findTailTypeByName(eq(AlertServiceImpl.SYSTEM_ALERT))).thenReturn(Optional.empty());
//        when(tailTypeRepository.save(any())).thenReturn(systemAlertTailType);
//
//        when(tailStatusRepository.findTailStatusByName(eq(AlertServiceImpl.NEW))).thenReturn(Optional.empty());
//        when(tailStatusRepository.save(any())).thenReturn(newTailStatus);
//
//        alertService.createTail(n1neTokenUUID.toString(), emptyInputKudaTailRequest);
//
//        verify(tailLevelRepository, times(1)).findTailLevelByName(eq(""));
//        verify(tailLevelRepository, times(1)).findTailLevelByName(eq(AlertServiceImpl.INFO));
//
//        verify(tailTypeRepository, times(1)).findTailTypeByName(eq(""));
//        verify(tailTypeRepository, times(1)).findTailTypeByName(eq(AlertServiceImpl.SYSTEM_ALERT));
//
//        verify(tailStatusRepository, never()).findTailStatusByName(eq(""));
//
//        ArgumentCaptor<TailEntity> tailEntityArgumentCaptor = ArgumentCaptor.forClass(TailEntity.class);
//        verify(tailRepository, times(1)).save(tailEntityArgumentCaptor.capture());
//        verify(emailService, times(1)).sendAlertEmail(any(), any());
//
//        TailEntity actualSavedTailEntity = tailEntityArgumentCaptor.getValue();
//        assertEquals(emptyInputKudaTailRequest.getTitle(), actualSavedTailEntity.getTitle());
//        assertEquals(emptyInputKudaTailRequest.getDescription(), actualSavedTailEntity.getDescription());
//        assertEquals(emptyInputKudaTailRequest.getDetails(), actualSavedTailEntity.getDetails());
//        assertEquals(AlertServiceImpl.INFO, actualSavedTailEntity.getLevel().getName());
//        assertEquals(AlertServiceImpl.SYSTEM_ALERT, actualSavedTailEntity.getType().getName());
//        assertEquals(AlertServiceImpl.NEW, actualSavedTailEntity.getStatus().getName());
//        assertEquals(user.getId(), actualSavedTailEntity.getAssignedUserId());
//        assertEquals(n1neDefaultOrganization.getId(), actualSavedTailEntity.getOrganization().getId());
//        assertNull(actualSavedTailEntity.getCustomVariables());
//    }
//
//    @Test
//    public void testCreateTail_DefaultValuesNotInDBButNewValuePresentInRequest_ShouldCreateTailWithGivenValues() {
//        when(n1neTokenRepository.findByToken(eq(n1neTokenUUID))).thenReturn(Optional.of(n1neToken));
//
//        when(tailLevelRepository.findTailLevelByName(anyString())).thenReturn(Optional.empty());
//        when(tailLevelRepository.save(any())).thenReturn(newTailLevelTrace);
//
//        when(tailTypeRepository.findTailTypeByName(anyString())).thenReturn(Optional.empty());
//        when(tailTypeRepository.save(any())).thenReturn(newTailTypeMonitorAlert);
//
//        when(tailStatusRepository.findTailStatusByName(eq(AlertServiceImpl.NEW))).thenReturn(Optional.empty());
//        when(tailStatusRepository.save(any())).thenReturn(newTailStatus);
//
//        alertService.createTail(n1neTokenUUID.toString(), newValuesKudaTailRequest);
//
//        verify(tailLevelRepository, times(1)).findTailLevelByName(eq(newValuesKudaTailRequest.getLevel()));
//        ArgumentCaptor<TailLevelEntity> tailLevelArgCaptor = ArgumentCaptor.forClass(TailLevelEntity.class);
//        verify(tailLevelRepository, times(1)).save(tailLevelArgCaptor.capture());
//        TailLevelEntity newTailLevelSaved = tailLevelArgCaptor.getValue();
//        assertEquals(newValuesKudaTailRequest.getLevel(), newTailLevelSaved.getName());
//
//        verify(tailTypeRepository, times(1)).findTailTypeByName(eq(newValuesKudaTailRequest.getType()));
//        verify(tailStatusRepository, times(1)).findTailStatusByName(eq(AlertServiceImpl.NEW));
//        ArgumentCaptor<TailTypeEntity> tailTypeArgCaptor = ArgumentCaptor.forClass(TailTypeEntity.class);
//        verify(tailTypeRepository, times(1)).save(tailTypeArgCaptor.capture());
//        TailTypeEntity newTailTypeSaved = tailTypeArgCaptor.getValue();
//        assertEquals(newValuesKudaTailRequest.getType(), newTailTypeSaved.getName());
//
//        verify(tailStatusRepository, times(1)).findTailStatusByName(eq(AlertServiceImpl.NEW));
//        ArgumentCaptor<TailStatusEntity> tailStatusArgCaptor = ArgumentCaptor.forClass(TailStatusEntity.class);
//        verify(tailStatusRepository, times(1)).save(tailStatusArgCaptor.capture());
//        TailStatusEntity newTailStatusSaved = tailStatusArgCaptor.getValue();
//        assertEquals(AlertServiceImpl.NEW, newTailStatusSaved.getName());
//
//        ArgumentCaptor<TailEntity> tailArgCaptor = ArgumentCaptor.forClass(TailEntity.class);
//        verify(tailRepository, times(1)).save(tailArgCaptor.capture());
//        verify(emailService, times(1)).sendAlertEmail(any(), any());
//
//        TailEntity actualSavedTail = tailArgCaptor.getValue();
//        assertEquals(newValuesKudaTailRequest.getTitle(), actualSavedTail.getTitle());
//        assertEquals(newValuesKudaTailRequest.getDescription(), actualSavedTail.getDescription());
//        assertEquals(newValuesKudaTailRequest.getDetails(), actualSavedTail.getDetails());
//        assertEquals(newValuesKudaTailRequest.getLevel(), actualSavedTail.getLevel().getName());
//        assertEquals(newValuesKudaTailRequest.getType(), actualSavedTail.getType().getName());
//        assertEquals(AlertServiceImpl.NEW, actualSavedTail.getStatus().getName());
//        assertEquals(user.getId(), actualSavedTail.getAssignedUserId());
//        assertEquals(n1neDefaultOrganization.getId(), actualSavedTail.getOrganization().getId());
//        assertEquals(2, actualSavedTail.getCustomVariables().size());
//    }

    @Test
    public void testCreateManualTail_ExistedOrganization_ShouldCreateManualTailWithGivenValues() throws OrganizationNotFoundException {
        when(organizationRepository.findById(eq(1L))).thenReturn(Optional.of(n1neDefaultOrganization));
        when(tailLevelRepository.findTailLevelByName(eq(AlertServiceImpl.INFO))).thenReturn(Optional.of(infoTailLevel));
        when(tailTypeRepository.findTailTypeByName(eq(AlertServiceImpl.SYSTEM_ALERT))).thenReturn(Optional.of(systemAlertTailType));
        when(tailStatusRepository.findTailStatusByName(AlertServiceImpl.NEW)).thenReturn(Optional.of(newTailStatus));

        // Action
        alertService.createManualTail(1L, user, fullPopulatedKudaTailRequest);

        // Verify mock call
        verify(tailLevelRepository, times(1)).findTailLevelByName(eq(AlertServiceImpl.INFO));
        verify(tailTypeRepository, times(1)).findTailTypeByName(eq(AlertServiceImpl.SYSTEM_ALERT));
        verify(tailStatusRepository, times(1)).findTailStatusByName(eq(AlertServiceImpl.NEW));
        // Verify save with expected value
        ArgumentCaptor<TailEntity> tailEntityArgumentCaptor = ArgumentCaptor.forClass(TailEntity.class);
        verify(tailRepository, times(1)).save(tailEntityArgumentCaptor.capture());
        verify(emailService, times(1)).sendAlertEmail(any(), any());

        TailEntity actualSavedTailEntity = tailEntityArgumentCaptor.getValue();

        assertEquals(fullPopulatedKudaTailRequest.getTitle(), actualSavedTailEntity.getTitle());
        assertEquals(fullPopulatedKudaTailRequest.getDescription(), actualSavedTailEntity.getDescription());
        assertEquals(fullPopulatedKudaTailRequest.getDetails(), actualSavedTailEntity.getDetails());
        assertEquals(AlertServiceImpl.INFO, actualSavedTailEntity.getLevel().getName());
        assertEquals(AlertServiceImpl.SYSTEM_ALERT, actualSavedTailEntity.getType().getName());
        assertEquals(AlertServiceImpl.NEW, actualSavedTailEntity.getStatus().getName());
        assertEquals(user.getId(), actualSavedTailEntity.getAssignedUserId());
        assertEquals(n1neDefaultOrganization.getId(), actualSavedTailEntity.getOrganization().getId());
        assertEquals(2, actualSavedTailEntity.getCustomVariables().size());
    }
}
