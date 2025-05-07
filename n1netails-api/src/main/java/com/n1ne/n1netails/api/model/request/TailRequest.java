package com.n1ne.n1netails.api.model.request;

import com.n1ne.n1netails.api.model.dto.TailLevelDto;
import com.n1ne.n1netails.api.model.dto.TailStatusDto;
import com.n1ne.n1netails.api.model.dto.TailTypeDto;
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
    private TailLevelDto tailLevel;
    private TailTypeDto tailType;
    private TailStatusDto tailStatus;
    private Map<String, String> metadata;
}
