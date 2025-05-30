package com.n1netails.n1netails.api.model.dto;

import com.n1netails.n1netails.api.model.entity.TailLevelEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class TailLevelAndTimestamp {
    Instant timestamp;
    TailLevelEntity level;
}
