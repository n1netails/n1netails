package com.n1netails.n1netails.api.model.response;

import java.util.List;

public class TailResolutionStatusResponse {

    private List<String> labels;
    private List<Integer> data;

    public TailResolutionStatusResponse(List<String> labels, List<Integer> data) {
        this.labels = labels;
        this.data = data;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Integer> getData() {
        return data;
    }

    public void setData(List<Integer> data) {
        this.data = data;
    }
}
