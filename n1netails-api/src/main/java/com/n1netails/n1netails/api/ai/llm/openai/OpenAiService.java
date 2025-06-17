package com.n1netails.n1netails.api.ai.llm.openai;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n1netails.n1netails.api.ai.llm.LlmService;

import com.n1netails.n1netails.api.model.ai.openai.response.TextCompletionResponse;
import com.n1netails.n1netails.api.model.ai.openai.response.TextContent;
import com.n1netails.n1netails.api.model.ai.openai.response.TextOutput;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenAiService implements LlmService {

  private final WebClient openaiWebClient;

  @Override
  public String completePrompt(String prompt) {
    Map<String, Object> requestBody = Map.of(
            "model", "gpt-4.1",
            "instructions", "You are a devops engineer attempting to investigate an alert. Provide information about what you think the error might be.",
            "input", prompt
    );

    String result = openaiWebClient.post()
            .uri("/v1/responses")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .block();
    log.info("OPENAI response: {}", result);

    ObjectMapper mapper = new ObjectMapper();
    TextCompletionResponse textCompletionResponse = new TextCompletionResponse();
    try {
      textCompletionResponse = mapper.readValue(result, TextCompletionResponse.class);
    } catch (JsonProcessingException e) {
      log.error("Failed to parse response", e);
    }

    log.info("TEXT COMPLETION RESPONSE: {}", textCompletionResponse);

    StringBuilder sb = new StringBuilder();

    for (TextOutput output : textCompletionResponse.getOutput()) {
      for (TextContent content : output.getContent()) {
        sb.append(content.getText());
        sb.append("\n");  // optional: add newline between pieces
      }
    }

    String response = sb.toString();
    log.info("OPENAI response: {}", response);
    return response;
  }
}
