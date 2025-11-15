package com.n1netails.n1netails.api.service.platform;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotificationPlatformRegistry {

    private final Map<String, NotificationPlatform> platformHandlers;

    public NotificationPlatformRegistry(List<NotificationPlatform> platforms) {
        // Automatically collect all beans implementing NotificationPlatform
        platformHandlers = platforms.stream()
                .collect(Collectors.toMap(NotificationPlatform::getPlatformName, Function.identity()));
    }

    public NotificationPlatform get(String platform) {
        return platformHandlers.get(platform);
    }
}
