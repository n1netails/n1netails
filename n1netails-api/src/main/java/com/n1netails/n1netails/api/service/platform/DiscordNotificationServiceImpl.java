package com.n1netails.n1netails.api.service.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.exception.type.NotificationException;
import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.model.notification.DiscordNotificationConfig;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.discord.DiscordColor;
import com.n1netails.n1netails.discord.exception.DiscordWebhookException;
import com.n1netails.n1netails.discord.internal.DiscordWebhookClientImpl;
import com.n1netails.n1netails.discord.model.Embed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.n1netails.n1netails.api.constant.PlatformConstant.DISCORD;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordNotificationServiceImpl implements NotificationPlatform {

    private final ObjectMapper objectMapper;

    @Override
    public String getPlatformName() {
        return DISCORD;
    }

    @Retryable(
            retryFor = DiscordWebhookException.class,
            maxAttempts = 7,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    @Override
    public void send(KudaTailRequest request, NotificationConfigEntity config)
            throws DiscordWebhookException {

        log.info("attempting to send discord notification");
        DiscordNotificationConfig discordConfig =
                objectMapper.convertValue(config.getDetails(), DiscordNotificationConfig.class);

        var webhookService = new com.n1netails.n1netails.discord.service.WebhookService();
        var client = new DiscordWebhookClientImpl(webhookService);

        Embed embed = new Embed();
        embed.setTitle(request.getTitle());
        embed.setDescription(request.getDescription());
        embed.setColor(DiscordColor.ORANGE.getValue());

        com.n1netails.n1netails.discord.model.WebhookMessage msg = new com.n1netails.n1netails.discord.model.WebhookMessage();
        msg.setUsername("N1netails Bot");
        msg.setContent("N1netails alert was triggered");
        msg.setEmbeds(List.of(embed));

        client.sendMessage(discordConfig.getWebhookUrl(), msg);
    }

    @Recover
    public void recover(DiscordWebhookException e, KudaTailRequest req, NotificationConfigEntity cfg) {
        throw new NotificationException("Failed sending Discord notification after retries");
    }
}
