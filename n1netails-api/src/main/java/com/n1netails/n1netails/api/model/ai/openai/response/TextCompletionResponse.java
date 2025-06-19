package com.n1netails.n1netails.api.model.ai.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.JoinColumn;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@ToString
public class TextCompletionResponse {

    private String id;
    private String object;
    @JsonProperty("created_at")
    private long createdAt;
    private String status;
    private boolean background;
    private String error;
    @JsonProperty("incomplete_details")
    private Object incompleteDetails;
    private String instructions;
    @JsonProperty("max_output_tokens")
    private Integer maxOutputTokens;
    @JsonProperty("max_tool_calls")
    private Integer maxToolCalls;
    private String model;
    private List<TextOutput> output;
    @JsonProperty("parallel_tool_calls")
    private boolean parallelToolCalls;
    @JsonProperty("previous_response_id")
    private String previousResponseId;
    private Reasoning reasoning;
    @JsonProperty("service_tier")
    private String serviceTier;
    private boolean store;
    private double temperature;
    private TextFormat text;
    @JsonProperty("tool_choice")
    private String toolChoice;
    private List<Object> tools;
    @JsonProperty("top_p")
    private double topP;
    private String truncation;
    private Usage usage;
    private Object user;
    private Map<String, Object> metadata;

}
