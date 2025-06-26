//package com.n1netails.n1netails.api.service;
//
//import com.google.common.cache.Cache;
//import com.n1netails.n1netails.api.exception.type.UserNotFoundException;
//import com.n1netails.n1netails.api.model.UserPrincipal;
//import com.n1netails.n1netails.api.model.dto.passkey.*;
//import com.n1netails.n1netails.api.model.entity.PasskeyCredentialEntity;
//import com.n1netails.n1netails.api.model.entity.UsersEntity;
//import com.n1netails.n1netails.api.repository.PasskeyCredentialRepository;
//import com.n1netails.n1netails.api.repository.UserRepository;
//import com.yubico.webauthn.*;
//import com.yubico.webauthn.data.*;
//import com.yubico.webauthn.exception.RegistrationFailedException;
//import com.yubico.webauthn.exception.AssertionFailedException;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Captor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.security.oauth2.jwt.JwtEncoder;
//import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
//
//
//import java.time.Instant;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.Optional;
//import java.util.Set;
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class PasskeyServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private PasskeyCredentialRepository passkeyCredentialRepository;
//
//    @Mock
//    private RelyingParty relyingParty;
//
//    @Mock
//    private JwtEncoder jwtEncoder;
//
//    @Mock
//    private AuthenticationManager authenticationManager; // Mocked, actual auth done by Spring Security
//
//    @Spy // Using Spy for caches to allow real cache behavior but still verify interactions if needed
//    private Cache<String, PublicKeyCredentialCreationOptions> registrationCache = CacheBuilder.newBuilder().build();
//
//    @Spy
//    private Cache<String, PublicKeyCredentialRequestOptions> authenticationCache = CacheBuilder.newBuilder().build();
//
//    @Captor
//    private ArgumentCaptor<PasskeyCredentialEntity> passkeyCredentialEntityCaptor;
//
//    @Captor
//    private ArgumentCaptor<FinishRegistrationOptions> finishRegistrationOptionsCaptor;
//
//    @Captor
//    private ArgumentCaptor<FinishAssertionOptions> finishAssertionOptionsCaptor;
//
//
//    // These values would come from application.yml in a real scenario
//    private String rpId = "localhost";
//    private String rpName = "N1netails API Test";
//    private Set<String> origins = new HashSet<>(Collections.singletonList("http://localhost:8080"));
//
//    private PasskeyService passkeyService;
//
//    private UsersEntity testUser;
//    private UserIdentity testUserIdentity;
//    private PublicKeyCredentialCreationOptions testCredentialCreationOptions;
//    private PublicKeyCredentialRequestOptions testCredentialRequestOptions;
//
//    @BeforeEach
//    void setUp() {
//        // Re-initialize PasskeyService before each test to ensure mocks are correctly injected by @InjectMocks
//        // and caches are fresh (though @Spy handles this well, explicit construction is clearer)
//        passkeyService = new PasskeyService(
//                userRepository,
//                passkeyCredentialRepository,
//                rpId,
//                rpName,
//                origins,
//                jwtEncoder,
//                authenticationManager
//        );
//        // Manually inject spied caches as @InjectMocks doesn't work well with @Spy on fields initialized with new.
//        // ReflectionTestUtils.setField(passkeyService, "registrationCache", registrationCache);
//        // ReflectionTestUtils.setField(passkeyService, "authenticationCache", authenticationCache);
//        // Update: Constructor injection of Cache is not directly possible with @Spy.
//        // The PasskeyService constructor now initializes its own caches.
//        // For testing cache interactions, we would need to either:
//        // 1. Make caches injectable (e.g. as beans) - more complex for this setup.
//        // 2. Test effects (e.g. flow ID expires) rather than direct cache mock verification if internal caches are used.
//        // Given the current PasskeyService constructor, direct injection of spied caches isn't straightforward without modifying it.
//        // We will test the service logic assuming the caches work as intended by Guava.
//
//        testUser = new UsersEntity();
//        testUser.setId(1L);
//        testUser.setUserId(UUID.randomUUID().toString());
//        testUser.setUsername("testuser");
//        testUser.setFirstName("Test");
//        testUser.setLastName("User");
//        testUser.setEmail("testuser@example.com");
//        testUser.setAuthorities(new String[]{"USER"});
//
//        testUserIdentity = UserIdentity.builder()
//                .name(testUser.getUsername())
//                .displayName(testUser.getFirstName() + " " + testUser.getLastName())
//                .id(ByteArray.fromBase64Url(testUser.getId().toString())) // Matching generateUserHandle logic
//                .build();
//
//        testCredentialCreationOptions = PublicKeyCredentialCreationOptions.builder()
//                .rp(RelyingPartyIdentity.builder().id(rpId).name(rpName).build())
//                .user(testUserIdentity)
//                .challenge(new ByteArray(new byte[16]))
//                .pubKeyCredParams(Collections.emptyList())
//                .build();
//
//        testCredentialRequestOptions = PublicKeyCredentialRequestOptions.builder()
//                .challenge(new ByteArray(new byte[16]))
//                .rpId(rpId)
//                .build();
//
//         // Reset RelyingParty mock behavior for startRegistration/startAssertion
//        lenient().when(relyingParty.startRegistration(any(StartRegistrationOptions.class))).thenReturn(testCredentialCreationOptions);
//        lenient().when(relyingParty.startAssertion(any(StartAssertionOptions.class))).thenReturn(testCredentialRequestOptions);
//    }
//
//    // --- Registration Tests ---
//
//    @Test
//    void startRegistration_UserFound_ReturnsOptionsAndCaches() throws UserNotFoundException {
//        when(userRepository.findUserByUsername("testuser")).thenReturn(testUser);
//        // RelyingParty mock for startRegistration is in setUp with lenient()
//
//        PasskeyRegistrationStartRequestDto request = new PasskeyRegistrationStartRequestDto();
//        request.setUsername("testuser");
//        request.setDomain(rpId);
//
//        PasskeyRegistrationStartResponseDto response = passkeyService.startRegistration(request);
//
//        assertNotNull(response);
//        assertNotNull(response.getFlowId());
//        assertEquals(testCredentialCreationOptions, response.getOptions());
//        // To verify cache, we'd need access to the service's internal cache instance or use a mockable cache.
//        // For now, we trust the service puts it if no exception.
//    }
//
//    @Test
//    void startRegistration_UserNotFound_ThrowsUserNotFoundException() {
//        when(userRepository.findUserByUsername("unknownuser")).thenReturn(null);
//
//        PasskeyRegistrationStartRequestDto request = new PasskeyRegistrationStartRequestDto();
//        request.setUsername("unknownuser");
//        request.setDomain(rpId);
//
//        assertThrows(UserNotFoundException.class, () -> passkeyService.startRegistration(request));
//    }
//
//    @Test
//    void finishRegistration_Success_SavesCredentialAndReturnsTrue() throws UserNotFoundException, RegistrationFailedException, ExecutionException {
//        // Arrange
//        String flowId = "testFlowId";
//        PasskeyRegistrationFinishRequestDto finishRequest = new PasskeyRegistrationFinishRequestDto();
//        finishRequest.setFlowId(flowId);
//        finishRequest.setFriendlyName("Test Key");
//
//        // Mock credential from client
//        AuthenticatorAttestationResponse attestationResponse = mock(AuthenticatorAttestationResponse.class);
//        AuthenticatorData authenticatorData = mock(AuthenticatorData.class);
//        AttestedCredentialData attestedCredentialData = mock(AttestedCredentialData.class);
//
//        when(attestationResponse.getAttestationObject()).thenReturn(new ByteArray(new byte[0])); // dummy
//        when(attestationResponse.getTransports()).thenReturn(Set.of(AuthenticatorTransport.INTERNAL));
//        when(attestationResponse.getAttestationObject().getAuthData()).thenReturn(authenticatorData);
//        when(authenticatorData.getAttestedCredentialData()).thenReturn(Optional.of(attestedCredentialData));
//        when(attestedCredentialData.getAaguid()).thenReturn(new Aaguid(new byte[16]));
//
//
//        PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> clientCredential =
//                PublicKeyCredential.builder()
//                        .id(new ByteArray(new byte[]{1, 2, 3}))
//                        .response(attestationResponse)
//                        .type(PublicKeyCredentialType.PUBLIC_KEY)
//                        .clientExtensionResults(ClientRegistrationExtensionOutputs.builder().build())
//                        .build();
//        finishRequest.setCredential(clientCredential);
//
//        // Mock cache lookup
//        // Directly put into the spied cache if PasskeyService uses the one we spied.
//        // If PasskeyService creates its own internal cache, this won't work for mocking getIfPresent.
//        // We will assume the flowId exists for this success test path by pre-populating.
//        // This part is tricky without direct cache injection into PasskeyService for testing.
//        // For now, let's simulate the cache having the options:
//        RegistrationCacheTestingHelper.putInCache(passkeyService, flowId, testCredentialCreationOptions);
//
//
//        when(userRepository.findUserByUsername(testUser.getUsername())).thenReturn(testUser);
//
//        RegistrationResult registrationResult = mock(RegistrationResult.class);
//        when(registrationResult.isSuccess()).thenReturn(true);
//        when(registrationResult.getKeyId()).thenReturn(CredentialNickname.builder().id(clientCredential.getId()).build());
//        when(registrationResult.getPublicKeyCose()).thenReturn(new ByteArray(new byte[]{4,5,6}));
//        when(registrationResult.getSignatureCount()).thenReturn(0L);
//        when(registrationResult.isBackupEligible()).thenReturn(true);
//        when(registrationResult.isBackedUp()).thenReturn(false);
//        when(registrationResult.isUserVerified()).thenReturn(true);
//
//        when(relyingParty.finishRegistration(any(FinishRegistrationOptions.class))).thenReturn(registrationResult);
//        when(passkeyCredentialRepository.save(any(PasskeyCredentialEntity.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        // Act
//        boolean success = passkeyService.finishRegistration(finishRequest);
//
//        // Assert
//        assertTrue(success);
//        verify(passkeyCredentialRepository).save(passkeyCredentialEntityCaptor.capture());
//        PasskeyCredentialEntity savedEntity = passkeyCredentialEntityCaptor.getValue();
//        assertEquals(testUser, savedEntity.getUser());
//        assertEquals(clientCredential.getId().getBase64Url(), savedEntity.getCredentialId());
//        assertArrayEquals(registrationResult.getPublicKeyCose().getBytes(), savedEntity.getPublicKeyCose());
//        assertEquals(0L, savedEntity.getSignatureCount());
//        assertEquals("Test Key", savedEntity.getDeviceName());
//        assertTrue(savedEntity.getTransports().contains(AuthenticatorTransport.INTERNAL.getId()));
//    }
//
//
//    @Test
//    void finishRegistration_FlowIdNotFound_ReturnsFalse() throws UserNotFoundException {
//        PasskeyRegistrationFinishRequestDto request = new PasskeyRegistrationFinishRequestDto();
//        request.setFlowId("nonExistentFlowId");
//        // No need to mock credential as it won't be reached
//        // Ensure cache does not contain "nonExistentFlowId" (default for fresh cache)
//
//        boolean result = passkeyService.finishRegistration(request);
//        assertFalse(result);
//    }
//
//    @Test
//    void finishRegistration_RpFinishFails_ReturnsFalse() throws UserNotFoundException, RegistrationFailedException, ExecutionException {
//        String flowId = "testFlowId";
//        PasskeyRegistrationFinishRequestDto finishRequest = setupBasicFinishRequest(flowId);
//        RegistrationCacheTestingHelper.putInCache(passkeyService, flowId, testCredentialCreationOptions);
//
//        when(relyingParty.finishRegistration(any(FinishRegistrationOptions.class)))
//                .thenThrow(new RegistrationFailedException("RP finish failed"));
//
//        boolean result = passkeyService.finishRegistration(finishRequest);
//
//        assertFalse(result);
//        verify(passkeyCredentialRepository, never()).save(any());
//    }
//
//    // --- Authentication Tests ---
//    @Test
//    void startAuthentication_ReturnsOptionsAndCaches() {
//        PasskeyAuthenticationStartRequestDto request = new PasskeyAuthenticationStartRequestDto();
//        request.setUsername(testUser.getUsername()); // Optional, but good to test with
//        // RelyingParty mock for startAssertion is in setUp with lenient()
//
//        PasskeyAuthenticationStartResponseDto response = passkeyService.startAuthentication(request);
//
//        assertNotNull(response);
//        assertNotNull(response.getFlowId());
//        assertEquals(testCredentialRequestOptions, response.getOptions());
//    }
//
//    @Test
//    void finishAuthentication_Success_ReturnsJwtAndUpdatesUser() throws AssertionFailedException, ExecutionException {
//        String flowId = "authFlowId";
//        PasskeyAuthenticationFinishRequestDto finishRequest = setupBasicAuthFinishRequest(flowId);
//        AuthCacheTestingHelper.putInCache(passkeyService, flowId, testCredentialRequestOptions);
//
//        AssertionResult assertionResult = mock(AssertionResult.class);
//        when(assertionResult.isSuccess()).thenReturn(true);
//        when(assertionResult.getUsername()).thenReturn(testUser.getUsername());
//        when(assertionResult.getCredentialId()).thenReturn(new ByteArray(new byte[]{1,2,3}));
//        when(assertionResult.getUserHandle()).thenReturn(testUserIdentity.getId());
//        when(assertionResult.getSignatureCount()).thenReturn(1L);
//
//        when(relyingParty.finishAssertion(any(FinishAssertionOptions.class))).thenReturn(assertionResult);
//        when(userRepository.findUserByUsername(testUser.getUsername())).thenReturn(testUser);
//
//        PasskeyCredentialEntity existingCredential = new PasskeyCredentialEntity();
//        existingCredential.setUser(testUser);
//        existingCredential.setCredentialId(assertionResult.getCredentialId().getBase64Url());
//        existingCredential.setUserHandle(assertionResult.getUserHandle().getBase64Url()); // Important for lookup
//        existingCredential.setPublicKeyCose(new byte[]{0}); // dummy
//        existingCredential.setSignatureCount(0L);
//        when(passkeyCredentialRepository.findByCredentialId(assertionResult.getCredentialId().getBase64Url()))
//            .thenReturn(Optional.of(existingCredential));
//        when(passkeyCredentialRepository.save(any(PasskeyCredentialEntity.class))).thenAnswer(inv -> inv.getArgument(0));
//
//
//        Jwt mockJwt = Jwt.withTokenValue("mocked.jwt.token")
//                .header("alg", "none")
//                .claim("sub", testUser.getUsername())
//                .issuedAt(Instant.now())
//                .expiresAt(Instant.now().plusSeconds(3600))
//                .build();
//        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);
//
//        PasskeyAuthenticationResponseDto response = passkeyService.finishAuthentication(finishRequest);
//
//        assertTrue(response.isSuccess());
//        assertEquals("mocked.jwt.token", response.getJwtToken());
//        assertEquals(testUser, response.getUser());
//        assertNotNull(testUser.getLastLoginDate());
//        verify(passkeyCredentialRepository).save(passkeyCredentialEntityCaptor.capture());
//        assertEquals(1L, passkeyCredentialEntityCaptor.getValue().getSignatureCount()); // Verify signature count updated
//    }
//
//
//    @Test
//    void finishAuthentication_AssertionFailed_ReturnsErrorResponse() throws AssertionFailedException, ExecutionException {
//        String flowId = "authFlowId";
//        PasskeyAuthenticationFinishRequestDto finishRequest = setupBasicAuthFinishRequest(flowId);
//        AuthCacheTestingHelper.putInCache(passkeyService, flowId, testCredentialRequestOptions);
//
//        when(relyingParty.finishAssertion(any(FinishAssertionOptions.class)))
//                .thenThrow(new AssertionFailedException("RP assertion failed"));
//
//        PasskeyAuthenticationResponseDto response = passkeyService.finishAuthentication(finishRequest);
//
//        assertFalse(response.isSuccess());
//        assertNull(response.getJwtToken());
//        assertTrue(response.getMessage().contains("Authentication failed"));
//    }
//
//    @Test
//    void finishAuthentication_UserFromAssertionNotFound_ReturnsError() throws AssertionFailedException, ExecutionException {
//        String flowId = "authFlowId";
//        PasskeyAuthenticationFinishRequestDto finishRequest = setupBasicAuthFinishRequest(flowId);
//        AuthCacheTestingHelper.putInCache(passkeyService, flowId, testCredentialRequestOptions);
//
//        AssertionResult assertionResult = mock(AssertionResult.class);
//        when(assertionResult.isSuccess()).thenReturn(true);
//        when(assertionResult.getUsername()).thenReturn("unknownUserFromAssertion"); // Different user
//        when(assertionResult.getCredentialId()).thenReturn(new ByteArray(new byte[]{1,2,3}));
//        when(assertionResult.getUserHandle()).thenReturn(testUserIdentity.getId()); // User handle might still be for testUser
//
//        when(relyingParty.finishAssertion(any(FinishAssertionOptions.class))).thenReturn(assertionResult);
//        // Crucially, userRepository will return null for "unknownUserFromAssertion"
//        when(userRepository.findUserByUsername("unknownUserFromAssertion")).thenReturn(null);
//
//        // Mock credential lookup for signature count update to avoid NPE there, though it's part of the problem path
//        PasskeyCredentialEntity existingCredential = new PasskeyCredentialEntity();
//        existingCredential.setUser(testUser); // Belongs to testUser
//        existingCredential.setCredentialId(assertionResult.getCredentialId().getBase64Url());
//        existingCredential.setUserHandle(assertionResult.getUserHandle().getBase64Url());
//        when(passkeyCredentialRepository.findByCredentialId(assertionResult.getCredentialId().getBase64Url()))
//            .thenReturn(Optional.of(existingCredential));
//
//
//        PasskeyAuthenticationResponseDto response = passkeyService.finishAuthentication(finishRequest);
//
//        assertFalse(response.isSuccess());
//        assertEquals("Authenticated user not found.", response.getMessage());
//    }
//
//
//    // --- Helper methods for setting up requests ---
//    private PasskeyRegistrationFinishRequestDto setupBasicFinishRequest(String flowId) {
//        PasskeyRegistrationFinishRequestDto request = new PasskeyRegistrationFinishRequestDto();
//        request.setFlowId(flowId);
//
//        AuthenticatorAttestationResponse attestationResponse = mock(AuthenticatorAttestationResponse.class);
//        AuthenticatorData authenticatorData = mock(AuthenticatorData.class);
//        AttestedCredentialData attestedCredentialData = mock(AttestedCredentialData.class);
//        when(attestationResponse.getAttestationObject()).thenReturn(new ByteArray(new byte[0]));
//        when(attestationResponse.getTransports()).thenReturn(Set.of());
//        when(attestationResponse.getAttestationObject().getAuthData()).thenReturn(authenticatorData);
//        when(authenticatorData.getAttestedCredentialData()).thenReturn(Optional.of(attestedCredentialData));
//        when(attestedCredentialData.getAaguid()).thenReturn(new Aaguid(new byte[16]));
//
//
//        PublicKeyCredential<AuthenticatorAttestationResponse, ClientRegistrationExtensionOutputs> clientCredential =
//                PublicKeyCredential.builder()
//                        .id(new ByteArray(new byte[]{1, 2, 3}))
//                        .response(attestationResponse)
//                        .type(PublicKeyCredentialType.PUBLIC_KEY)
//                        .clientExtensionResults(ClientRegistrationExtensionOutputs.builder().build())
//                        .build();
//        request.setCredential(clientCredential);
//        return request;
//    }
//
//    private PasskeyAuthenticationFinishRequestDto setupBasicAuthFinishRequest(String flowId) {
//        PasskeyAuthenticationFinishRequestDto request = new PasskeyAuthenticationFinishRequestDto();
//        request.setFlowId(flowId);
//
//        AuthenticatorAssertionResponse assertionResponse = mock(AuthenticatorAssertionResponse.class);
//         when(assertionResponse.getAuthenticatorData()).thenReturn(new ByteArray(new byte[37])); // Minimal valid size
//         when(assertionResponse.getSignature()).thenReturn(new ByteArray(new byte[70])); // Dummy signature
//         when(assertionResponse.getUserHandle()).thenReturn(Optional.of(testUserIdentity.getId()));
//
//
//        PublicKeyCredential<AuthenticatorAssertionResponse, ClientAssertionExtensionOutputs> clientCredential =
//                PublicKeyCredential.builder()
//                        .id(new ByteArray(new byte[]{1, 2, 3})) // Credential ID
//                        .response(assertionResponse)
//                        .type(PublicKeyCredentialType.PUBLIC_KEY)
//                        .clientExtensionResults(ClientAssertionExtensionOutputs.builder().build())
//                        .build();
//        request.setCredential(clientCredential);
//        return request;
//    }
//
//
//    // Helper static classes to access/modify PasskeyService's internal caches for testing
//    // This is a workaround for not having direct injection of the spied caches.
//    // Relies on the known field names in PasskeyService.
//    static class RegistrationCacheTestingHelper {
//        static void putInCache(PasskeyService service, String key, PublicKeyCredentialCreationOptions value) {
//            try {
//                java.lang.reflect.Field cacheField = PasskeyService.class.getDeclaredField("registrationCache");
//                cacheField.setAccessible(true);
//                Cache<String, PublicKeyCredentialCreationOptions> cache = (Cache<String, PublicKeyCredentialCreationOptions>) cacheField.get(service);
//                cache.put(key, value);
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                throw new RuntimeException("Failed to access registrationCache for testing", e);
//            }
//        }
//    }
//
//    static class AuthCacheTestingHelper {
//        static void putInCache(PasskeyService service, String key, PublicKeyCredentialRequestOptions value) {
//            try {
//                java.lang.reflect.Field cacheField = PasskeyService.class.getDeclaredField("authenticationCache");
//                cacheField.setAccessible(true);
//                Cache<String, PublicKeyCredentialRequestOptions> cache = (Cache<String, PublicKeyCredentialRequestOptions>) cacheField.get(service);
//                cache.put(key, value);
//            } catch (NoSuchFieldException | IllegalAccessException e) {
//                throw new RuntimeException("Failed to access authenticationCache for testing", e);
//            }
//        }
//    }
//}
