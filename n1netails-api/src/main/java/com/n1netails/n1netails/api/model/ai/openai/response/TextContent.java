package com.n1netails.n1netails.api.model.ai.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TextContent {

    private String type;
    private List<Object> annotations;
    private String text;

}
