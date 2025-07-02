package com.n1netails.n1netails.api.model.ai.openai.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextContent {

    private String type;
    private List<Object> annotations;
    private List<Object> logprobs;
    private String text;

}
