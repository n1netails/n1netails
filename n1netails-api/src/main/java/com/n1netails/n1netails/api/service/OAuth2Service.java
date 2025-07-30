package com.n1netails.n1netails.api.service;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2Service {

    String loginGithub(OAuth2User oAuth2User, OAuth2AuthenticationToken authentication);
}
