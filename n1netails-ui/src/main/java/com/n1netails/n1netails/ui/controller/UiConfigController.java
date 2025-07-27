package com.n1netails.n1netails.ui.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/ui/n1netails-config")
public class UiConfigController {

    @Value("${n1netails.api.url}")
    private String apiBaseUrl;

    @Value("${openai.enabled}")
    private boolean openaiEnabled;

    @Value("${gemini.enabled}")
    private boolean geminiEnabled;

    @Value("${auth.github.enabled}")
    private boolean githubAuthEnabled;

    @GetMapping("/api-url")
    public Map<String, String> getApiUrl() {
        return Collections.singletonMap("n1netailsApiUrl", apiBaseUrl);
    }

    @GetMapping("/openai-enabled")
    public boolean isOpenaiEnabled() { return openaiEnabled; }

    @GetMapping("/gemini-enabled")
    public boolean isGeminiEnabled() { return geminiEnabled; }

    @GetMapping("/github-auth-enabled")
    public boolean isGithubAuthEnabled() { return githubAuthEnabled; }
}
