package com.n1netails.n1netails.api.service.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.exception.type.NotificationException;
import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.model.notification.MsTeamsNotificationConfig;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.api.util.EmojiUtil;
import com.n1netails.n1netails.teams.api.TeamsWebhookClient;
import com.n1netails.n1netails.teams.exception.TeamsWebhookException;
import com.n1netails.n1netails.teams.internal.TeamsWebhookClientImpl;
import com.n1netails.n1netails.teams.model.Fact;
import com.n1netails.n1netails.teams.model.MessageCard;
import com.n1netails.n1netails.teams.model.Section;
import com.n1netails.n1netails.teams.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.n1netails.n1netails.api.constant.PlatformConstant.MSTEAMS;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamsNotificationServiceImpl implements NotificationPlatform {

    @Value("${n1netails.ui}")
    private String ui;

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

        MessageCard messageCard = new MessageCard();
        messageCard.setTitle(EmojiUtil.getTailLevelEmoji(request.getLevel()) + request.getTitle());
        messageCard.setSummary(request.getDescription());

        List<Section> sections = new ArrayList<>();

        if (!request.getMetadata().isEmpty()) {
            Section section = new Section();
            section.setTitle("Metadata");
            List<Fact> facts = new ArrayList<>();
            request.getMetadata().forEach((key, value) -> {
                facts.add(new Fact(key, value));
            });
            section.setFacts(facts);
            sections.add(section);
        }

        if (request.getDetails() != null && !request.getDetails().isEmpty() && !request.getDetails().isBlank()) {
            Section detailsSection = new Section();
            detailsSection.setTitle("Details");
            List<Fact> facts = new ArrayList<>();
            facts.add(new Fact("content", request.getDetails()));
            detailsSection.setFacts(facts);
            sections.add(detailsSection);
        }

        Section linkSection = new Section();
        linkSection.setTitle("View notification");
        List<Fact> facts = new ArrayList<>();
        facts.add(new Fact("Dashboard", "[N1netails]("+ui+")"));
        linkSection.setFacts(facts);
        sections.add(linkSection);

        messageCard.setSections(sections);
        client.sendMessage(teamsConfig.getWebhookUrl(), messageCard);
    }

    @Recover
    public void recover(TeamsWebhookException e, KudaTailRequest req, NotificationConfigEntity cfg) {
        throw new NotificationException("Failed sending teams notification after retries");
    }
}
