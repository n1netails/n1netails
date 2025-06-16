package com.n1netails.n1netails.api.ai.llm;

import com.n1netails.n1netails.api.ai.llm.gemini.GeminiService;
import com.n1netails.n1netails.api.ai.llm.openai.OpenAiService;

public class LlmServiceFactory {

    public static LlmService get(String provider) {
        return switch (provider) {
            case "openai" -> new OpenAiService();
            case "gemini" -> new GeminiService();
            default -> throw new IllegalArgumentException("Unknown provider");
        };
    }
}
