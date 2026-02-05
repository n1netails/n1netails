package com.n1netails.n1netails.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.exception.type.EmailExistException;
import com.n1netails.n1netails.api.exception.type.InvalidRoleException;
import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.request.UpdateUserRoleRequest;
import com.n1netails.n1netails.api.model.request.UserLoginRequest;
import com.n1netails.n1netails.api.model.request.UserRegisterRequest;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.service.EmailService;
import com.n1netails.n1netails.api.service.UserService;
import com.n1netails.n1netails.api.util.JwtTokenUtil;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    private final String pathPrefix = "/ninetails/user";
    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String AUTH_HEADER = "Bearer " + VALID_TOKEN;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;


    @MockitoBean
    private UserService userService;
    @MockitoBean
    private AuthenticationManager authenticationManager;
    @MockitoBean
    private AuthorizationService authorizationService;
    @MockitoBean
    private UserRepository userRepository;
    @MockitoBean
    private JwtTokenUtil jwtTokenUtil;
    @MockitoBean
    private JwtDecoder jwtDecoder;
    @MockitoBean
    private EmailService emailService;

    @Test
    void getCurrentUser_validToken_shouldReturnUser() throws Exception {
        // Arrange
        Jwt jwt = mock(Jwt.class);
        UsersEntity enabledUser = new UsersEntity();
        enabledUser.setId(1L);
        enabledUser.setEnabled(true);
        enabledUser.setActive(true);
        enabledUser.setNotLocked(true);

        // Mock Data
        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt);
        when(jwt.getClaim("id")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(enabledUser));

        // Action
        mockMvc.perform(get(pathPrefix + "/self").
                        header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L));

        // Verify mock call
        verify(jwtDecoder, times(1)).decode(VALID_TOKEN);
        verify(userRepository, times(1)).findById(1L);

    }

    @Test
        // NOTE: This test does NOT return 401 as one might expect.
        // Because the @RequestHeader in the controller is required by default,
        // Spring throws a MissingRequestHeaderException before the controller method is called.
        // This results in the def. error response (500 INTERNAL_SERVER_ERROR).
    void getCurrentUser_missingAuthorizationHeader_shouldReturnUnauthorized() throws Exception {

        // Action
        mockMvc.perform(get(pathPrefix + "/self"))
                // Assert
                .andExpect(status().isUnauthorized());

    }

    @Test
    void getCurrentUser_invalidAuthorizationHeader_shouldReturnUnauthorized() throws Exception {
        // Action
        mockMvc.perform(get(pathPrefix + "/self")
                        .header(HttpHeaders.AUTHORIZATION, "InvalidToken"))
                // Assert
                .andExpect(status().isUnauthorized());

        verify(jwtDecoder, never()).decode("InvalidToken");
        verify(userRepository, never()).findById(any());
    }

    @Test
    void getCurrentUser_invalidJwt_shouldReturnUnauthorized() throws Exception {
        // Mock Data
        when(jwtDecoder.decode(VALID_TOKEN)).thenThrow(new JwtException("Invalid token"));

        // Action
        mockMvc.perform(get(pathPrefix + "/self").header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                // Assert
                .andExpect(status().isUnauthorized());

        verify(jwtDecoder, times(1)).decode(VALID_TOKEN);
        verify(userRepository, never()).findById(any());
    }

    @Test
    void getCurrentUser_userNotFound_shouldReturnUnauthorized() throws Exception {
        // Arrange
        Jwt jwt = mock(Jwt.class);

        // Mock Data
        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt);
        when(jwt.getClaim("id")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Action
        mockMvc.perform(get(pathPrefix + "/self").header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                // Assert
                .andExpect(status().isUnauthorized());

        verify(jwtDecoder, times(1)).decode(VALID_TOKEN);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getCurrentUser_disabledUser_shouldReturnUnauthorized() throws Exception {
        // Arrange
        Jwt jwt = mock(Jwt.class);

        UsersEntity disabledUser = new UsersEntity();
        disabledUser.setId(1L);
        disabledUser.setEnabled(false);
        disabledUser.setActive(true);
        disabledUser.setNotLocked(true);

        // Mock Data
        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt);
        when(jwt.getClaim("id")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(disabledUser));

        // Action
        mockMvc.perform(get(pathPrefix + "/self").header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                // Assert
                .andExpect(status().isUnauthorized());

        verify(jwtDecoder, times(1)).decode(VALID_TOKEN);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getCurrentUser_lockedUser_shouldReturnUnauthorized() throws Exception {
        // Arrange
        Jwt jwt = mock(Jwt.class);

        UsersEntity disabledUser = new UsersEntity();
        disabledUser.setId(1L);
        disabledUser.setEnabled(true);
        disabledUser.setActive(true);
        disabledUser.setNotLocked(false);

        // Mock Data
        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt);
        when(jwt.getClaim("id")).thenReturn(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(disabledUser));

        // Action
        mockMvc.perform(get(pathPrefix + "/self").header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                // Assert
                .andExpect(status().isUnauthorized());

        verify(jwtDecoder, times(1)).decode(VALID_TOKEN);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    //Return 500 what okay is but not declared in Swagger
    void getCurrentUser_runTimeException_shouldReturnUnauthorized() throws Exception {
        // Arrange
        Jwt jwt = mock(Jwt.class);

        UsersEntity disabledUser = new UsersEntity();
        disabledUser.setId(1L);
        disabledUser.setEnabled(true);
        disabledUser.setActive(true);
        disabledUser.setNotLocked(false);

        // Mock Data
        when(jwtDecoder.decode(VALID_TOKEN)).thenReturn(jwt);
        when(jwt.getClaim("id")).thenReturn(1L);
        when(userRepository.findById(1L)).thenThrow(new RuntimeException("DB down"));

        // Action
        mockMvc.perform(get(pathPrefix + "/self").header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                // Assert
                .andExpect(status().isUnauthorized());

        verify(jwtDecoder, times(1)).decode(VALID_TOKEN);
        verify(userRepository, times(1)).findById(1L);
    }


    @Test
    void editUser_validUserBody_shouldReturnEditedUser() throws Exception {
        // Arrange
        UsersEntity requestUser = new UsersEntity();
        requestUser.setId(1L);
        requestUser.setEmail("user@example.com");
        requestUser.setUsername("user-01");

        UsersEntity updatedUser = new UsersEntity();
        updatedUser.setId(1L);
        updatedUser.setEmail("user@example.com");
        updatedUser.setUsername("user-01-updated");

        UserPrincipal principal = new UserPrincipal(requestUser);

        // Mock Data
        when(authorizationService.getCurrentUserPrincipal(AUTH_HEADER)).thenReturn(principal);
        when(userService.editUser(any(UsersEntity.class))).thenReturn(updatedUser);

        // Action
        mockMvc.perform(post(pathPrefix + "/edit")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                // Assert
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value("userUpdated@example.com"))
                .andExpect(jsonPath("$.id").value(1L));

        // Verify mock call
        verify(authorizationService, times(1)).getCurrentUserPrincipal(AUTH_HEADER);
        verify(userService, times(1)).editUser(any(UsersEntity.class));
    }

    @Test
        // NOTE: This test does NOT return 401 as one might expect.
        // Because the @RequestHeader in the controller is required by default,
        // Spring throws a MissingRequestHeaderException before the controller method is called.
        // This results in the def. error response (500 INTERNAL_SERVER_ERROR).
    void editUser_missingAuthorizationHeader_shouldReturnUnauthorized() throws Exception {

        // Action
        mockMvc.perform(post(pathPrefix + "/edit"))
                // Assert
                .andExpect(status().isUnauthorized());

        verify(authorizationService, never()).getCurrentUserPrincipal(any());
        verify(userService, never()).editUser(any());
    }

    @Test
        // NOTE: According to the Swagger/OpenAPI this endpoint should return 401 for authentication failures.
        // But because the controller method declares `throws UserNotFoundException` and the exception
        // is not handled, Spring maps it to 404 Not Found by default.
    void editUser_invalidAuthorizationHeader_shouldReturnUnauthorized() throws Exception {

        //Arrange
        UsersEntity requestUser = new UsersEntity();
        requestUser.setEmail("user@example.com");
        requestUser.setUsername("user-01");

        //Mock
        when(authorizationService.getCurrentUserPrincipal("InvalidToken"))
                .thenThrow(new UserNotFoundException("User not found"));

        // Action
        mockMvc.perform(post(pathPrefix + "/edit")
                        .header(HttpHeaders.AUTHORIZATION, "InvalidToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                // Assert
                .andExpect(status().isUnauthorized());

        // Verify
        verify(authorizationService, times(1)).getCurrentUserPrincipal("InvalidToken");
        verify(userService, never()).editUser(any());
    }

    @Test
        // NOTE: According to the Swagger/OpenAPI this endpoint should return 401 for authentication failures.
        // However, because the controller method declares `throws UserNotFoundException` and the exception
        // is not handled, Spring maps it to 404 Not Found by default.
    void editUser_principalNotFound_shouldReturnUnauthorized() throws Exception {

        // Arrange
        UsersEntity requestUser = new UsersEntity();
        requestUser.setId(1L);
        requestUser.setEmail("user@example.com");
        requestUser.setUsername("user-01");

        // Mock
        when(authorizationService.getCurrentUserPrincipal(AUTH_HEADER))
                .thenThrow(new UserNotFoundException("User not found"));

        // Action
        mockMvc.perform(post(pathPrefix + "/edit")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                // Assert
                .andExpect(status().isUnauthorized());

        // Verify
        verify(authorizationService, times(1)).getCurrentUserPrincipal(AUTH_HEADER);
        verify(userService, never()).editUser(any());
    }

    @Test
    void editUser_requestUserDiffersFromPrinciple_shouldReturnUnauthorized() throws Exception {
        // Arrange
        UsersEntity requestUser = new UsersEntity();
        requestUser.setId(1L);
        requestUser.setEmail("user@example.com");
        requestUser.setUsername("user-01");

        UsersEntity principleUser = new UsersEntity();
        principleUser.setId(1L);
        principleUser.setEmail("userPrinciple@example.com");
        principleUser.setUsername("user-01");

        UserPrincipal principal = new UserPrincipal(principleUser);

        // Mock Data
        when(authorizationService.getCurrentUserPrincipal(AUTH_HEADER)).thenReturn(principal);


        // Action
        mockMvc.perform(post(pathPrefix + "/edit")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                // Assert
                .andExpect(status().isUnauthorized());

        // Verify mock call
        verify(authorizationService, times(1)).getCurrentUserPrincipal(AUTH_HEADER);
        verify(userService, never()).editUser(any());
    }

    @Test
        // NOTE: According to the Swagger/OpenAPI this endpoint should return 401 for authentication failures.
        // The problem for the test is the mission email in  @RequestBody UsersEntity user
        // Swagger does not declare what to expect but usually BadRequest 400
    void editUser_missingEmail_shouldReturnBadRequest() throws Exception {

        //Arrange
        UsersEntity requestUser = new UsersEntity();
        requestUser.setId(1L);
        requestUser.setUsername("user-01");

        //Action
        mockMvc.perform(post(pathPrefix + "/edit")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                //Assets
                .andExpect(status().isBadRequest());

        // No service should be called
        verify(authorizationService, never()).getCurrentUserPrincipal(any());
        verify(userService, never()).editUser(any());
    }

    @Test
    void editUser_extraJsonFields_shouldIgnoreAndSucceed() throws Exception {
        //Arrange
        String jsonWithExtra = "{ \"id\":1, \"email\":\"user@example.com\", \"username\":\"user-01\", \"extraField\":\"ignored\" }";

        UsersEntity requestUser = new UsersEntity();
        requestUser.setId(1L);
        requestUser.setEmail("user@example.com");
        requestUser.setUsername("user-01");

        UserPrincipal principal = new UserPrincipal(requestUser);

        //Mock
        when(authorizationService.getCurrentUserPrincipal(AUTH_HEADER)).thenReturn(principal);
        when(userService.editUser(any())).thenReturn(requestUser);

        //Action
        mockMvc.perform(post(pathPrefix + "/edit")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonWithExtra))
                //Assets
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@example.com"))
                .andExpect(jsonPath("$.username").value("user-01"));


        verify(authorizationService, times(1)).getCurrentUserPrincipal(AUTH_HEADER);
        verify(userService, times(1)).editUser(any());
    }

    @Test
    // 500 but 401 is expected
    void editUser_emptyRequestBody_shouldReturnUnauthorized() throws Exception {
        //Action
        mockMvc.perform(post(pathPrefix + "/edit")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                //Assets
                .andExpect(status().isUnauthorized());

        verify(authorizationService, never()).getCurrentUserPrincipal(any());
        verify(userService, never()).editUser(any());
    }

    @Test
        //Expected 401 but was 500 what okay is but not declared in Swagger
    void editUser_runTimeException_shouldReturnUnauthorized() throws Exception {

        // Arrange
        UsersEntity requestUser = new UsersEntity();
        requestUser.setId(1L);
        requestUser.setEmail("user@example.com");
        requestUser.setUsername("user-01");

        // Mock
        when(authorizationService.getCurrentUserPrincipal(AUTH_HEADER))
                .thenThrow(new RuntimeException());

        // Action
        mockMvc.perform(post(pathPrefix + "/edit")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser)))
                // Assert
                .andExpect(status().isUnauthorized());

        // Verify
        verify(authorizationService, times(1)).getCurrentUserPrincipal(AUTH_HEADER);
        verify(userService, never()).editUser(any());
    }

    @Test
    void login_validCredentials_shouldReturnUserAndJWTToken() throws Exception {
        //Arrange
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("valid_email@ninetails.com");
        request.setPassword("unsecure-password");

        UsersEntity loginUser = new UsersEntity();
        loginUser.setId(1L);
        loginUser.setEmail("valid_email@ninetails.com");
        loginUser.setUsername("user-01");

        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        when(userService.findUserByEmail(request.getEmail())).thenReturn(loginUser);

        when(jwtTokenUtil.createToken(any(UserPrincipal.class))).thenReturn("dummy.jwt.token");

        //Act
        mockMvc.perform(post(pathPrefix + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assets
                .andExpect(status().isOk())
                .andExpect(header().string("Jwt-Token", "dummy.jwt.token"))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("valid_email@ninetails.com"));

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(1))
                .findUserByEmail(request.getEmail());

    }

    @Test
        //NOTE: BadCredentialsException is one of the errors authenticationManager.authenticate(...) can throw
        //Any of them is not handled
    void login_invalidCredentials_shouldReturnUnauthorized() throws Exception {

        //Arrange
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("invalid_email@ninetails.com");
        request.setPassword("wrong-password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        //Act
        mockMvc.perform(post(pathPrefix + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assets
                .andExpect(status().isUnauthorized());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, never())
                .findUserByEmail(request.getEmail());
    }

    @Test
        //NOTE: userService.findUserByEmail returns null by default. This case is not handled.
        //Some methods in UserService throws UserNotFoundException, maybe it is a good idea to do the same for the findUserByEmail and not "the null"
    void login_userNotFound_shouldReturnUnauthorized() throws Exception {
        //Arrange
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("valid_email@ninetails.com");
        request.setPassword("unsecure-password");

        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        when(userService.findUserByEmail(request.getEmail())).thenReturn(null);


        //Act
        mockMvc.perform(post(pathPrefix + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                //Assets
                .andExpect(status().isUnauthorized());

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(1))
                .findUserByEmail(request.getEmail());

    }

    @Test
    //Expected 401 but was 500. Not declared in swagger nor handled
    //400 is better in this case
    void login_malformedJson_shouldReturnUnauthorized() throws Exception {
        //Arrange
        String malformedJson = "{ \"email\": \"user@example.com\", \"password\": }";

        mockMvc.perform(post(pathPrefix + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatusCode").value(400))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"));

        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, times(1))
                .findUserByEmail("user@example.com");
    }

    @Test
        //Expected 401 but was 500. Not declared in swagger nor handled
        //400 is better in this case
    void login_missingRequestBody_shouldUnauthorized() throws Exception {
        // Act & Assert
        mockMvc.perform(post(pathPrefix + "/login")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.httpStatusCode").value(400))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"));
    }

    @Test
        //Expected 401 but was 500. Not declared in swagger nor handled
        //400 is better in this case
    void login_missingEmail_shouldReturnUnauthorized() throws Exception {
        //Arrange
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail(null);
        request.setPassword("unsecure-password");

        //Act
        mockMvc.perform(post(pathPrefix + "/login")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request)))
                //Assets
                .andExpect(status().isUnauthorized());


        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, never())
                .findUserByEmail(request.getEmail());

    }

    @Test
        //Expected 401 but was 500. Not declared in swagger
    void login_wrongContentType_shouldReturnUnauthorized() throws Exception {
        // Arrange
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("valid_email@ninetails.com");
        request.setPassword("unsecure-password");

        // Act
        mockMvc.perform(post(pathPrefix + "/login")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(request)))
                //Assets
                .andExpect(status().isUnauthorized());
    }

    @Test
        //Expected 401 but was 500. Not declared in swagger
    void login_runtimeException_shouldReturnUnauthorized() throws Exception {
        // Arrange
        UserLoginRequest request = new UserLoginRequest();
        request.setEmail("valid_email@ninetails.com");
        request.setPassword("unsecure-password");

        // Mock userService to throw a runtime exception
        when(userService.findUserByEmail(any()))
                .thenThrow(new RuntimeException("DB down"));

        // Act & Assert
        mockMvc.perform(post(pathPrefix + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void register_validUser_shouldReturnUserAndJwt() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("newuser@ninetails.com");
        request.setPassword("StrongP@ssword1");
        request.setFirstName("Nine");
        request.setLastName("Tails");

        UsersEntity registeredUser = new UsersEntity();
        registeredUser.setId(1L);
        registeredUser.setEmail("newuser@ninetails.com");
        registeredUser.setFirstName("Nine");
        registeredUser.setLastName("Tails");

        Authentication auth = mock(Authentication.class);

        when(userService.register(any(UserRegisterRequest.class))).thenReturn(registeredUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);

        doNothing().when(emailService).sendWelcomeEmail(registeredUser);
        when(jwtTokenUtil.createToken(any(UserPrincipal.class))).thenReturn("dummy.jwt.token");

        mockMvc.perform(post(pathPrefix + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))

                .andExpect(status().isOk())
                .andExpect(header().string("Jwt-Token", "dummy.jwt.token"))
                .andExpect(jsonPath("$.email").value("newuser@ninetails.com"))
                .andExpect(jsonPath("$.firstName").value("Nine"))
                .andExpect(jsonPath("$.lastName").value("Tails"));

        verify(userService, times(1)).register(any(UserRegisterRequest.class));
        verify(emailService, times(1)).sendWelcomeEmail(registeredUser);
        verify(authenticationManager, times(1)).authenticate(any());
    }

    @Test
    void register_invalidPassword_shouldReturnBadRequest() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("newuser@ninetails.com");
        request.setPassword("weak");

        mockMvc.perform(post(pathPrefix + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).register(any());
        verify(emailService, never()).sendWelcomeEmail(any());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    // 400 expected but was 409 CONFLICT
    void register_emailAlreadyExists_shouldReturnBadRequest() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("existing@ninetails.com");
        request.setPassword("StrongP@ssword1");

        when(userService.register(any(UserRegisterRequest.class)))
                .thenThrow(new EmailExistException("Email already exists"));

        mockMvc.perform(post(pathPrefix + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).register(any());
        verify(emailService, never()).sendWelcomeEmail(any());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    // 400 expected but was 500
    void register_missingRequestBody_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post(pathPrefix + "/register")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
        // 400 expected but was 500
    void register_malformedJson_shouldReturnBadRequest() throws Exception {
        String malformedJson = "{ \"email\": \"user@ninetails.com\", \"password\": }";

        mockMvc.perform(post(pathPrefix + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
        // 400 expected but was 500
    void register_wrongContentType_shouldReturnBadRequest() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("user@ninetails.com");
        request.setPassword("StrongP@ssword1");

        mockMvc.perform(post(pathPrefix + "/register")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
        // 400 expected but was 404
    void register_userNotFound_shouldReturnBadRequest() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("user@ninetails.com");
        request.setPassword("StrongP@ssword1");

        when(userService.register(any(UserRegisterRequest.class)))
                .thenThrow(new UserNotFoundException("Not found"));

        mockMvc.perform(post(pathPrefix + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).register(any());
        verify(emailService, never()).sendWelcomeEmail(any());
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    // 500 was returned but 400
    void register_runtimeException_shouldReturnBadRequest() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("user@ninetails.com");
        request.setPassword("StrongP@ssword1");

        when(userService.register(any(UserRegisterRequest.class))).thenThrow(new RuntimeException("DB down"));

        mockMvc.perform(post(pathPrefix + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(userService, times(1)).register(any());
        verify(emailService, never()).sendWelcomeEmail(any());
        verify(authenticationManager, never()).authenticate(any());

    }

    @Test
    @WithMockUser(authorities = "user:super")
    void updateUserRole_validRequest_shouldReturnUpdatedUser() throws Exception {

        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setRoleName("ROLE_ADMIN");

        Long userId = 10L;
        UsersEntity targetUser = new UsersEntity();
        targetUser.setId(userId);
        targetUser.setEmail("user@ninetails.com");

        UsersEntity updatedUser = new UsersEntity();
        updatedUser.setId(userId);
        updatedUser.setEmail("user@ninetails.com");
        updatedUser.setRole("ROLE_ADMIN");

        UsersEntity superAdminUser = new UsersEntity();
        superAdminUser.setEmail("super_admin@ninetails.com");
        superAdminUser.setRole("SUPER_ADMIN_AUTHORITIES");

        UserPrincipal superAdminPrincipal = new UserPrincipal(superAdminUser);

        when(authorizationService.getCurrentUserPrincipal(AUTH_HEADER))
                .thenReturn(superAdminPrincipal);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(targetUser));
        when(authorizationService.isSuperAdmin(any(UserPrincipal.class)))
                .thenReturn(false);
        when(userService.updateUserRole(userId, "ROLE_ADMIN"))
                .thenReturn(updatedUser);

        mockMvc.perform(put(pathPrefix + "/" + userId + "/role")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("user@ninetails.com"))
                .andExpect(jsonPath("$.role").value("ROLE_ADMIN"));

        verify(authorizationService, times(1)).getCurrentUserPrincipal(AUTH_HEADER);
        verify(userRepository, times(1)).findById(userId);
        verify(authorizationService, times(1)).isSuperAdmin(any(UserPrincipal.class));
        verify(userService, times(1)).updateUserRole(userId, "ROLE_ADMIN");
    }

    @Test
    @WithMockUser(authorities = "user:super")
    void updateUserRole_targetUserNotFound_shouldReturn404() throws Exception {
        Long userId = 99L;

        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setRoleName("ROLE_ADMIN");

        UsersEntity superAdminUser = new UsersEntity();
        superAdminUser.setEmail("super_admin@ninetails.com");
        superAdminUser.setRole("SUPER_ADMIN_AUTHORITIES");

        UserPrincipal superAdminPrincipal = new UserPrincipal(superAdminUser);

        when(authorizationService.getCurrentUserPrincipal(AUTH_HEADER))
                .thenReturn(superAdminPrincipal);

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        mockMvc.perform(put(pathPrefix + "/" + userId + "/role")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
        verify(authorizationService, times(1)).getCurrentUserPrincipal(AUTH_HEADER);
        verify(userRepository, times(1)).findById(userId);
        verify(authorizationService, never()).isSuperAdmin(any(UserPrincipal.class));
        verify(userService, never()).updateUserRole(userId, "ROLE_ADMIN");
    }

    @Test
    // Swagger @ApiResponse(responseCode = "403", description = "Access denied")
    // But 403 is forbidden and not access denied 401
    @WithMockUser(authorities = "user:super")
    void updateUserRole_targetIsSuperAdmin_shouldReturn403() throws Exception {
        Long userId = 5L;

        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setRoleName("ROLE_ADMIN");

        UsersEntity superAdminUser = new UsersEntity();
        superAdminUser.setId(userId);

        when(authorizationService.getCurrentUserPrincipal(anyString()))
                .thenReturn(mock(UserPrincipal.class));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(superAdminUser));
        when(authorizationService.isSuperAdmin(any(UserPrincipal.class)))
                .thenReturn(true);

        mockMvc.perform(put(pathPrefix + "/" + userId + "/role")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(authorizationService, times(1)).getCurrentUserPrincipal(AUTH_HEADER);
        verify(userRepository, times(1)).findById(userId);
        verify(authorizationService, times(1)).isSuperAdmin(any(UserPrincipal.class));
        verify(userService, never()).updateUserRole(userId, "ROLE_ADMIN");
    }

    @Test
    // Swagger @ApiResponse(responseCode = "403", description = "Access denied")
    // But 403 is forbidden and not access denied 401
    @WithMockUser(authorities = "user:super")
    void updateUserRole_promoteToSuperAdmin_wrongOrganization_shouldReturn403() throws Exception {
        Long userId = 7L;

        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setRoleName("ROLE_SUPER_ADMIN");

        OrganizationEntity otherOrg = new OrganizationEntity();
        otherOrg.setName("other-org");

        UsersEntity user = new UsersEntity();
        user.setId(userId);
        user.setOrganizations(Set.of(otherOrg));

        UsersEntity superAdminUser = new UsersEntity();
        superAdminUser.setEmail("super_admin@ninetails.com");
        superAdminUser.setRole("SUPER_ADMIN_AUTHORITIES");

        UserPrincipal superAdminPrincipal = new UserPrincipal(superAdminUser);

        when(authorizationService.getCurrentUserPrincipal(AUTH_HEADER))
                .thenReturn(superAdminPrincipal);
        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(authorizationService.isSuperAdmin(any(UserPrincipal.class)))
                .thenReturn(false);

        mockMvc.perform(put(pathPrefix + "/" + userId + "/role")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(authorizationService, times(1)).getCurrentUserPrincipal(AUTH_HEADER);
        verify(userRepository, times(1)).findById(userId);
        verify(authorizationService, times(1)).isSuperAdmin(any(UserPrincipal.class));
        verify(userService, never()).updateUserRole(userId, "ROLE_ADMIN");
    }

    @Test
    @WithMockUser(authorities = "user:super")
    void updateUserRole_invalidRole_shouldReturn404() throws Exception {
        Long userId = 3L;

        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setRoleName("ROLE_UNKNOWN");

        UsersEntity user = new UsersEntity();
        user.setId(userId);

        UsersEntity superAdminUser = new UsersEntity();
        superAdminUser.setEmail("super_admin@ninetails.com");
        superAdminUser.setRole("SUPER_ADMIN_AUTHORITIES");

        UserPrincipal superAdminPrincipal = new UserPrincipal(superAdminUser);

        when(authorizationService.getCurrentUserPrincipal(AUTH_HEADER))
                .thenReturn(superAdminPrincipal);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));
        when(authorizationService.isSuperAdmin(any(UserPrincipal.class)))
                .thenReturn(false);
        when(userService.updateUserRole(userId, "ROLE_UNKNOWN"))
                .thenThrow(new InvalidRoleException("Invalid role"));

        mockMvc.perform(put(pathPrefix + "/" + userId + "/role")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(authorizationService, times(1)).getCurrentUserPrincipal(AUTH_HEADER);
        verify(userRepository, times(1)).findById(userId);
        verify(authorizationService, times(1)).isSuperAdmin(any(UserPrincipal.class));
        verify(userService, times(1)).updateUserRole(userId, "ROLE_UNKNOWN");
    }

    @Test
    // returns 500 what okay is but not described in Swagger
    void updateUserRole_runtimeException_shouldReturnInternalServerError() throws Exception {
        Long userId = 1L;

        UpdateUserRoleRequest request = new UpdateUserRoleRequest();
        request.setRoleName("ROLE_USER");

        UsersEntity adminUser = new UsersEntity();
        adminUser.setEmail("admin@ninetails.com");

        UsersEntity targetUser = new UsersEntity();
        targetUser.setId(userId);
        targetUser.setEmail("user@ninetails.com");
        targetUser.setRole("ROLE_USER");

        UserPrincipal adminPrincipal = new UserPrincipal(adminUser);

        when(authorizationService.getCurrentUserPrincipal(AUTH_HEADER))
                .thenReturn(adminPrincipal);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(targetUser));

        when(authorizationService.isSuperAdmin(any(UserPrincipal.class)))
                .thenReturn(false);

        when(userService.updateUserRole(userId, request.getRoleName()))
                .thenThrow(new RuntimeException("DB down"));

        mockMvc.perform(put(pathPrefix + "/" + userId + "/role")
                        .header(HttpHeaders.AUTHORIZATION, AUTH_HEADER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        verify(authorizationService, times(1)).getCurrentUserPrincipal(AUTH_HEADER);
        verify(userRepository, times(1)).findById(userId);
        verify(authorizationService, times(1)).isSuperAdmin(any(UserPrincipal.class));
        verify(userService, times(1)).updateUserRole(userId, "ROLE_USER");
    }


}
