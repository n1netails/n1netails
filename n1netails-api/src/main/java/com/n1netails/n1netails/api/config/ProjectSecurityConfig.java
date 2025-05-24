package com.n1netails.n1netails.api.config;

import com.n1netails.n1netails.api.filter.JwtAuthorizationFilter;
import com.n1netails.n1netails.api.handler.JwtAccessDeniedHandler;
import com.n1netails.n1netails.api.handler.JwtAuthenticationEntryHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Added
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;
import java.util.Collections;

import static com.n1netails.n1netails.api.constant.ProjectSecurityConstant.PUBLIC_URLS;

@Slf4j
@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(prePostEnabled = true) // Added
public class ProjectSecurityConfig {

    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final JwtAuthenticationEntryHandler jwtAuthenticationEntryHandler;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(AbstractHttpConfigurer::disable);

        http.cors(cors -> cors.configurationSource(request -> {
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowedOrigins(Collections.singletonList("*"));
            config.setAllowedMethods(Collections.singletonList("*"));
//            config.setAllowCredentials(true);
            config.setAllowCredentials(false);
            config.setAllowedHeaders(Collections.singletonList("*"));
            config.setExposedHeaders(Arrays.asList("Authorization", "Jwt-Token"));
            config.setMaxAge(3600L);
            return config;
        }));

        http.oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer ->
                httpSecurityOAuth2ResourceServerConfigurer.jwt(jwtConfigurer ->
                        jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        http
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer.accessDeniedHandler(jwtAccessDeniedHandler))
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(jwtAuthenticationEntryHandler))
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeHttpRequests(
                auth -> auth
                        .requestMatchers(PUBLIC_URLS).permitAll()
                        .anyRequest().authenticated()
        );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return authenticationManagerBuilder.build();
    }

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        return new JwtAuthenticationConverter();
    }
}
