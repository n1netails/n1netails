package com.n1netails.n1netails.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
public class TailSummary {
    private Long id;
    private String title;
    private String description;
    private Instant timestamp;
    private Instant resolvedTimestamp;
    private Long assignedUserId;
    private String level;
    private String type;
    private String status;
}

