package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.model.entity.UsersEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2Service {

//    UsersEntity loginGithub(OAuth2User oAuth2User);
    String loginGithub(OAuth2User oAuth2User, OAuth2AuthenticationToken authentication);
}
