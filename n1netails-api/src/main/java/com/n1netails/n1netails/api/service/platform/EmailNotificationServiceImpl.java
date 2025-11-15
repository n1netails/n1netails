package com.n1netails.n1netails.api.service.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.exception.type.NotificationException;
import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.model.notification.EmailNotificationConfig;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import static com.n1netails.n1netails.api.constant.PlatformConstant.EMAIL;

@Service
@RequiredArgsConstructor
public class EmailNotificationServiceImpl implements NotificationPlatform {

    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    @Override
    public String getPlatformName() {
        return EMAIL;
    }

    @Retryable(
            retryFor = Exception .class,
            maxAttempts = 7,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    @Override
    public void send(KudaTailRequest request, NotificationConfigEntity config) {

        EmailNotificationConfig emailConfig =
                objectMapper.convertValue(config.getDetails(), EmailNotificationConfig.class);

        emailService.sendNotificationEmail(emailConfig.getAddress(), request);
    }

    @Recover
    public void recover(Exception e, KudaTailRequest req, NotificationConfigEntity cfg) {
        throw new NotificationException("Failed sending email notification after retries");
    }
}
