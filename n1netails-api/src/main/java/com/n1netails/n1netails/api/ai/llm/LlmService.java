package com.n1netails.n1netails.api.ai.llm;

import com.n1netails.n1netails.api.model.ai.openai.request.PromptInput;

import java.util.List;

public interface LlmService {

    String completePrompt(String model, String prompt);
//    String completePrompt(String model, List<PromptInput> promptInputs);
}
