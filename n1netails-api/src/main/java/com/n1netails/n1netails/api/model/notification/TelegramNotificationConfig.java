package com.n1netails.n1netails.api.model.notification;

import lombok.Data;

@Data
public class TelegramNotificationConfig {

    private String chatId;
    private String botToken;
}
