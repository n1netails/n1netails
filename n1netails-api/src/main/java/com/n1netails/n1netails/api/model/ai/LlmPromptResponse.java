package com.n1netails.n1netails.api.model.ai;

import lombok.Data;

@Data
public class LlmPromptResponse {

    String provider;
    String model;
    String completion;
    Long userId;
    Long organizationId;
    Long tailId;
}
