package com.n1netails.n1netails.api.ai.llm.gemini;

import com.n1netails.n1netails.api.ai.llm.LlmService;
import org.springframework.stereotype.Service;

@Service
public class GeminiService implements LlmService {

    @Override
    public String completePrompt(String prompt) {
        return "";
    }
}
