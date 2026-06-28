package com.algoquest.backend.progress;

import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    private final ProgressService progressService;

    public ProgressController(ProgressService progressService) {
        this.progressService = progressService;
    }

    /** Claim local progress: merge local items into DB and return the full merged set. */
    @PostMapping("/claim")
    public ClaimProgressResponse claim(
            Authentication authentication,
            @RequestBody ClaimProgressRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        List<ProgressItemDto> merged = progressService.claim(
                userId,
                request.getItems() != null ? request.getItems() : List.of());
        return new ClaimProgressResponse(merged);
    }
}
