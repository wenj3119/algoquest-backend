package com.algoquest.backend.controller;

import com.algoquest.backend.dto.GenerateHintRequest;
import com.algoquest.backend.dto.GenerateHintResponse;
import com.algoquest.backend.dto.ProblemDetailResponse;
import com.algoquest.backend.dto.ProblemSummaryResponse;
import com.algoquest.backend.dto.SubmitCodeRequest;
import com.algoquest.backend.dto.SubmitCodeResponse;
import com.algoquest.backend.service.HintService;
import com.algoquest.backend.service.ProblemService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/problems")
public class ProblemController {

    private final ProblemService problemService;
    private final HintService hintService;

    public ProblemController(ProblemService problemService, HintService hintService) {
        this.problemService = problemService;
        this.hintService = hintService;
    }

    @GetMapping
    public List<ProblemSummaryResponse> getProblems() {
        return problemService.getProblems();
    }

    @GetMapping("/{id}")
    public ProblemDetailResponse getProblemDetail(@PathVariable Long id) {
        return problemService.getProblemById(id);
    }

    @PostMapping("/{id}/submit")
    public SubmitCodeResponse submitCode(@PathVariable Long id, @RequestBody SubmitCodeRequest request) {
        return problemService.submitCode(id, request);
    }

    @PostMapping("/{id}/hints")
    public GenerateHintResponse generateHints(@PathVariable Long id, @RequestBody GenerateHintRequest request) {
        problemService.assertProblemExists(id);
        return hintService.generateHints(id, request);
    }
}
