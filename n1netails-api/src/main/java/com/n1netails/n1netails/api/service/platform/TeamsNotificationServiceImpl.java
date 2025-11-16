package com.n1netails.n1netails.api.service.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.exception.type.NotificationException;
import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.model.notification.MsTeamsNotificationConfig;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.teams.api.TeamsWebhookClient;
import com.n1netails.n1netails.teams.exception.TeamsWebhookException;
import com.n1netails.n1netails.teams.internal.TeamsWebhookClientImpl;
import com.n1netails.n1netails.teams.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import static com.n1netails.n1netails.api.constant.PlatformConstant.MSTEAMS;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamsNotificationServiceImpl implements NotificationPlatform {

    private final ObjectMapper objectMapper;

    @Override
    public String getPlatformName() {
        return MSTEAMS;
    }

    @Retryable(
            retryFor = TeamsWebhookException.class,
            maxAttempts = 7,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    @Override
    public void send(KudaTailRequest request, NotificationConfigEntity config) throws TeamsWebhookException {

        log.info("attempting to send teams notification");
        MsTeamsNotificationConfig teamsConfig =
                objectMapper.convertValue(config.getDetails(), MsTeamsNotificationConfig.class);

        var webhookService = new WebhookService();
        TeamsWebhookClient client = new TeamsWebhookClientImpl(webhookService);

        com.n1netails.n1netails.teams.model.WebhookMessage message =
                new com.n1netails.n1netails.teams.model.WebhookMessage();

        String content;
        if (request.getTitle() != null && request.getDescription() != null) {
            content = String.format("%s \n %s", request.getTitle(), request.getDescription());
        } else if(request.getTitle() != null) {
            content = request.getTitle();
        } else if(request.getDescription() != null) {
            content = request.getDescription();
        } else {
            content = "N1netails alert was triggered";
        }

        message.setContent(content);
        client.sendMessage(teamsConfig.getWebhookUrl(), message);
    }

    @Recover
    public void recover(TeamsWebhookException e, KudaTailRequest req, NotificationConfigEntity cfg) {
        throw new NotificationException("Failed sending teams notification after retries");
    }
}
