package com.algoquest.backend.service;

import com.algoquest.backend.db.entity.JudgeSpecEntity;
import com.algoquest.backend.db.entity.ProblemEntity;
import com.algoquest.backend.db.entity.ProblemExampleEntity;
import com.algoquest.backend.db.entity.ProblemStepEntity;
import com.algoquest.backend.db.entity.ProblemStepOptionEntity;
import com.algoquest.backend.db.entity.TestCaseEntity;
import com.algoquest.backend.db.repository.JudgeSpecRepository;
import com.algoquest.backend.db.repository.ProblemExampleRepository;
import com.algoquest.backend.db.repository.ProblemRepository;
import com.algoquest.backend.db.repository.ProblemStepOptionRepository;
import com.algoquest.backend.db.repository.ProblemStepRepository;
import com.algoquest.backend.db.repository.TestCaseRepository;
import com.algoquest.backend.dto.ExampleResponse;
import com.algoquest.backend.dto.ProblemDetailResponse;
import com.algoquest.backend.dto.ProblemStepResponse;
import com.algoquest.backend.dto.ProblemSummaryResponse;
import com.algoquest.backend.dto.StepOptionResponse;
import com.algoquest.backend.dto.SubmitCaseResponse;
import com.algoquest.backend.dto.SubmitCodeRequest;
import com.algoquest.backend.dto.SubmitCodeResponse;
import com.algoquest.backend.judge.JudgeCaseResult;
import com.algoquest.backend.judge.JudgeExecutor;
import com.algoquest.backend.judge.JudgeResult;
import com.algoquest.backend.judge.spec.JudgeSpecConverter;
import com.algoquest.backend.judge.spec.JudgeSpecData;
import com.algoquest.backend.judge.spec.TestCaseData;
import com.algoquest.backend.model.Difficulty;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProblemService {

    private static final int MAX_CODE_LENGTH = 20_000;

    private final ProblemRepository problemRepository;
    private final ProblemExampleRepository problemExampleRepository;
    private final ProblemStepRepository problemStepRepository;
    private final ProblemStepOptionRepository problemStepOptionRepository;
    private final JudgeExecutor judgeExecutor;
    private final JudgeSpecRepository judgeSpecRepository;
    private final TestCaseRepository testCaseRepository;

    public ProblemService(ProblemRepository problemRepository,
                          ProblemExampleRepository problemExampleRepository,
                          ProblemStepRepository problemStepRepository,
                          ProblemStepOptionRepository problemStepOptionRepository,
                          JudgeExecutor judgeExecutor,
                          JudgeSpecRepository judgeSpecRepository,
                          TestCaseRepository testCaseRepository) {
        this.problemRepository = problemRepository;
        this.problemExampleRepository = problemExampleRepository;
        this.problemStepRepository = problemStepRepository;
        this.problemStepOptionRepository = problemStepOptionRepository;
        this.judgeExecutor = judgeExecutor;
        this.judgeSpecRepository = judgeSpecRepository;
        this.testCaseRepository = testCaseRepository;
    }

    public List<ProblemSummaryResponse> getProblems() {
        return problemRepository.findAllByOrderBySortOrderAscIdAsc().stream()
                .map(p -> new ProblemSummaryResponse(
                        p.getId(),
                        p.getTitle(),
                        Difficulty.valueOf(p.getDifficulty()),
                        p.getCategory(),
                        "not_started"
                ))
                .toList();
    }

    public ProblemDetailResponse getProblemById(Long id) {
        ProblemEntity problem = requireProblem(id);

        List<ProblemExampleEntity> examples =
                problemExampleRepository.findByProblemIdOrderBySortOrder(id);
        List<ProblemStepEntity> steps =
                problemStepRepository.findByProblemIdOrderBySortOrder(id);

        List<Long> stepIds = steps.stream().map(ProblemStepEntity::getId).toList();
        Map<Long, List<ProblemStepOptionEntity>> optionsByStep =
                problemStepOptionRepository.findByStepIdInOrderBySortOrder(stepIds).stream()
                        .collect(Collectors.groupingBy(ProblemStepOptionEntity::getStepId));

        return new ProblemDetailResponse(
                problem.getId(),
                problem.getTitle(),
                Difficulty.valueOf(problem.getDifficulty()),
                problem.getCategory(),
                problem.getDescription(),
                examples.stream().map(this::toExampleResponse).toList(),
                problem.getStarterCode(),
                steps.stream().map(step -> toProblemStepResponse(step, optionsByStep)).toList()
        );
    }

    public SubmitCodeResponse submitCode(Long id, SubmitCodeRequest request) {
        requireProblem(id);

        if (request == null) {
            return new SubmitCodeResponse("failed", 0, 0, "请求体不能为空。", List.of());
        }

        String language = request.language();
        if (language == null || !"java".equalsIgnoreCase(language.trim())) {
            return new SubmitCodeResponse("failed", 0, 0, "当前仅支持 Java 提交。", List.of());
        }

        String code = request.code();
        if (code == null || code.isBlank()) {
            return new SubmitCodeResponse("failed", 0, 0, "代码不能为空。", List.of());
        }

        if (code.length() > MAX_CODE_LENGTH) {
            return new SubmitCodeResponse("failed", 0, 0, "代码长度不能超过 20000 个字符。", List.of());
        }

        if (!code.contains("public class Solution")) {
            return new SubmitCodeResponse("compile_error", 0, 0, "提交代码必须包含 public class Solution。", List.of());
        }

        JudgeResult judgeResult = routeJudge(id, code);
        return new SubmitCodeResponse(
                judgeResult.status(),
                judgeResult.passedCount(),
                judgeResult.totalCount(),
                judgeResult.message(),
                judgeResult.cases().stream().map(this::toSubmitCaseResponse).toList()
        );
    }

    public void assertProblemExists(Long id) {
        if (!problemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found: " + id);
        }
    }

    private JudgeResult routeJudge(Long id, String code) {
        JudgeSpecEntity specEntity = judgeSpecRepository.findByProblemId(id)
                .orElseThrow(() -> new IllegalStateException("No judge spec found for problem " + id));
        ProblemEntity problem = problemRepository.findById(id).orElseThrow();
        List<TestCaseEntity> caseEntities = testCaseRepository.findByProblemIdOrderBySortOrder(id);
        JudgeSpecData spec = JudgeSpecConverter.toSpecData(specEntity, problem.getSource());
        List<TestCaseData> cases = caseEntities.stream()
                .map(JudgeSpecConverter::toTestCaseData)
                .toList();
        return judgeExecutor.execute(spec, cases, code);
    }

    private ProblemEntity requireProblem(Long id) {
        return problemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Problem not found: " + id));
    }

    private ExampleResponse toExampleResponse(ProblemExampleEntity e) {
        return new ExampleResponse(e.getInput(), e.getOutput(), e.getExplanation());
    }

    private ProblemStepResponse toProblemStepResponse(ProblemStepEntity step,
                                                       Map<Long, List<ProblemStepOptionEntity>> optionsByStep) {
        List<StepOptionResponse> options = optionsByStep
                .getOrDefault(step.getId(), List.of())
                .stream()
                .map(o -> new StepOptionResponse(o.getLabel(), o.getContent()))
                .toList();
        return new ProblemStepResponse(
                step.getStepKey(),
                step.getTitle(),
                step.getContent(),
                step.getType(),
                options,
                step.getAnswer(),
                step.getExplanation()
        );
    }

    private SubmitCaseResponse toSubmitCaseResponse(JudgeCaseResult caseResult) {
        return new SubmitCaseResponse(caseResult.input(), caseResult.expected(), caseResult.actual(), caseResult.passed());
    }
}
