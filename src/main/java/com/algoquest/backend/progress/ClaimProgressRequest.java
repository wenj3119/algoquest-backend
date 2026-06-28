package com.algoquest.backend.progress;

import java.util.List;

public class ClaimProgressRequest {

    private List<ProgressItemDto> items;

    public List<ProgressItemDto> getItems() { return items; }
    public void setItems(List<ProgressItemDto> items) { this.items = items; }
}
