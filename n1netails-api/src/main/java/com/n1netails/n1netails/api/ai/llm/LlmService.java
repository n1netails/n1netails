package com.n1netails.n1netails.api.ai.llm;

public interface LlmService {

    String completePrompt(String model, String prompt);
}
