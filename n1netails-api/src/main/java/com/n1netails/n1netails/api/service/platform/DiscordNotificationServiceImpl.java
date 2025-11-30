package com.n1netails.n1netails.api.service.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.exception.type.N1neTokenNotFoundException;
import com.n1netails.n1netails.api.exception.type.NotificationException;
import com.n1netails.n1netails.api.model.entity.N1neTokenEntity;
import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.notification.DiscordNotificationConfig;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.api.repository.N1neTokenRepository;
import com.n1netails.n1netails.api.util.EmojiUtil;
import com.n1netails.n1netails.discord.DiscordColor;
import com.n1netails.n1netails.discord.exception.DiscordWebhookException;
import com.n1netails.n1netails.discord.internal.DiscordWebhookClientImpl;
import com.n1netails.n1netails.discord.model.Embed;
import com.n1netails.n1netails.discord.model.EmbedBuilder;
import com.n1netails.n1netails.discord.model.WebhookMessage;
import com.n1netails.n1netails.discord.model.WebhookMessageBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.n1netails.n1netails.api.constant.PlatformConstant.DISCORD;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiscordNotificationServiceImpl implements NotificationPlatform {

    @Value("${n1netails.ui}")
    private String ui;

    private final N1neTokenRepository n1neTokenRepository;
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
            throws DiscordWebhookException, N1neTokenNotFoundException {

        log.info("attempting to send discord notification");
        N1neTokenEntity n1neTokenEntity = this.n1neTokenRepository.findById(config.getTokenId())
                .orElseThrow(() -> new N1neTokenNotFoundException("N1ne token entity not found."));
        UsersEntity user = n1neTokenEntity.getUser();

        DiscordNotificationConfig discordConfig =
                objectMapper.convertValue(config.getDetails(), DiscordNotificationConfig.class);

        var webhookService = new com.n1netails.n1netails.discord.service.WebhookService();
        var client = new DiscordWebhookClientImpl(webhookService);

        Embed.Author author = new Embed.Author();
        author.setName(user.getUsername());
        author.setUrl(ui);
        author.setIcon_url(user.getProfileImageUrl());

        List<Embed.EmbedField> fields = new ArrayList<>();
        request.getMetadata().forEach((key, value) -> {
            Embed.EmbedField field = new Embed.EmbedField();
            field.setName(key);
            field.setValue(value);
            field.setInline(true);
            fields.add(field);
        });

        Embed.Footer footer = new Embed.Footer();
        int year = java.time.LocalDate.now().getYear();
        footer.setText("N1netails @ " + year);
        footer.setIcon_url("https://raw.githubusercontent.com/n1netails/n1netails/refs/heads/main/n1netails_icon_transparent.png");
        String description = request.getDescription();

        Embed embed = new EmbedBuilder()
                .withTitle("View Notification")
                .withDescription(description)
                .withUrl(ui)
                .withColor(DiscordColor.ORANGE.getValue())
                .withAuthor(author)
                .withFields(fields)
                .withFooter(footer)
                .withTimestamp(Instant.now().toString())
                .build();

        String content = EmojiUtil.getTailLevelEmoji(request.getLevel()) +
                request.getTitle();

        WebhookMessage msg = new WebhookMessageBuilder()
                .withUsername("N1netails")
                .withContent(content)
                .withEmbeds(Collections.singletonList(embed))
                .build();
        client.sendMessage(discordConfig.getWebhookUrl(), msg);
    }

    @Recover
    public void recover(DiscordWebhookException e, KudaTailRequest req, NotificationConfigEntity cfg) {
        throw new NotificationException("Failed sending Discord notification after retries");
    }
}
