package com.n1netails.n1netails.api.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.exception.type.NotificationException;
import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.model.entity.UserNotificationPreferenceEntity;
import com.n1netails.n1netails.api.model.entity.UsersEntity;
import com.n1netails.n1netails.api.model.notification.*;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.api.repository.NotificationConfigRepository;
import com.n1netails.n1netails.api.repository.UserNotificationPreferenceRepository;
import com.n1netails.n1netails.api.service.EmailService;
import com.n1netails.n1netails.api.service.EncryptionService;
import com.n1netails.n1netails.api.service.NotificationService;
import com.n1netails.n1netails.discord.DiscordColor;
import com.n1netails.n1netails.discord.api.DiscordWebhookClient;
import com.n1netails.n1netails.discord.exception.DiscordWebhookException;
import com.n1netails.n1netails.discord.internal.DiscordWebhookClientImpl;
import com.n1netails.n1netails.discord.model.Embed;
import com.n1netails.n1netails.discord.model.WebhookMessage;
import com.n1netails.n1netails.slack.api.SlackClient;
import com.n1netails.n1netails.slack.exception.SlackClientException;
import com.n1netails.n1netails.slack.internal.SlackClientImpl;
import com.n1netails.n1netails.slack.model.SlackMessage;
import com.n1netails.n1netails.slack.service.BotService;
import com.n1netails.n1netails.teams.api.TeamsWebhookClient;
import com.n1netails.n1netails.teams.exception.TeamsWebhookException;
import com.n1netails.n1netails.teams.internal.TeamsWebhookClientImpl;
import com.n1netails.n1netails.teams.service.WebhookService;
import com.n1netails.n1netails.telegram.api.TelegramClient;
import com.n1netails.n1netails.telegram.exception.TelegramClientException;
import com.n1netails.n1netails.telegram.internal.TelegramClientImpl;
import com.n1netails.n1netails.telegram.model.TelegramMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("notificationService")
public class NotificationServiceImpl implements NotificationService {

    public static final String TELEGRAM = "telegram";
    public static final String DISCORD = "discord";
    public static final String SLACK = "slack";
    public static final String MSTEAMS = "msteams";
    public static final String EMAIL = "email";

    @Value("${n1netails.notifications.enabled}")
    private boolean notificationsEnabled;
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

    private final NotificationConfigRepository notificationConfigRepository;
    private final UserNotificationPreferenceRepository userNotificationPreferenceRepository;
    private final EncryptionService encryptionService;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public List<NotificationConfigEntity> getDecryptedConfigurations(Long tokenId) {
        return notificationConfigRepository.findByTokenId(tokenId).stream()
                .peek(config -> {
                    config.getDetails().forEach((key, value) -> {
                        try {
                            config.getDetails().put(key, encryptionService.decrypt(value));
                        } catch (Exception e) {
                            log.error("Error decrypting notification config details", e);
                            throw new RuntimeException("Error decrypting notification config details", e);
                        }
                    });
                })
                .collect(Collectors.toList());
    }

    public void saveConfigurations(Long tokenId, List<NotificationConfigEntity> configs) {
        notificationConfigRepository.findByTokenId(tokenId).forEach(notificationConfigRepository::delete);
        configs.forEach(config -> {
            try {
                config.setTokenId(tokenId);
                config.getDetails().forEach((key, value) -> {
                    try {
                        config.getDetails().put(key, encryptionService.encrypt(value));
                    } catch (Exception e) {
                        log.error("Error encrypting notification config details", e);
                        throw new RuntimeException("Error encrypting notification config details", e);
                    }
                });
                notificationConfigRepository.save(config);
            } catch (Exception e) {
                log.error("Error saving notification config", e);
                throw new RuntimeException("Error saving notification config", e);
            }
        });
    }

    public List<String> getUserNotificationPreferences(Long userId) {
        return userNotificationPreferenceRepository.findByUserId(userId).stream()
                .map(UserNotificationPreferenceEntity::getPlatform)
                .collect(Collectors.toList());
    }

    public void saveUserNotificationPreferences(Long userId, List<String> platforms) {
        userNotificationPreferenceRepository.findByUserId(userId).forEach(userNotificationPreferenceRepository::delete);
        platforms.forEach(platform -> {
            UserNotificationPreferenceEntity preference = new UserNotificationPreferenceEntity();
            preference.setUserId(userId);
            preference.setPlatform(platform);
            userNotificationPreferenceRepository.save(preference);
        });
    }

    @Override
    public void sendNotificationAlert(UsersEntity usersEntity, KudaTailRequest request, Long tokenId) {
        if (!notificationsEnabled) return;

        List<UserNotificationPreferenceEntity> userNotificationPreferences = userNotificationPreferenceRepository.findByUserId(usersEntity.getId());
        Set<String> userSelectedPlatforms = new HashSet<>();
        userNotificationPreferences.forEach(preference -> {
            userSelectedPlatforms.add(preference.getPlatform());
        });

        List<NotificationConfigEntity> notificationConfigEntities = this.getDecryptedConfigurations(tokenId);
        notificationConfigEntities.forEach(config -> {
            if (userSelectedPlatforms.contains(config.getPlatform())) {
                switch (config.getPlatform()) {
                    case EMAIL -> {
                        try {
                            if (emailEnabled) this.runEmail(request, config);
                        } catch (Exception e) {
                            throw new NotificationException("There was an issue sending a notification to Email");
                        }
                    }
                    case MSTEAMS -> {
                        try {
                            if (microsoftTeamsEnabled) this.runTeamsWebhookClient(request, config);
                        } catch (TeamsWebhookException e) {
                            throw new NotificationException("There was an issue sending a notification to Microsoft Teams");
                        }
                    }
                    case SLACK -> {
                        try {
                            if (slackEnabled) this.runSlackClient(request, config);
                        } catch (SlackClientException e) {
                            throw new NotificationException("There was an issue sending a notification to Slack");
                        }
                    }
                    case DISCORD -> {
                        try {
                            if (discordEnabled) this.runDiscordWebhookClient(request, config);
                        } catch (DiscordWebhookException e) {
                            throw new NotificationException("There was an issue sending a notification to Discord");
                        }
                    }
                    case TELEGRAM -> {
                        try {
                            if (telegramEnabled) this.runTelegramClient(request, config);
                        } catch (TelegramClientException e) {
                            throw new NotificationException("There was an issue sending a notification to Telegram");
                        }
                    }
                    default -> throw new NotificationException("Unsupported notification platform: " + config.getPlatform());
                }
            }
        });
    }

