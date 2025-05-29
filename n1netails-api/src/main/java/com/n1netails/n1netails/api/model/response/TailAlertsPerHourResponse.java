package com.n1netails.n1netails.api.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TailAlertsPerHourResponse {

    private List<String> labels;
    private List<Integer> data;
}
