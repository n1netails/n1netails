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
    @Value("${n1netails.doc.url}")
    private String docUrl;
    @Value("${openai.enabled}")
    private boolean openaiEnabled;
    @Value("${gemini.enabled}")
    private boolean geminiEnabled;
    @Value("${auth.github.enabled}")
    private boolean githubAuthEnabled;
    @Value("${auth.google.enabled}")
    private boolean googleAuthEnabled;

    @Value("${n1netails.notifications.enabled}")
    private boolean notificationsEnabled;
    @Value("${n1netails.notifications.email.enabled}")
    private boolean emailEnabled;
    @Value("${n1netails.notifications.msteams.enabled}")
    private boolean microsoftTeamsEnabled;
    @Value("${n1netails.notifications.slack.enabled}")
    private boolean slackEnabled;
    @Value("${n1netails.notifications.discord.enabled}")
    private boolean discordEnabled;
    @Value("${n1netails.notifications.telegram.enabled}")
    private boolean telegramEnabled;

    @GetMapping("/api-url")
    public Map<String, String> getApiUrl() {
        return Collections.singletonMap("n1netailsApiUrl", apiBaseUrl);
    }

    @GetMapping("/doc-url")
    public Map<String, String> getDocUrl() { return Collections.singletonMap("n1netailsDocUrl", docUrl); }

    @GetMapping("/openai-enabled")
    public boolean isOpenaiEnabled() { return openaiEnabled; }

    @GetMapping("/gemini-enabled")
    public boolean isGeminiEnabled() { return geminiEnabled; }

    @GetMapping("/github-auth-enabled")
    public boolean isGithubAuthEnabled() { return githubAuthEnabled; }

    @GetMapping("/google-auth-enabled")
    public boolean isGoogleAuthEnabled() { return googleAuthEnabled; }

    @GetMapping("/notifications-enabled")
    public boolean isNotificationsEnabled() { return notificationsEnabled; }

    @GetMapping("/notifications-email-enabled")
    public boolean isNotificationsEmailEnabled() { return emailEnabled; }

    @GetMapping("/notifications-msteams-enabled")
    public boolean isNotificationsMsTeamsEnabled() { return microsoftTeamsEnabled; }

    @GetMapping("/notifications-slack-enabled")
    public boolean isNotificationsSlackEnabled() { return slackEnabled; }

    @GetMapping("/notifications-discord-enabled")
    public boolean isNotificationsDiscordEnabled() { return discordEnabled; }

    @GetMapping("/notifications-telegram-enabled")
    public boolean isNotificationsTelegramEnabled() { return telegramEnabled; }

}
