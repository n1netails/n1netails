package com.n1netails.n1netails.api.service;

import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.model.ai.LlmRequest;
import com.n1netails.n1netails.api.model.ai.LlmResponse;

public interface InvestigationService {

    LlmResponse investigateWithLlm(LlmRequest llmRequest) throws TailNotFoundException;
}
