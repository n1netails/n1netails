package com.n1netails.n1netails.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.repository.PasskeyCredentialRepository;
import com.n1netails.n1netails.api.service.AppCredentialRepository; // This will be an adapter class
import com.yubico.webauthn.CredentialRepository;
import com.yubico.webauthn.RelyingParty;
import com.yubico.webauthn.data.RelyingPartyIdentity;
import com.yubico.webauthn.extension.largeblob.LargeBlobServerExtension; // Corrected import
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays; // Added import
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Configuration
public class WebAuthnConfig {

    @Value("${webauthn.relying-party-id}")
    private String relyingPartyId;

    @Value("${webauthn.relying-party-name}")
    private String relyingPartyName;

    @Value("${webauthn.relying-party-origins}")
    private Set<String> relyingPartyOrigins;

    // This is an adapter between Yubico's CredentialRepository and our PasskeyCredentialRepository
    @Bean
    public CredentialRepository yubicoCredentialRepository(PasskeyCredentialRepository passkeyCredentialRepository, ObjectMapper objectMapper) {
        return new AppCredentialRepository(passkeyCredentialRepository, objectMapper);
    }

    @Bean
    public RelyingParty relyingParty(CredentialRepository credentialRepository,
                                     @Value("${server.ssl.enabled:false}") boolean sslEnabled,
                                     @Value("${server.port}") int port,
                                     @Value("${server.address:localhost}") String address) {
        RelyingPartyIdentity rpIdentity = RelyingPartyIdentity.builder()
                .id(relyingPartyId)
                .name(relyingPartyName)
                .build();

        // If origins are not explicitly set, try to derive from server config
        Set<String> effectiveOrigins = new HashSet<>(this.relyingPartyOrigins);
        if (effectiveOrigins.isEmpty()) {
            String protocol = sslEnabled ? "https://" : "http://";
            String origin = protocol + address + ":" + port;
            effectiveOrigins.add(origin);
            // Add common localhost variations for development if RP ID is localhost
            if ("localhost".equalsIgnoreCase(relyingPartyId) && "localhost".equalsIgnoreCase(address)) {
                 effectiveOrigins.add("http://localhost:" + port); // Already added
                 effectiveOrigins.add("http://127.0.0.1:" + port);
            }
        }

        // Configure allowable AAGUIDs (optional, for restricting to specific authenticators)
        // Set<ByteArray> allowedAaguids = new HashSet<>();
        // allowedAaguids.add(AAGUID.fromHex("c5defc69-2058-4390-9b97-0abb90800a0d")); // Example YubiKey 5 AAGUID

        return RelyingParty.builder()
                .identity(rpIdentity)
                .credentialRepository(credentialRepository)
                .origins(effectiveOrigins)
                // .allowUntrustedAttestation(true) // Useful for development, but be cautious in production
                // .attestationTrustSource(new TrustStoreAttestationTrustSource(trustStore)) // For production with MDS
                // .allowedAaguids(allowedAaguids) // Optional: restrict allowed authenticators by AAGUID
                .validateSignatureCounter(true)
                .allowOriginPort(true) // Allow origins with port numbers
                .allowOriginSubdomain(false) // Set to true if you want to allow subdomains
                .extensions(Arrays.asList( // Use Arrays.asList
                    new LargeBlobServerExtension() // Corrected extension
                ))
                .build();
    }

    // We need an ObjectMapper for serializing/deserializing Yubico objects if we store them as JSON
    // Spring Boot provides one by default, but we can customize it if needed.
    // For now, relying on the default Spring Boot ObjectMapper.
}
