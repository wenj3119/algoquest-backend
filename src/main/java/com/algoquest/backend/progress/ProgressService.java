package com.algoquest.backend.progress;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProgressService {

    private static final Map<String, Integer> STATUS_PRIORITY = Map.of(
            "not_started", 0,
            "in_progress", 1,
            "steps_completed", 2,
            "passed", 3
    );

    private final UserProblemProgressRepository progressRepo;
    private final MistakeRecordRepository mistakeRepo;
    private final HintUsageRecordRepository hintRepo;
    private final ObjectMapper objectMapper;

    public ProgressService(
            UserProblemProgressRepository progressRepo,
            MistakeRecordRepository mistakeRepo,
            HintUsageRecordRepository hintRepo,
            ObjectMapper objectMapper) {
        this.progressRepo = progressRepo;
        this.mistakeRepo = mistakeRepo;
        this.hintRepo = hintRepo;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public List<ProgressItemDto> claim(Long userId, List<ProgressItemDto> localItems) {
        if (localItems != null) {
            for (ProgressItemDto local : localItems) {
                if (local.getProblemId() != null) {
                    mergeOne(userId, local);
                }
            }
        }
        return getAllForUser(userId);
    }

    private void mergeOne(Long userId, ProgressItemDto local) {
        UserProblemProgressId key = new UserProblemProgressId(userId, local.getProblemId());
        Optional<UserProblemProgressEntity> existingOpt = progressRepo.findById(key);

        OffsetDateTime localUpdatedAt = parseTimestamp(local.getUpdatedAt());

        UserProblemProgressEntity entity;
        if (existingOpt.isEmpty()) {
            entity = new UserProblemProgressEntity();
            entity.setUserId(userId);
            entity.setProblemId(local.getProblemId());
            entity.setStatus(coalesceStatus(local.getStatus()));
            entity.setCode(local.getCode() != null ? local.getCode() : "");
            entity.setStepResults(nodeToString(local.getStepResults(), "{}"));
            entity.setCurrentStepIndex(local.getCurrentStepIndex() != null ? local.getCurrentStepIndex() : 0);
            entity.setLastSubmitResult(nodeToString(local.getLastSubmitResult(), null));
            entity.setUpdatedAt(localUpdatedAt != null ? localUpdatedAt : OffsetDateTime.now(ZoneOffset.UTC));
        } else {
            entity = existingOpt.get();
            entity.setStatus(mergeStatus(entity.getStatus(), coalesceStatus(local.getStatus())));
            if (localUpdatedAt != null && localUpdatedAt.isAfter(entity.getUpdatedAt())) {
                entity.setCode(local.getCode() != null ? local.getCode() : "");
                entity.setStepResults(nodeToString(local.getStepResults(), "{}"));
                entity.setCurrentStepIndex(local.getCurrentStepIndex() != null ? local.getCurrentStepIndex() : 0);
                entity.setLastSubmitResult(nodeToString(local.getLastSubmitResult(), null));
                entity.setUpdatedAt(localUpdatedAt);
            }
        }
        progressRepo.save(entity);

        // Sub-records: insert-or-ignore by primary key
        if (local.getMistakeRecords() != null) {
            for (MistakeRecordDto dto : local.getMistakeRecords()) {
                if (dto.getId() != null && !mistakeRepo.existsById(dto.getId())) {
                    mistakeRepo.save(toMistakeEntity(userId, local.getProblemId(), dto));
                }
            }
        }
        if (local.getHintUsageRecords() != null) {
            for (HintUsageRecordDto dto : local.getHintUsageRecords()) {
                if (dto.getId() != null && !hintRepo.existsById(dto.getId())) {
                    hintRepo.save(toHintEntity(userId, local.getProblemId(), dto));
                }
            }
        }
    }

    public List<ProgressItemDto> getAllForUser(Long userId) {
        List<UserProblemProgressEntity> progressList = progressRepo.findAllByUserId(userId);
        if (progressList.isEmpty()) return List.of();

        List<MistakeRecordEntity> allMistakes = mistakeRepo.findAllByUserId(userId);
        List<HintUsageRecordEntity> allHints = hintRepo.findAllByUserId(userId);

        Map<Long, List<MistakeRecordEntity>> mistakesByProblem = allMistakes.stream()
                .collect(Collectors.groupingBy(MistakeRecordEntity::getProblemId));
        Map<Long, List<HintUsageRecordEntity>> hintsByProblem = allHints.stream()
                .collect(Collectors.groupingBy(HintUsageRecordEntity::getProblemId));

        return progressList.stream()
                .map(p -> toDto(
                        p,
                        mistakesByProblem.getOrDefault(p.getProblemId(), List.of()),
                        hintsByProblem.getOrDefault(p.getProblemId(), List.of())))
                .collect(Collectors.toList());
    }

    private ProgressItemDto toDto(
            UserProblemProgressEntity p,
            List<MistakeRecordEntity> mistakes,
            List<HintUsageRecordEntity> hints) {
        ProgressItemDto dto = new ProgressItemDto();
        dto.setProblemId(p.getProblemId());
        dto.setStatus(p.getStatus());
        dto.setCode(p.getCode());
        dto.setStepResults(toJsonNode(p.getStepResults()));
        dto.setCurrentStepIndex(p.getCurrentStepIndex());
        dto.setLastSubmitResult(toJsonNode(p.getLastSubmitResult()));
        dto.setUpdatedAt(p.getUpdatedAt().toInstant().toString());
        dto.setMistakeRecords(mistakes.stream().map(this::toMistakeDto).collect(Collectors.toList()));
        dto.setHintUsageRecords(hints.stream().map(this::toHintDto).collect(Collectors.toList()));
        return dto;
    }

    private MistakeRecordDto toMistakeDto(MistakeRecordEntity entity) {
        MistakeRecordDto dto = new MistakeRecordDto();
        dto.setId(entity.getId());
        dto.setProblemId(entity.getProblemId());
        dto.setSubmitStatus(entity.getSubmitStatus());
        dto.setReason(entity.getReason());
        dto.setNote(entity.getNote());
        dto.setCreatedAt(entity.getCreatedAt().toInstant().toString());
        return dto;
    }

    private HintUsageRecordDto toHintDto(HintUsageRecordEntity entity) {
        HintUsageRecordDto dto = new HintUsageRecordDto();
        dto.setId(entity.getId());
        dto.setProblemId(entity.getProblemId());
        dto.setSubmitStatus(entity.getSubmitStatus());
        dto.setMistakeReason(entity.getMistakeReason());
        dto.setMaxUnlockedLevel(entity.getMaxUnlockedLevel());
        dto.setCreatedAt(entity.getCreatedAt().toInstant().toString());
        return dto;
    }

    private MistakeRecordEntity toMistakeEntity(Long userId, Long problemId, MistakeRecordDto dto) {
        MistakeRecordEntity entity = new MistakeRecordEntity();
        entity.setId(dto.getId());
        entity.setUserId(userId);
        entity.setProblemId(problemId);
        entity.setSubmitStatus(dto.getSubmitStatus() != null ? dto.getSubmitStatus() : "");
        entity.setReason(dto.getReason() != null ? dto.getReason() : "unknown");
        entity.setNote(dto.getNote());
        OffsetDateTime createdAt = parseTimestamp(dto.getCreatedAt());
        entity.setCreatedAt(createdAt != null ? createdAt : OffsetDateTime.now(ZoneOffset.UTC));
        return entity;
    }

    private HintUsageRecordEntity toHintEntity(Long userId, Long problemId, HintUsageRecordDto dto) {
        HintUsageRecordEntity entity = new HintUsageRecordEntity();
        entity.setId(dto.getId());
        entity.setUserId(userId);
        entity.setProblemId(problemId);
        entity.setSubmitStatus(dto.getSubmitStatus() != null ? dto.getSubmitStatus() : "");
        entity.setMistakeReason(dto.getMistakeReason());
        entity.setMaxUnlockedLevel(dto.getMaxUnlockedLevel() != null ? dto.getMaxUnlockedLevel() : 0);
        OffsetDateTime createdAt = parseTimestamp(dto.getCreatedAt());
        entity.setCreatedAt(createdAt != null ? createdAt : OffsetDateTime.now(ZoneOffset.UTC));
        return entity;
    }

    private String mergeStatus(String existing, String incoming) {
        int existingPriority = STATUS_PRIORITY.getOrDefault(existing, 0);
        int incomingPriority = STATUS_PRIORITY.getOrDefault(incoming, 0);
        return incomingPriority > existingPriority ? incoming : existing;
    }

    private String coalesceStatus(String status) {
        return STATUS_PRIORITY.containsKey(status) ? status : "not_started";
    }

    private OffsetDateTime parseTimestamp(String isoString) {
        if (isoString == null || isoString.isBlank()) return null;
        try {
            return OffsetDateTime.ofInstant(Instant.parse(isoString), ZoneOffset.UTC);
        } catch (Exception e) {
            return null;
        }
    }

    private String nodeToString(JsonNode node, String defaultValue) {
        if (node == null || node.isNull() || node.isMissingNode()) return defaultValue;
        return node.toString();
    }

    private JsonNode toJsonNode(String json) {
        if (json == null || json.isBlank()) return null;
        try {
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
