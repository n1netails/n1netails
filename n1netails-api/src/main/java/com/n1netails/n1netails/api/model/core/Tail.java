package com.n1netails.n1netails.api.model.core;

import lombok.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Tail {
    private String title;
    private String description;
    private String details;
    @Builder.Default
    private Instant timestamp = Instant.now();
    private String status;
    private TailLevel level;
    private TailType type;
    @Builder.Default
    private Map<String, String> metadata = new HashMap<>();
}