    private void runEmail(KudaTailRequest request, NotificationConfigEntity configEntity) {
        EmailNotificationConfig emailNotificationConfig = objectMapper.convertValue(configEntity.getDetails(), EmailNotificationConfig.class);
        this.emailService.sendNotificationEmail(emailNotificationConfig.getAddress(), request);
    }

    private void runTeamsWebhookClient(KudaTailRequest request, NotificationConfigEntity configEntity) throws TeamsWebhookException {
        MsTeamsNotificationConfig msTeamsNotificationConfig = objectMapper.convertValue(configEntity.getDetails(), MsTeamsNotificationConfig.class);
        WebhookService webhookService = new WebhookService();
        TeamsWebhookClient client = new TeamsWebhookClientImpl(webhookService);
        com.n1netails.n1netails.teams.model.WebhookMessage message = new com.n1netails.n1netails.teams.model.WebhookMessage();
        String content;

        if (request.getTitle() != null && request.getDescription() != null) {
            content = String.format("%s \n %s", request.getTitle(), request.getDescription());
        } else if(request.getTitle() != null) {
            content = String.format("%s", request.getTitle());
        } else if (request.getDescription() != null) {
            content = String.format("%s", request.getDescription());
        } else {
            content = "N1netails alert was triggered";
        }

        message.setContent(content);
        String teamsWebhookUrl = msTeamsNotificationConfig.getWebhookUrl();
        client.sendMessage(teamsWebhookUrl, message);
    }

    private void runSlackClient(KudaTailRequest request, NotificationConfigEntity configEntity) throws SlackClientException {
        SlackNotificationConfig slackNotificationConfig = objectMapper.convertValue(configEntity.getDetails(), SlackNotificationConfig.class);
        String token = slackNotificationConfig.getBotToken();
        String channel = slackNotificationConfig.getChannel();
        BotService botService = new BotService(token);
        SlackClient slackClient = new SlackClientImpl(botService);
        SlackMessage message = new SlackMessage();
        message.setChannel(channel);

        if (request.getTitle() != null && request.getDescription() != null) {
            message.setText(String.format("%s \n %s", request.getTitle(), request.getDescription()));
        } else if(request.getTitle() != null) {
            message.setText(String.format("%s", request.getTitle()));
        } else if (request.getDescription() != null) {
            message.setText(String.format("%s", request.getDescription()));
        } else {
            message.setText("N1netails alert was triggered");
        }

        slackClient.sendMessage(message);
    }

    private void runDiscordWebhookClient(KudaTailRequest request, NotificationConfigEntity configEntity) throws DiscordWebhookException {
        DiscordNotificationConfig discordNotificationConfig = objectMapper.convertValue(configEntity.getDetails(), DiscordNotificationConfig.class);
        com.n1netails.n1netails.discord.service.WebhookService webhookService = new com.n1netails.n1netails.discord.service.WebhookService();
        DiscordWebhookClient discordWebhookClient = new DiscordWebhookClientImpl(webhookService);

        Embed embed = new Embed();
        embed.setTitle(request.getTitle());
        embed.setDescription(request.getDescription());
        embed.setColor(DiscordColor.ORANGE.getValue());

        WebhookMessage msg = new WebhookMessage();
        msg.setUsername("N1netails Bot");
        msg.setContent("N1netails alert was triggered");
        msg.setEmbeds(List.of(embed));

        String webhookUrl = discordNotificationConfig.getWebhookUrl();
        discordWebhookClient.sendMessage(webhookUrl, msg);
    }

    private void runTelegramClient(KudaTailRequest request, NotificationConfigEntity configEntity) throws TelegramClientException {
        TelegramNotificationConfig telegramNotificationConfig = objectMapper.convertValue(configEntity.getDetails(), TelegramNotificationConfig.class);
        com.n1netails.n1netails.telegram.service.BotService botService = new com.n1netails.n1netails.telegram.service.BotService();
        TelegramClient telegramClient = new TelegramClientImpl(botService);
        TelegramMessage telegramMessage;

        if (request.getTitle() != null && request.getDescription() != null) {
            telegramMessage = new TelegramMessage(String.format("%s \n %s", request.getTitle(), request.getDescription()), false);
        } else if(request.getTitle() != null) {
            telegramMessage = new TelegramMessage(String.format("%s", request.getTitle()), false);
        } else if (request.getDescription() != null) {
            telegramMessage = new TelegramMessage(String.format("%s", request.getDescription()), false);
        } else {
            telegramMessage = new TelegramMessage("N1netails alert was triggered", false);
        }

        String chatId = telegramNotificationConfig.getChatId();
        String botToken = telegramNotificationConfig.getBotToken();
        telegramClient.sendMessage(chatId, botToken, telegramMessage);
    }
}
