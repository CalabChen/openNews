package com.example.opennews.model.hot;

import java.util.List;

public class HotNewsResponse {
    private boolean success;
    private List<HotNewsItem> data;

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<HotNewsItem> getData() {
        return data;
    }

    public void setData(List<HotNewsItem> data) {
        this.data = data;
    }
}