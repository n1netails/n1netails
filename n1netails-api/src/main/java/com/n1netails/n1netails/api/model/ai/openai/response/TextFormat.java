package com.n1netails.n1netails.api.model.ai.openai.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class TextFormat {

    private FormatType format;

    @Getter
    @Setter
    public static class FormatType {
        private String type;
    }
}
