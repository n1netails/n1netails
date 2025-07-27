//package com.n1netails.n1netails.api.controller;
//
//import com.n1netails.n1netails.api.model.entity.UsersEntity;
//import com.n1netails.n1netails.api.service.OAuth2Service;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Map;
//import java.util.Objects;
//
//@Slf4j
//@Tag(name = "OAuth Controller", description = "Operations related to OAuth2")
//@RequiredArgsConstructor
//@RestController
//public class OAuthController {
//
//    private final OAuth2Service oAuth2Service;
//
//    @GetMapping("/login-success")
//    public ResponseEntity<Map<String, String>> user(@AuthenticationPrincipal OAuth2User oAuth2User) {
//        log.info("Attempting Github Oauth2 login");
//        if (oAuth2User == null) {
//            // todo throw custom exception
//            throw new RuntimeException("OAuth2User is null!");
//        }
//        String token = this.oAuth2Service.loginGithub(oAuth2User);
//        return ResponseEntity.ok(Map.of("token", token));
//    }
//}
