package com.n1netails.n1netails.api.model.notification;

import lombok.Data;

@Data
public class SlackNotificationConfig {

    private String channel;
    private String botToken;
}
