package com.n1netails.n1netails.api.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class TailTimestampAndResolvedTimestamp {
    private Instant timestamp;
    private Instant resolvedTimestamp;
}
