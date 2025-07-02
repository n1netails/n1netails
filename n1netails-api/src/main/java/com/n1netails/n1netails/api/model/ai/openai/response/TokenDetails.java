package com.n1netails.n1netails.api.model.ai.openai.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TokenDetails {

    @JsonProperty("cached_tokens")
    private int cachedTokens;
    @JsonProperty("reasoning_tokens")
    private int reasoningTokens;
}
