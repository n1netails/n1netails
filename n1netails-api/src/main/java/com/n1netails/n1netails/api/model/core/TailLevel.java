package com.n1netails.n1netails.api.model.core;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TailLevel {
    private String name;
    private String description;
    private boolean isDeletable;
}
