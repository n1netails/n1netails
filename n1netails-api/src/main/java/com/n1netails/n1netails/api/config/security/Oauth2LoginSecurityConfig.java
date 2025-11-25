package com.n1netails.n1netails.api.config.security;

import com.n1netails.n1netails.api.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@RequiredArgsConstructor
@ConditionalOnExpression("'${auth.github.enabled}' == 'true' or '${auth.google.enabled}' == 'true'")
@Configuration
@Order(1)
public class Oauth2LoginSecurityConfig {

    @Value("${auth.oauth2.redirects.success}")
    private String oAuth2RedirectsSuccess;

    private final OAuth2Service oAuth2Service;

    @Bean
    SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/oauth2/**", "/login/**")
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/oauth2/**", "/login/**").authenticated()
                    .anyRequest().permitAll()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .oauth2Login(oauth2 -> oauth2
                    .successHandler((request, response, authentication) -> {
                        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
                        String provider = oauthToken.getAuthorizedClientRegistrationId();
                        String jwtToken;

                        if ("github".equalsIgnoreCase(provider)) {
                            jwtToken = oAuth2Service.loginGithub(oAuth2User, oauthToken);
                        } else if ("google".equalsIgnoreCase(provider)) {
                            jwtToken = oAuth2Service.loginGoogle(oAuth2User, oauthToken);
                        } else {
                            // Handle other providers or throw an error
                            throw new IllegalStateException("Unexpected provider: " + provider);
                        }

                        // Redirect to Angular app with the token as query param
                        response.sendRedirect(oAuth2RedirectsSuccess + jwtToken);
                    })
            );

        return http.build();
    }
}
