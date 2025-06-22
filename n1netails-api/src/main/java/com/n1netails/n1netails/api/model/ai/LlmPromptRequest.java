package com.n1netails.n1netails.api.model.ai;

import lombok.Data;

@Data
public class LlmPromptRequest {

    String provider;
    String model;
    String prompt;
    Long userId;
    Long organizationId;
    Long tailId;
}
