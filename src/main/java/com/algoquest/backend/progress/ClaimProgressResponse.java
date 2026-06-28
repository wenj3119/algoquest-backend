package com.algoquest.backend.progress;

import java.util.List;

public class ClaimProgressResponse {

    private final List<ProgressItemDto> items;

    public ClaimProgressResponse(List<ProgressItemDto> items) {
        this.items = items;
    }

    public List<ProgressItemDto> getItems() { return items; }
}
