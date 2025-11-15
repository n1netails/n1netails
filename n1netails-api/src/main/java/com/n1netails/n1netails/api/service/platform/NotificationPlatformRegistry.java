package com.n1netails.n1netails.api.service.platform;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.n1netails.n1netails.api.constant.PlatformConstant.*;
import static com.n1netails.n1netails.api.constant.PlatformConstant.DISCORD;
import static com.n1netails.n1netails.api.constant.PlatformConstant.TELEGRAM;

@Component
public class NotificationPlatformRegistry {

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

    private final Map<String, NotificationPlatform> platformHandlers;

    public Map<String, Boolean> getPlatformEnabledFlags() {
        return Map.of(
                EMAIL, this.emailEnabled,
                MSTEAMS, this.microsoftTeamsEnabled,
                SLACK, this.slackEnabled,
                DISCORD, this.discordEnabled,
                TELEGRAM, this.telegramEnabled
        );
    }

    public NotificationPlatformRegistry(List<NotificationPlatform> platforms) {
        // Automatically collect all beans implementing NotificationPlatform
        platformHandlers = platforms.stream()
                .collect(Collectors.toMap(NotificationPlatform::getPlatformName, Function.identity()));
    }

    public NotificationPlatform get(String platform) {
        return platformHandlers.get(platform);
    }
}
