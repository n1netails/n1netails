package com.n1netails.n1netails.api.controller;

import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.AuthorizationService;
import com.n1netails.n1netails.api.service.EmailService;
import com.n1netails.n1netails.api.service.UserService;
import com.n1netails.n1netails.api.util.JwtTokenUtil;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    private final String pathPrefix = "/ninetails/user";
    private static final String VALID_TOKEN = "valid.jwt.token";
    private static final String AUTH_HEADER = "Bearer " + VALID_TOKEN;

    @Autowired
    private MockMvc mockMvc;

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
    void getCurrentUser_missingAuthorizationHeader_shouldReturnUnauthorized() throws Exception {
        // NOTE: This test does NOT return 401 as one might expect.
        // Because the @RequestHeader in the controller is required by default,
        // Spring throws a MissingRequestHeaderException before the controller method is called.
        // This results in the def. error response (500 INTERNAL_SERVER_ERROR).

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
    }

    @Test
    void getCurrentUser_invalidJwt_shouldThrowAccessDenied() throws Exception {
        // Mock Data
        when(jwtDecoder.decode(VALID_TOKEN)).thenThrow(new JwtException("Invalid token"));

        // Action
        mockMvc.perform(get(pathPrefix + "/self").header(HttpHeaders.AUTHORIZATION, AUTH_HEADER))
                // Assert
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCurrentUser_userNotFound_shouldThrowAccessDenied() throws Exception {
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
    }

    @Test
    void getCurrentUser_disabledUser_shouldThrowAccessDenied() throws Exception {
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
    }

    @Test
    void getCurrentUser_lockedUser_shouldThrowAccessDenied() throws Exception {
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
    }


}
