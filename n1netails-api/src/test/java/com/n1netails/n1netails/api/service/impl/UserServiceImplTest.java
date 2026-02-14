package com.n1netails.n1netails.api.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.InvalidRoleException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.UserRegisterRequest;
import com.n1netails.n1netails.api.repository.ForgotPasswordRequestRepository;
import com.n1netails.n1netails.api.repository.OrganizationRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.EmailService;
import com.n1netails.n1netails.api.service.LoginAttemptService;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private LoginAttemptService loginAttemptService;

    @Mock
    private EmailService emailService;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private ForgotPasswordRequestRepository forgotPasswordRequestRepository;

    @InjectMocks
    private UserServiceImpl userServiceImpl;

    private static UsersEntity existingUser;
    private static UserRegisterRequest validRegisterRequest;
    private static OrganizationEntity defaultOrg;
    private static final String ENCODED_PASSWORD = "encodedPassword";

    @BeforeAll
    private static void setUp() {
        existingUser = new UsersEntity();
        existingUser.setId(1L);
        existingUser.setEmail("test@n1netails.com");
        existingUser.setUsername("testuser");
        existingUser.setNotLocked(true);

        defaultOrg = new OrganizationEntity();
        defaultOrg.setName("n1netails");
        defaultOrg.setId(1L);

        validRegisterRequest = new UserRegisterRequest();
        validRegisterRequest.setFirstName("John");
        validRegisterRequest.setLastName("Doe");
        validRegisterRequest.setUsername("johndoe");
        validRegisterRequest.setEmail("john@n1netails.com");
        validRegisterRequest.setPassword("password123");
    }

    @Test
    public void testLoadUserByUsername_ExistingUser_ShouldReturnUserDetails() {

        existingUser.setNotLocked(true);
        when(userRepository.findUserByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));
        when(loginAttemptService.hasExceededMaxAttempts(existingUser.getUsername())).thenReturn(false);

        UserDetails userResult = userServiceImpl.loadUserByUsername(existingUser.getEmail());

        assertNotNull(userResult);
        assertEquals(existingUser.getEmail(), userResult.getUsername());
        verify(userRepository, times(1)).save(any(UsersEntity.class));
    }

    @Test
    public void testLoadUserByUsername_ExistingUser_ShouldUpdateLoginDatesAndReturnUser() {
        existingUser.setNotLocked(true);
        when(userRepository.findUserByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));
        when(loginAttemptService.hasExceededMaxAttempts(existingUser.getUsername())).thenReturn(false);

        UserDetails userResult = userServiceImpl.loadUserByUsername(existingUser.getEmail());

        assertNotNull(userResult);
        assertEquals(existingUser.getEmail(), userResult.getUsername());

        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    public void testLoadUserByUsername_NonExistingUser_ShouldReturnNull() {
        when(userRepository.findUserByEmail(existingUser.getEmail())).thenReturn(Optional.empty());

        UserDetails userResult = userServiceImpl.loadUserByUsername(existingUser.getEmail());

        assertNull(userResult);
        verify(userRepository, never()).save(any());
    }

    @Test
    public void testLoadUserByUsername_UserExceedsAttempts_ShouldLockUser() {
        existingUser.setNotLocked(true);
        when(userRepository.findUserByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));
        when(loginAttemptService.hasExceededMaxAttempts(existingUser.getUsername())).thenReturn(true);

        userServiceImpl.loadUserByUsername(existingUser.getEmail());

        ArgumentCaptor<UsersEntity> userCap = ArgumentCaptor.forClass(UsersEntity.class);
        verify(userRepository).save(userCap.capture());

        assertFalse(userCap.getValue().isNotLocked());
    }

    @Test
    public void testRegister_FirstUserEver_ShouldAssignSuperAdminRole() throws Exception {
        when(userRepository.findUserByEmail(validRegisterRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn(ENCODED_PASSWORD);
        when(userRepository.count()).thenReturn(0L);
        when(organizationRepository.findByName(defaultOrg.getName())).thenReturn(Optional.of(defaultOrg));

        userServiceImpl.register(validRegisterRequest);

        ArgumentCaptor<UsersEntity> userCap = ArgumentCaptor.forClass(UsersEntity.class);
        verify(userRepository).save(userCap.capture());
        assertEquals("ROLE_SUPER_ADMIN", userCap.getValue().getRole());
    }

    @Test
    public void testRegister_ValidRequest_ShouldCreateUserWithDefaultOrg() throws Exception {
        when(userRepository.findUserByEmail(validRegisterRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn(ENCODED_PASSWORD);
        when(userRepository.count()).thenReturn(1L);
        when(organizationRepository.findByName(defaultOrg.getName())).thenReturn(Optional.of(defaultOrg));

        userServiceImpl.register(validRegisterRequest);

        ArgumentCaptor<UsersEntity> userCap = ArgumentCaptor.forClass(UsersEntity.class);
        verify(userRepository, times(1)).save(userCap.capture());
        UsersEntity savedUser = userCap.getValue();
        assertEquals(validRegisterRequest.getEmail(), savedUser.getEmail());
        assertEquals(ENCODED_PASSWORD, savedUser.getPassword());
        assertEquals("ROLE_USER", savedUser.getRole());
        assertTrue(savedUser.getOrganizations().contains(defaultOrg));
        assertNotNull(savedUser.getJoinDate());
    }

    @Test
    public void testRegister_EmailAlreadyExists_ShouldThrowEmailExistException() {

        when(userRepository.findUserByEmail(validRegisterRequest.getEmail())).thenReturn(Optional.of(existingUser));
        assertThrows(EmailExistException.class, () -> {
            userServiceImpl.register(validRegisterRequest);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    public void testRegister_EmptyPassword_ShouldSetEncodedPasswordAsEmptyString() throws Exception {
        UserRegisterRequest emptyPassRequest = new UserRegisterRequest();
        emptyPassRequest.setEmail("empty@n1netails.com");
        emptyPassRequest.setPassword("");
        emptyPassRequest.setUsername("emptyuser");

        when(userRepository.findUserByEmail(emptyPassRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.count()).thenReturn(1L);
        when(organizationRepository.findByName("n1netails")).thenReturn(Optional.of(defaultOrg));

        userServiceImpl.register(emptyPassRequest);

        ArgumentCaptor<UsersEntity> userCap = ArgumentCaptor.forClass(UsersEntity.class);
        verify(userRepository).save(userCap.capture());
        assertEquals("", userCap.getValue().getPassword());
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    public void testRegister_UserWithNullOrganizations_ShouldInitializeHashSet() throws Exception {
        when(userRepository.findUserByEmail(validRegisterRequest.getEmail())).thenReturn(Optional.empty());
        when(organizationRepository.findByName("n1netails")).thenReturn(Optional.of(defaultOrg));

        userServiceImpl.register(validRegisterRequest);

        ArgumentCaptor<UsersEntity> userCap = ArgumentCaptor.forClass(UsersEntity.class);
        verify(userRepository).save(userCap.capture());
        assertNotNull(userCap.getValue().getOrganizations());
        assertTrue(userCap.getValue().getOrganizations().contains(defaultOrg));
    }

    @Test
    public void testRegister_PasswordBlankAndOrgsNull_ShouldInitializeDefaults() throws Exception {
        UserRegisterRequest blankPassRequest = new UserRegisterRequest();
        blankPassRequest.setEmail("blank@n1netails.com");
        blankPassRequest.setPassword("   ");
        blankPassRequest.setUsername("blankuser");

        when(userRepository.findUserByEmail(blankPassRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.count()).thenReturn(1L);
        when(organizationRepository.findByName("n1netails")).thenReturn(Optional.of(defaultOrg));

        userServiceImpl.register(blankPassRequest);

        ArgumentCaptor<UsersEntity> userCap = ArgumentCaptor.forClass(UsersEntity.class);
        verify(userRepository).save(userCap.capture());

        assertEquals("", userCap.getValue().getPassword());

        assertNotNull(userCap.getValue().getOrganizations());
        assertTrue(userCap.getValue().getOrganizations().contains(defaultOrg));
    }

    @Test
    public void testUpdatePassword_ExistingUser_ShouldUpdateAndSave() throws UserNotFoundException {
        String newPass = "newSecret@";
        String encodedPass = "newEncodedPass";

        when(userRepository.findUserByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode(newPass)).thenReturn(encodedPass);

        userServiceImpl.updatePassword(existingUser.getEmail(), newPass);

        ArgumentCaptor<UsersEntity> userCap = ArgumentCaptor.forClass(UsersEntity.class);

        verify(userRepository).save(userCap.capture());

        assertEquals(encodedPass, userCap.getValue().getPassword());
    }

    @Test
    public void testCompleteTutorial_ValidUser_ShouldSetTutorialCompleted() throws UserNotFoundException {
        when(userRepository.findUserByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));

        userServiceImpl.completeTutorial(existingUser.getEmail());

        ArgumentCaptor<UsersEntity> userCap = ArgumentCaptor.forClass(UsersEntity.class);

        verify(userRepository).save(userCap.capture());

        assertTrue(userCap.getValue().isTutorialCompleted());
    }

    @Test
    public void testUpdateUserRole_ValidRole_ShouldUpdateAndSave() throws Exception {
        Long userId = 1L;
        String newRole = "ROLE_ADMIN";
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(UsersEntity.class))).thenAnswer(i -> i.getArguments()[0]);

        UsersEntity result = userServiceImpl.updateUserRole(userId, newRole);

        assertEquals("ROLE_ADMIN", result.getRole());
        assertNotNull(result.getAuthorities());
        verify(userRepository).save(existingUser);
    }

    @Test
    public void testUpdateUserRole_InvalidRole_ShouldThrowInvalidRoleException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertThrows(InvalidRoleException.class, () -> {
            userServiceImpl.updateUserRole(userId, "ROLE_INVENTADO");
        });
    }

    @Test
    public void testUpdateUserRole_VariousRoles_ShouldAssignCorrectAuthorities() throws Exception {
        Long userId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        userServiceImpl.updateUserRole(userId, "ROLE_HR");
        verify(userRepository, atLeastOnce()).save(argThat(u -> u.getRole().equals("ROLE_HR")));

        userServiceImpl.updateUserRole(userId, "ROLE_MANAGER");
        verify(userRepository, atLeastOnce()).save(argThat(u -> u.getRole().equals("ROLE_MANAGER")));

        userServiceImpl.updateUserRole(userId, "ROLE_OIDC_USER");
        verify(userRepository, atLeastOnce()).save(argThat(u -> u.getRole().equals("ROLE_OIDC_USER")));
    }

    @Test
    public void testUpdateUserRole_NonExistentRole_ShouldThrowInvalidRoleException() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        assertThrows(InvalidRoleException.class, () -> {
            userServiceImpl.updateUserRole(userId, "NON_EXISTENT_ROLE");
        });
    }

    @Test
    public void testForgotPasswordRequest_UserExists_ShouldSaveRequestAndSendEmail() throws Exception {
        when(userRepository.findUserByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));
        when(forgotPasswordRequestRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        userServiceImpl.forgotPasswordRequest(existingUser.getEmail());

        verify(forgotPasswordRequestRepository, times(1)).save(any());
        verify(emailService, times(1)).sendPasswordResetEmail(any());
    }

    @Test
    public void testForgotPasswordRequest_UserNotFound_ShouldDoNothing() throws Exception {
        when(userRepository.findUserByEmail("unknown@mail.com")).thenReturn(Optional.empty());

        userServiceImpl.forgotPasswordRequest("unknown@mail.com");

        verify(forgotPasswordRequestRepository, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(any());
    }

    @Test
    public void testEditUser_ValidData_ShouldUpdateFields() {
        UsersEntity updateInfo = new UsersEntity();
        updateInfo.setEmail(existingUser.getEmail());
        updateInfo.setFirstName("NewName");
        updateInfo.setLastName("NewLastName");
        updateInfo.setUsername("newuser123");

        when(userRepository.findUserByEmail(existingUser.getEmail())).thenReturn(Optional.of(existingUser));

        UsersEntity result = userServiceImpl.editUser(updateInfo);

        assertEquals("NewName", result.getFirstName());
        assertEquals("newuser123", result.getUsername());
        verify(userRepository).save(existingUser);
    }

    @Test
    public void testRegister_EmailAlreadyTaken_ShouldThrowEmailExistException() {
        String emailInUse = "already@taken.com";

        UsersEntity existingUserInDb = new UsersEntity();
        existingUserInDb.setId(99L);
        existingUserInDb.setEmail(emailInUse);

        when(userRepository.findUserByEmail(emailInUse)).thenReturn(Optional.of(existingUserInDb));

        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail(emailInUse);
        request.setPassword("password123");

        EmailExistException exception = assertThrows(EmailExistException.class, () -> {
            userServiceImpl.register(request);
        });

        assertEquals(UserServiceImpl.EMAIL_ALREADY_EXISTS, exception.getMessage());

        verify(userRepository, never()).save(any(UsersEntity.class));
    }

}
