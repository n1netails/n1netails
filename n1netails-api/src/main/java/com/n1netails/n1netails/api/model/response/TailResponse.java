package com.n1netails.n1netails.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TailResponse {
    private Long id;
    // Fields needed for controller authorization logic
    private Long userId; // Owner of the tail
    private Long organizationId; // Organization this tail belongs to

    private String title;
    private String description;
    private Instant timestamp;
    private Instant resolvedTimestamp;
    private Long assignedUserId; // User assigned to resolve the tail
    private String assignedUsername;
    private String details;
    private String level;
    private String type;
    private String status;
    private Map<String, String> metadata;
}
