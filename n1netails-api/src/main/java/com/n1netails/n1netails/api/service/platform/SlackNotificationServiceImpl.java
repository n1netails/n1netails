package com.n1netails.n1netails.api.service.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.exception.type.NotificationException;
import com.n1netails.n1netails.api.model.entity.NotificationConfigEntity;
import com.n1netails.n1netails.api.model.notification.SlackNotificationConfig;
import com.n1netails.n1netails.api.model.request.KudaTailRequest;
import com.n1netails.n1netails.api.util.EmojiUtil;
import com.n1netails.n1netails.slack.exception.SlackClientException;
import com.n1netails.n1netails.slack.internal.SlackClientImpl;
import com.n1netails.n1netails.slack.model.SlackMessage;
import com.n1netails.n1netails.slack.service.BotService;
import com.slack.api.model.block.Blocks;
import com.slack.api.model.block.LayoutBlock;
import com.slack.api.model.block.composition.BlockCompositions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.n1netails.n1netails.api.constant.PlatformConstant.SLACK;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackNotificationServiceImpl implements NotificationPlatform {

    @Value("${n1netails.ui}")
    private String ui;

    private final ObjectMapper objectMapper;

    @Override
    public String getPlatformName() {
        return SLACK;
    }

    @Retryable(
            retryFor = SlackClientException  .class,
            maxAttempts = 7,
            backoff = @Backoff(delay = 2000, multiplier = 2)
    )
    @Override
    public void send(KudaTailRequest request, NotificationConfigEntity config) throws SlackClientException {

        log.info("attempting to send slack notification");
        SlackNotificationConfig slackConfig =
                objectMapper.convertValue(config.getDetails(), SlackNotificationConfig.class);

        var botService = new BotService(slackConfig.getBotToken());
        var client = new SlackClientImpl(botService);

        SlackMessage msg = new SlackMessage();
        msg.setChannel(slackConfig.getChannel());

        String text = "";
        text += EmojiUtil.getTailLevelEmoji(request.getLevel());

        if (request.getTitle() != null && request.getDescription() != null) {
            text += String.format("%s \n %s", request.getTitle(), request.getDescription());
        } else if (request.getTitle() != null) {
            text += request.getTitle();
        } else if (request.getDescription() != null) {
            text += request.getDescription();
        } else {
            text += "N1netails alert was triggered";
        }
        msg.setText(text);

        List<LayoutBlock> layoutBlocks = new ArrayList<>();

        String finalText = text;
        layoutBlocks.add(Blocks.section(section -> section.text(BlockCompositions.markdownText(finalText))));

        if (!request.getMetadata().isEmpty()) {
            request.getMetadata().forEach((key, value) -> {
                layoutBlocks.add(Blocks.section(section -> section.text(BlockCompositions.markdownText("- " + key + ": " + value))));
            });
        }

        if (request.getDetails() != null && !request.getDetails().isEmpty() && !request.getDetails().isBlank()) {
            layoutBlocks.add(Blocks.section(section -> section.text(BlockCompositions.markdownText("Details: " + request.getDetails()))));
        }
        layoutBlocks.add(Blocks.section(section -> section.text(BlockCompositions.markdownText("View notification: " + ui))));

        msg.setBlocks(layoutBlocks);
        client.sendMessage(msg);
    }

    @Recover
    public void recover(SlackClientException e, KudaTailRequest req, NotificationConfigEntity cfg) {
        throw new NotificationException("Failed sending slack notification after retries");
    }
}
