package com.n1netails.n1netails.api.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TailVariable {
    private String key;
    private String value;
}
