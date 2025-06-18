package com.n1netails.n1netails.api.model.ai.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TextCompletionRequest {

    private String model;
    private String instructions;
    private String input;
}
