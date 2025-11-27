package com.n1netails.n1netails.api.model.dto;

public class TailStatusCount {
    private String statusName;
    private long count;

    public TailStatusCount(String statusName, long count) {
        this.statusName = statusName;
        this.count = count;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
