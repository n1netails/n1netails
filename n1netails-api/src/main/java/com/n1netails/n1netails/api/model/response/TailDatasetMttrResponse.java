package com.n1netails.n1netails.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TailDatasetMttrResponse {
    private List<String> labels;
    private List<Double> data;
}