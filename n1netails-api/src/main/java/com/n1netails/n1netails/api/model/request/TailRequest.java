package com.n1netails.n1netails.api.model.request;

import com.n1netails.n1netails.api.model.dto.TailLevel;
import com.n1netails.n1netails.api.model.dto.TailType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TailRequest {
    private String title;
    private String description;
    private String details;
    private Instant timestamp;
    private Instant resolvedTimestamp;
    private String assignedUserId;
    private String status;
    private TailLevel level;
    private TailType type;
    private Map<String, String> metadata;
}
