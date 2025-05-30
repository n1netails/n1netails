package com.n1netails.n1netails.api.model.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TailMonthlySummaryResponse {
    private List<String> labels;
    private List<TailDatasetResponse> datasets;
}
