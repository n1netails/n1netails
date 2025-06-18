package com.n1netails.n1netails.api.service.impl;

import com.n1netails.n1netails.api.ai.llm.LlmService;
import com.n1netails.n1netails.api.ai.llm.LlmServiceFactory;
import com.n1netails.n1netails.api.exception.type.TailNotFoundException;
import com.n1netails.n1netails.api.model.ai.LlmRequest;
import com.n1netails.n1netails.api.model.ai.LlmResponse;
import com.n1netails.n1netails.api.model.entity.TailEntity;
import com.n1netails.n1netails.api.repository.TailRepository;
import com.n1netails.n1netails.api.service.InvestigationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@Qualifier("investigationService")
public class InvestigationServiceImpl implements InvestigationService {

    private final LlmServiceFactory llmServiceFactory;
    private final TailRepository tailRepository;

    @Override
    public LlmResponse investigateWithLlm(LlmRequest llmRequest) throws TailNotFoundException {

        LlmService llmService = this.llmServiceFactory.get(llmRequest.getProvider());
        TailEntity tailEntity = this.tailRepository.findById(llmRequest.getTailId())
                .orElseThrow(() -> new TailNotFoundException("The requested tail does not exist."));

        StringBuilder metadataBuilder = new StringBuilder();
        tailEntity.getCustomVariables().forEach(tailVariableEntity ->
                metadataBuilder
                        .append(tailVariableEntity.getKey())
                        .append(": ")
                        .append(tailVariableEntity.getValue())
                        .append("\n")
        );
        String metadata = metadataBuilder.toString();

        String prompt = String.format("""
                Title: %s
                Description: %s
                Details: %s
                Metadata: %s
                """,
                tailEntity.getTitle(),tailEntity.getDescription(), tailEntity.getDetails(), metadata);

        String promptResult = llmService.completePrompt(prompt);

        LlmResponse llmResponse = new LlmResponse();
        llmResponse.setPromptCompletionResponse(promptResult);
        return llmResponse;
    }
}
