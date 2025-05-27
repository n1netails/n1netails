package com.n1netails.n1netails.api.model.core;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TailStatus {
    private String name;
    private boolean isDeletable;
}
