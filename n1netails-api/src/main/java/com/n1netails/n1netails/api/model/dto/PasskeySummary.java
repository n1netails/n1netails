package com.n1netails.n1netails.api.model.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PasskeySummary {
    private Long id;
    private byte[] credentialId;
    private byte[] publicKeyCose;
    private long signatureCount;
    private byte[] userHandle;
    private Date lastUsedAt;
    private Date registeredAt;
    private Long userId;
}
