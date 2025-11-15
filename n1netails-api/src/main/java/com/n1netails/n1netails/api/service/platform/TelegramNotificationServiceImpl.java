package com.n1netails.n1netails.api.service.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.exception.type.NotificationException;
import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.model.notification.TelegramNotificationConfig;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.telegram.api.TelegramClient;
import com.n1netails.n1netails.telegram.exception.TelegramClientException;
import com.n1netails.n1netails.telegram.internal.TelegramClientImpl;
import com.n1netails.n1netails.telegram.model.TelegramMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import static com.n1netails.n1netails.api.constant.PlatformConstant.TELEGRAM;

@Service
@RequiredArgsConstructor
public class TelegramNotificationServiceImpl implements NotificationPlatform {

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

        TelegramNotificationConfig telegramConfig =
                objectMapper.convertValue(config.getDetails(), TelegramNotificationConfig.class);

        TelegramClient client = new TelegramClientImpl(new com.n1netails.n1netails.telegram.service.BotService());

        String text;
        if (request.getTitle() != null && request.getDescription() != null) {
            text = String.format("%s \n %s", request.getTitle(), request.getDescription());
        } else if(request.getTitle() != null) {
            text = request.getTitle();
        } else if(request.getDescription() != null) {
            text = request.getDescription();
        } else {
            text = "N1netails alert was triggered";
        }

        TelegramMessage message = new TelegramMessage(text, false);
        client.sendMessage(telegramConfig.getChatId(), telegramConfig.getBotToken(), message);
    }

    @Recover
    public void recover(TelegramClientException e, KudaTailRequest req, NotificationConfigEntity cfg) {
        throw new NotificationException("Failed sending telegram notification after retries");
    }
}
