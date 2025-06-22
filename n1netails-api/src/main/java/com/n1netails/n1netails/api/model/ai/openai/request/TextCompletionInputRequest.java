package com.n1netails.n1netails.api.model.ai.openai.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class TextCompletionInputRequest {

    private String model;
    private List<PromptInput> input;
}
