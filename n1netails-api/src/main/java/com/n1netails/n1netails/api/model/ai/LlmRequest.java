package com.n1netails.n1netails.api.model.ai;

import lombok.Data;

@Data
public class LlmRequest {

    String provider;
    String model;
    Long tailId;
    Long userId;
    Long organizationId;
}
