package com.n1netails.n1netails.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
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
    private Long organizationId;

    public TailSummary(Long id, String title, String description, Instant timestamp, Instant resolvedTimestamp, Long assignedUserId, String level, String type, String status, Long organizationId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
        this.resolvedTimestamp = resolvedTimestamp;
        this.assignedUserId = assignedUserId;
        this.level = level;
        this.type = type;
        this.status = status;
        this.organizationId = organizationId;
    }
}

