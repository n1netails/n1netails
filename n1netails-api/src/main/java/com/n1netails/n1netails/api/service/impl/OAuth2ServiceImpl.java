package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.constant.Authority;
import com.n1netails.n1netails.api.model.UserPrincipal;
import com.n1netails.n1netails.api.model.entity.OrganizationEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.repository.OrganizationRepository;
import com.n1netails.n1netails.api.repository.UserRepository;
import com.n1netails.n1netails.api.service.OAuth2Service;
import com.n1netails.n1netails.api.util.JwtTokenUtil;
import com.n1netails.n1netails.api.util.UserUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
@Qualifier("oAuth2Service")
public class OAuth2ServiceImpl implements OAuth2Service {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final JwtTokenUtil jwtTokenUtil;

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String loginGithub(OAuth2User oAuth2User, OAuth2AuthenticationToken authentication) {

        log.info("loginGithub");
        log.info("getting user info from OAuth2User");
        String provider = "GITHUB";
        log.info("providerId");
        String providerId = Optional.ofNullable(oAuth2User.getAttribute("id")).map(Object::toString).orElse(null);
        log.info("username");
        String username = oAuth2User.getAttribute("login");
        log.info("email");
        String email = oAuth2User.getAttribute("email"); // may be null
        log.info("avatarUrl");
        String avatarUrl = oAuth2User.getAttribute("avatar_url");
        log.info("name");
        String name = oAuth2User.getAttribute("name");
        log.info("getting user info from OAuth2User COMPLETED");

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );
        String accessToken = client.getAccessToken().getTokenValue();

        if (email == null) email = fetchPrimaryEmailFromGithub(accessToken);
        if (email == null) email = username + "@users.noreply.github.com";

        // Step 1: Try to find existing user by provider+providerId
        UsersEntity user = userRepository.findByProviderAndProviderId(provider, providerId).orElse(null);

        if (user == null) {
            // Step 2: Try to find by email
            user = userRepository.findUserByEmail(email).orElse(null);

            if (user != null) {
                // Step 3: Link GitHub to existing user
                log.info("Linking GitHub to existing user: {}", email);
                user.setProvider(provider);
                user.setProviderId(providerId);
            } else {
                // Step 4: New user registration
                log.info("Creating new user from GitHub OAuth2");
                user = new UsersEntity();
                user.setProvider(provider);
                user.setProviderId(providerId);
                user.setUserId(UserUtil.generateUserId());
                user.setJoinDate(new Date());
                user.setActive(true);
                user.setEnabled(true);
                user.setNotLocked(true);
                user.setRole(com.n1netails.n1netails.api.model.enumeration.Role.ROLE_USER.name());
                user.setAuthorities(Authority.USER_AUTHORITIES);

                OrganizationEntity n1netailsOrg = organizationRepository.findByName("n1netails")
                        .orElseThrow(() -> new RuntimeException("Default 'n1netails' organization not found."));

                user.setOrganizations(new HashSet<>(Set.of(n1netailsOrg)));
            }
        }

        // Common fields for all paths
        user.setEmail(email);
        user.setUsername(username);
        user.setProfileImageUrl(avatarUrl);

        // Set names
        if (name != null && !name.isBlank()) {
            int lastSpace = name.lastIndexOf(" ");
            if (lastSpace != -1) {
                user.setFirstName(name.substring(0, lastSpace));
                user.setLastName(name.substring(lastSpace + 1));
            } else {
                user.setFirstName(name);
            }
        }

        user.setLastLoginDateDisplay(user.getLastLoginDate());
        user.setLastLoginDate(new Date());

        log.info("Saving user from GitHub OAuth2");
        userRepository.save(user);

        return jwtTokenUtil.createToken(new UserPrincipal(user));
    }


    private String fetchPrimaryEmailFromGithub(String accessToken) {
        String url = "https://api.github.com/user/emails";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + accessToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<>() {}
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            for (Map<String, Object> emailObj : response.getBody()) {
                Boolean primary = (Boolean) emailObj.get("primary");
                Boolean verified = (Boolean) emailObj.get("verified");
                String email = (String) emailObj.get("email");
                if (primary != null && primary && verified != null && verified) {
                    return email;
                }
            }
        }
        return null; // fallback if no primary email found
    }
}
