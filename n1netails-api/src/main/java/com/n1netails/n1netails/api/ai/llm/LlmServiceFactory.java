package com.n1netails.n1netails.api.ai.llm;

import com.n1netails.n1netails.api.ai.llm.gemini.GeminiService;
import com.n1netails.n1netails.api.ai.llm.openai.OpenAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class LlmServiceFactory {

    @Value("${gemini.enabled}")
    private boolean geminiEnabled;

    @Value("${openai.enabled}")
    private boolean openaiEnabled;

    private final WebClient openaiWebClient;

    public LlmService get(String provider) {
        return switch (provider) {
            case "openai" -> {
                if (!openaiEnabled) throw new IllegalArgumentException("OpenAI is not enabled");
                yield new OpenAiService(openaiWebClient);
            }
            case "gemini" -> {
                if (!geminiEnabled) throw new IllegalArgumentException("Gemini is not enabled");
                yield new GeminiService();
            }
            default -> throw new IllegalArgumentException("Unknown provider: " + provider);
        };
    }
}
