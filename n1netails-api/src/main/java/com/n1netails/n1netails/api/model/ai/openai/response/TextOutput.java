package com.n1netails.n1netails.api.model.ai.openai.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TextOutput {

    private String id;
    private String type;
    private String status;
    private List<TextContent> content;
    private String role;
}
