//package com.n1netails.n1netails.api.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
//import com.n1netails.n1netails.api.model.dto.passkey.*;
//import com.n1netails.n1netails.api.model.entity.UsersEntity;
//import com.n1netails.n1netails.api.service.PasskeyService;
//import com.yubico.webauthn.data.PublicKeyCredentialCreationOptions;
//import com.yubico.webauthn.data.PublicKeyCredentialRequestOptions;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.hamcrest.Matchers.is;
//
//@ExtendWith(SpringExtension.class)
//@WebMvcTest(PasskeyController.class)
//class PasskeyControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private PasskeyService passkeyService;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    private PasskeyRegistrationStartRequestDto startRegistrationRequest;
//    private PasskeyRegistrationStartResponseDto startRegistrationResponse;
//    private PasskeyRegistrationFinishRequestDto finishRegistrationRequest;
//    private PasskeyAuthenticationStartRequestDto startAuthRequest;
//    private PasskeyAuthenticationStartResponseDto startAuthResponse;
//    private PasskeyAuthenticationFinishRequestDto finishAuthRequest;
//
//    @BeforeEach
//    void setUp() {
//        startRegistrationRequest = new PasskeyRegistrationStartRequestDto();
//        startRegistrationRequest.setUsername("testuser");
//        startRegistrationRequest.setDomain("localhost");
//
//        // Dummy options, service layer tests cover content validation
//        PublicKeyCredentialCreationOptions creationOptions = PublicKeyCredentialCreationOptions.builder().challenge(new com.yubico.webauthn.data.ByteArray(new byte[0])).rp(com.yubico.webauthn.data.RelyingPartyIdentity.builder().id("id").name("name").build()).user(com.yubico.webauthn.data.UserIdentity.builder().name("n").displayName("d").id(new com.yubico.webauthn.data.ByteArray(new byte[0])).build()).pubKeyCredParams(java.util.Collections.emptyList()).build();
//        startRegistrationResponse = new PasskeyRegistrationStartResponseDto("flow1", creationOptions);
//
//        finishRegistrationRequest = new PasskeyRegistrationFinishRequestDto();
//        finishRegistrationRequest.setFlowId("flow1");
//        // Mocking PublicKeyCredential is complex, service layer handles this
//        finishRegistrationRequest.setCredential(null);
//
//        startAuthRequest = new PasskeyAuthenticationStartRequestDto();
//        startAuthRequest.setUsername("testuser");
//
//        PublicKeyCredentialRequestOptions requestOptions = PublicKeyCredentialRequestOptions.builder().challenge(new com.yubico.webauthn.data.ByteArray(new byte[0])).build();
//        startAuthResponse = new PasskeyAuthenticationStartResponseDto("flow2", requestOptions);
//
//        finishAuthRequest = new PasskeyAuthenticationFinishRequestDto();
//        finishAuthRequest.setFlowId("flow2");
//        finishAuthRequest.setCredential(null); // Mocking this is complex
//    }
//
//    @Test
//    void startRegistration_Success() throws Exception {
//        when(passkeyService.startRegistration(any(PasskeyRegistrationStartRequestDto.class)))
//                .thenReturn(startRegistrationResponse);
//
//        mockMvc.perform(post("/ninetails/auth/passkey/register/start")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(startRegistrationRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.flowId", is("flow1")));
//    }
//
//    @Test
//    void startRegistration_UserNotFound_ReturnsNotFound() throws Exception {
//        when(passkeyService.startRegistration(any(PasskeyRegistrationStartRequestDto.class)))
//                .thenThrow(new UserNotFoundException("User not found"));
//
//        mockMvc.perform(post("/ninetails/auth/passkey/register/start")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(startRegistrationRequest)))
//                .andExpect(status().isNotFound());
//    }
//
//    @Test
//    void startRegistration_ServiceThrowsGenericException_ReturnsInternalServerError() throws Exception {
//        when(passkeyService.startRegistration(any(PasskeyRegistrationStartRequestDto.class)))
//                .thenThrow(new RuntimeException("Unexpected error"));
//
//        mockMvc.perform(post("/ninetails/auth/passkey/register/start")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(startRegistrationRequest)))
//                .andExpect(status().isInternalServerError());
//    }
//
//    @Test
//    void finishRegistration_Success() throws Exception {
//        when(passkeyService.finishRegistration(any(PasskeyRegistrationFinishRequestDto.class)))
//                .thenReturn(true);
//
//        mockMvc.perform(post("/ninetails/auth/passkey/register/finish")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(finishRegistrationRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success", is(true)))
//                .andExpect(jsonPath("$.message", is("Passkey registration successful.")));
//    }
//
//    @Test
//    void finishRegistration_Failed() throws Exception {
//        when(passkeyService.finishRegistration(any(PasskeyRegistrationFinishRequestDto.class)))
//                .thenReturn(false);
//
//        mockMvc.perform(post("/ninetails/auth/passkey/register/finish")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(finishRegistrationRequest)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.success", is(false)))
//                .andExpect(jsonPath("$.message", is("Passkey registration failed.")));
//    }
//
//    @Test
//    void finishRegistration_UserNotFound_ReturnsNotFound() throws Exception {
//        when(passkeyService.finishRegistration(any(PasskeyRegistrationFinishRequestDto.class)))
//                .thenThrow(new UserNotFoundException("User not found"));
//
//        mockMvc.perform(post("/ninetails/auth/passkey/register/finish")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(finishRegistrationRequest)))
//                .andExpect(status().isNotFound());
//    }
//
//
//    @Test
//    void startAuthentication_Success() throws Exception {
//        when(passkeyService.startAuthentication(any(PasskeyAuthenticationStartRequestDto.class)))
//                .thenReturn(startAuthResponse);
//
//        mockMvc.perform(post("/ninetails/auth/passkey/login/start")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(startAuthRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.flowId", is("flow2")));
//    }
//
//    @Test
//    void startAuthentication_NoBody_SuccessForDiscoverable() throws Exception {
//        when(passkeyService.startAuthentication(any(PasskeyAuthenticationStartRequestDto.class))) // Service gets an empty DTO
//                .thenReturn(startAuthResponse); // Response is still the same structure
//
//        mockMvc.perform(post("/ninetails/auth/passkey/login/start")
//                        .contentType(MediaType.APPLICATION_JSON)) // No content body
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.flowId", is("flow2")));
//    }
//
//
//    @Test
//    void finishAuthentication_Success() throws Exception {
//        UsersEntity user = new UsersEntity(); user.setUsername("testuser");
//        PasskeyAuthenticationResponseDto authResponse = new PasskeyAuthenticationResponseDto(true, "Auth success", "jwt.token", user);
//        when(passkeyService.finishAuthentication(any(PasskeyAuthenticationFinishRequestDto.class)))
//                .thenReturn(authResponse);
//
//        mockMvc.perform(post("/ninetails/auth/passkey/login/finish")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(finishAuthRequest)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success", is(true)))
//                .andExpect(jsonPath("$.jwtToken", is("jwt.token")));
//    }
//
//    @Test
//    void finishAuthentication_Failed() throws Exception {
//        PasskeyAuthenticationResponseDto authResponse = new PasskeyAuthenticationResponseDto(false, "Auth failed");
//        when(passkeyService.finishAuthentication(any(PasskeyAuthenticationFinishRequestDto.class)))
//                .thenReturn(authResponse);
//
//        mockMvc.perform(post("/ninetails/auth/passkey/login/finish")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(finishAuthRequest)))
//                .andExpect(status().isUnauthorized()) // As per controller logic for !response.isSuccess()
//                .andExpect(jsonPath("$.success", is(false)));
//    }
//}
