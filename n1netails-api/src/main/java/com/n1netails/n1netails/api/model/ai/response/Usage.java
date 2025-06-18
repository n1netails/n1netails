package com.n1netails.n1netails.api.model.ai.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Usage {

    @JsonProperty("input_tokens")
    private int inputTokens;
    @JsonProperty("input_tokens_details")
    private TokenDetails inputTokensDetails;
    @JsonProperty("output_tokens")
    private int outputTokens;
    @JsonProperty("output_tokens_details")
    private TokenDetails outputTokensDetails;
    @JsonProperty("total_tokens")
    private int totalTokens;
}
