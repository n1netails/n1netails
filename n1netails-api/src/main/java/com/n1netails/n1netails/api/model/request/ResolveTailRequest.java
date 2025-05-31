package com.n1netails.n1netails.api.model.request;

import com.n1netails.n1netails.api.model.dto.TailSummary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResolveTailRequest {

    private Long userId;
    private TailSummary tailSummary;
    private String note;
}
