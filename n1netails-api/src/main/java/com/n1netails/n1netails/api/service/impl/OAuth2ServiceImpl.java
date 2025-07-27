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

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
        Object idAttr = oAuth2User.getAttribute("id");
        String providerId = (idAttr != null) ? idAttr.toString() : null;
        log.info("username");
        String username = oAuth2User.getAttribute("login");
        log.info("email");
        String email = oAuth2User.getAttribute("email"); // may be null
        log.info("avatarUrl");
        String avatarUrl = oAuth2User.getAttribute("avatar_url");
        log.info("name");
        String name = oAuth2User.getAttribute("name");
        log.info("getting user info from OAuth2User COMPLETED");

        UsersEntity user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    log.info("creating new user from github oauth2");
                    UsersEntity newUser = new UsersEntity();
                    newUser.setProvider(provider);
                    newUser.setProviderId(providerId);
                    newUser.setUserId(UserUtil.generateUserId());
                    newUser.setJoinDate(new Date());
                    newUser.setActive(true);
                    newUser.setEnabled(true);
                    newUser.setNotLocked(true);
                    newUser.setRole(com.n1netails.n1netails.api.model.enumeration.Role.ROLE_USER.name());
                    newUser.setAuthorities(Authority.USER_AUTHORITIES);

                    // Associate with "n1netails" organization
                    OrganizationEntity n1netailsOrg = organizationRepository.findByName("n1netails")
                            .orElseThrow(() -> new RuntimeException("Default 'n1netails' organization not found. Liquibase script might have failed."));

                    if (newUser.getOrganizations() == null) {
                        newUser.setOrganizations(new HashSet<>());
                    }
                    newUser.getOrganizations().add(n1netailsOrg);
                    return newUser;
                });


        // --- Extract GitHub Access Token ---
        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );
        String accessToken = client.getAccessToken().getTokenValue();

        // --- Get Primary Email (if available) ---
//        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            email = fetchPrimaryEmailFromGithub(accessToken);
        }

        // set no reply email if none available
//        if (email == null) email = username + "@users.noreply.github.com";
        user.setEmail(email);

        user.setUsername(username);
        user.setProfileImageUrl(avatarUrl);

        // split name into first and last name
        String firstName = "";
        String lastName = "";
        if (name != null && !name.isBlank()) {
            if (name.contains(" ")) {
                int lastSpace = name.lastIndexOf(" ");
                firstName = name.substring(0, lastSpace);
                lastName = name.substring(lastSpace + 1); // skip the space
            } else {
                firstName = name; // if only one name is provided
            }
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);

        user.setLastLoginDateDisplay(user.getLastLoginDate());
        user.setLastLoginDate(new Date());

        log.info("saving oauth2 user");
        userRepository.save(user);

        UserPrincipal principal = new UserPrincipal(user);
        return jwtTokenUtil.createToken(principal);
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
