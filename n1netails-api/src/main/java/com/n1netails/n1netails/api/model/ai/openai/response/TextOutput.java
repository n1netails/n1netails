package com.n1netails.n1netails.api.model.ai.openai.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextOutput {

    private String id;
    private String type;
    private String status;
    private List<TextContent> content;
    private String role;
}
