package com.n1netails.n1netails.api.model.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Note {

    private Long id;
    private Long tailId;
    private Long organizationId;
    private Long userId;
    private String username;
    private boolean human;
    private boolean n1;
    private String llmProvider;
    private String llmModel;
    private Instant createdAt;
    private String content;
}
