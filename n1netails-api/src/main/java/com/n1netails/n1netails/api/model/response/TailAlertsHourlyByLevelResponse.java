package com.n1netails.n1netails.api.model.response;

import java.util.List;

public class TailAlertsHourlyByLevelResponse {

    private List<String> labels;
    private List<TailDatasetResponse> datasets;

    public TailAlertsHourlyByLevelResponse(List<String> labels, List<TailDatasetResponse> datasets) {
        this.labels = labels;
        this.datasets = datasets;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<TailDatasetResponse> getDatasets() {
        return datasets;
    }

    public void setDatasets(List<TailDatasetResponse> datasets) {
        this.datasets = datasets;
    }
}
