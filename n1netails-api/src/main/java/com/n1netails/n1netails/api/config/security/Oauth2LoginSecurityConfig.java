package com.n1netails.n1netails.api.config.security;

import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.service.OAuth2Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Configuration
@Order(1)
public class Oauth2LoginSecurityConfig {

    private final OAuth2Service oAuth2Service;

    @Bean
    SecurityFilterChain oauth2SecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/oauth2/**", "/login/**", "/login-success")
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/oauth2/**", "/login/**", "/login-success").authenticated()
                    .anyRequest().permitAll()
            )
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
//            .oauth2Login(oauth2 -> oauth2
//                    .defaultSuccessUrl("/login-success", true)
//            );

//            .oauth2Login(oauth2 -> oauth2
//                    .successHandler((request, response, authentication) -> {
//                        UsersEntity user = oAuth2Service.loginGithub((OAuth2User) authentication.getPrincipal());
//                        String jwt = jwtTokenProvider.generateToken(user); // custom method
//
//                        response.sendRedirect("http://localhost:4200/oauth2/success?token=" + jwt);
//                    })
//            );

            .oauth2Login(oauth2 -> oauth2
                    .successHandler((request, response, authentication) -> {
                        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

                        // Save or update the user in DB
                        String jwtToken = oAuth2Service.loginGithub(oAuth2User, oauthToken);

//                        // Wrap into UserPrincipal
//                        UserPrincipal principal = new UserPrincipal(user);

//                        // Generate JWT token
//                        String jwt = jwtTokenUtil.createToken(principal);

                        log.info("jwt token: {}", jwtToken);
                        // Redirect to Angular app with the token as query param
                        response.sendRedirect("http://localhost:4200/oauth2/success?token=" + jwtToken);
                    })
            );

        return http.build();
    }
}
