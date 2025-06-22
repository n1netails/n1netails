package com.n1netails.n1netails.api.model.ai;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PromptInput {

    private String role;
    private String content;
}
