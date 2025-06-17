package com.n1netails.n1netails.api.model.ai.openai.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextFormat {

    private FormatType format;

    @Getter
    @Setter
    public static class FormatType {
        private String type;
    }
}
