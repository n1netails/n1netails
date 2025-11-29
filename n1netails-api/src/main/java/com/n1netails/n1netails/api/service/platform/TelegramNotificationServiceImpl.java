package com.n1netails.n1netails.api.service.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.exception.type.NotificationException;
import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.model.notification.TelegramNotificationConfig;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.api.util.EmojiUtil;
import com.n1netails.n1netails.telegram.api.TelegramClient;
import com.n1netails.n1netails.telegram.exception.TelegramClientException;
import com.n1netails.n1netails.telegram.internal.TelegramClientImpl;
import com.n1netails.n1netails.telegram.model.Button;
import com.n1netails.n1netails.telegram.model.InlineKeyboardMarkup;
import com.n1netails.n1netails.telegram.model.TelegramMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.n1netails.n1netails.api.constant.PlatformConstant.TELEGRAM;

@Slf4j
@Service
@RequiredArgsConstructor
public class TelegramNotificationServiceImpl implements NotificationPlatform {

    @Value("${n1netails.ui}")
    private String ui;

    private final ObjectMapper objectMapper;

    @Override
    public String getPlatformName() {
        return TELEGRAM;
    }

    @Retryable(
            retryFor = TelegramClientException.class,
            maxAttempts = 7,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    @Override
    public void send(KudaTailRequest request, NotificationConfigEntity config) throws TelegramClientException {

        log.info("attempting to send telegram notification");
        TelegramNotificationConfig telegramConfig =
                objectMapper.convertValue(config.getDetails(), TelegramNotificationConfig.class);
        TelegramClient client = new TelegramClientImpl(new com.n1netails.n1netails.telegram.service.BotService());

        String text = "";
        text += EmojiUtil.getTailLevelEmoji(request.getLevel());
        if (request.getTitle() != null && request.getDescription() != null) {
            text += String.format("%s \n %s", request.getTitle(), request.getDescription());
        } else if(request.getTitle() != null) {
            text += request.getTitle();
        } else if(request.getDescription() != null) {
            text += request.getDescription();
        } else {
            text += "N1netails alert was triggered";
        }

        if (!request.getMetadata().isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("\n");
            request.getMetadata().forEach((key, value) -> {
                sb.append("\n\t " + key + ": " + value);
            });
            text += sb.toString();
        }

        // override localhost url when developing as telegram does not allow localhost urls.
        if (ui.contains("localhost")) ui = "https://app.n1netails.com";
        Button dashboardButton = new Button("View Notification", ui);
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup(List.of(List.of(dashboardButton)));

        TelegramMessage message = new TelegramMessage(text, false, keyboardMarkup);
        client.sendMessage(telegramConfig.getChatId(), telegramConfig.getBotToken(), message);
    }

    @Recover
    public void recover(TelegramClientException e, KudaTailRequest req, NotificationConfigEntity cfg) {
        throw new NotificationException("Failed sending telegram notification after retries");
    }
}
