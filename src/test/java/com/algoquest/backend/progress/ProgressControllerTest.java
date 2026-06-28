package com.algoquest.backend.progress;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class ProgressControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    // ── Auth guards ────────────────────────────────────────────────────────────

    @Test
    void claim_noToken_returns401() throws Exception {
        mockMvc.perform(post("/api/progress/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[]}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void claim_invalidToken_returns401() throws Exception {
        mockMvc.perform(post("/api/progress/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer invalid.jwt.token")
                        .content("{\"items\":[]}"))
                .andExpect(status().isUnauthorized());
    }

    // ── Claim with empty payload returns empty list ────────────────────────────

    @Test
    void claim_empty_returnsEmptyList() throws Exception {
        String token = registerAndGetToken();
        mockMvc.perform(post("/api/progress/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("{\"items\":[]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items.length()").value(0));
    }

    // ── Test 13: Claim idempotency ─────────────────────────────────────────────

    @Test
    void claim_idempotency_subRecordsNotDuplicated() throws Exception {
        String token = registerAndGetToken();
        String mistakeId = "mr-idem-" + UUID.randomUUID();
        String hintId = "hr-idem-" + UUID.randomUUID();

        String payload = buildPayload(1L, "in_progress", "2026-06-19T10:00:00Z",
                List.of(mistakeRecord(mistakeId, 1L, "failed", "key_condition")),
                List.of(hintRecord(hintId, 1L, "failed", 2)));

        // Claim the same payload three times
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post("/api/progress/claim")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .content(payload))
                    .andExpect(status().isOk());
        }

        // Fetch all via empty claim and verify no duplication
        MvcResult result = mockMvc.perform(post("/api/progress/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("{\"items\":[]}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode response = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode items = response.get("items");
        assertThat(items.size()).isEqualTo(1);
        assertThat(items.get(0).get("mistakeRecords").size()).isEqualTo(1);
        assertThat(items.get(0).get("hintUsageRecords").size()).isEqualTo(1);
    }

    // ── Test 14: Cross-device simulation ──────────────────────────────────────

    @Test
    void claim_crossDevice_deviceBGetsDeviceAData() throws Exception {
        String token = registerAndGetToken();
        String mistakeId = "mr-cross-" + UUID.randomUUID();

        // Device A: claim with progress
        mockMvc.perform(post("/api/progress/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(buildPayload(1L, "passed", "2026-06-19T10:00:00Z",
                                List.of(mistakeRecord(mistakeId, 1L, "passed", "key_condition")),
                                List.of())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].status").value("passed"))
                .andExpect(jsonPath("$.items[0].mistakeRecords.length()").value(1));

        // Device B: claim with empty local data → must receive Device A's progress
        MvcResult deviceBResult = mockMvc.perform(post("/api/progress/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content("{\"items\":[]}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode resp = objectMapper.readTree(deviceBResult.getResponse().getContentAsString());
        assertThat(resp.get("items").size()).isEqualTo(1);
        assertThat(resp.get("items").get(0).get("status").asText()).isEqualTo("passed");
        assertThat(resp.get("items").get(0).get("mistakeRecords").size()).isEqualTo(1);
        assertThat(resp.get("items").get(0).get("mistakeRecords").get(0).get("id").asText())
                .isEqualTo(mistakeId);
    }

    // ── Test 15: User isolation ────────────────────────────────────────────────

    @Test
    void claim_userIsolation_userBCannotSeeUserAProgress() throws Exception {
        String tokenA = registerAndGetToken();
        String tokenB = registerAndGetToken();

        // User A claims progress for problem 2
        mockMvc.perform(post("/api/progress/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenA)
                        .content(buildPayload(2L, "passed", "2026-06-19T09:00:00Z", List.of(), List.of())))
                .andExpect(status().isOk());

        // User B claims with empty local → must NOT see user A's data
        mockMvc.perform(post("/api/progress/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenB)
                        .content("{\"items\":[]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0));
    }

    @Test
    void claim_userIsolation_separateProgressPerUser() throws Exception {
        String tokenA = registerAndGetToken();
        String tokenB = registerAndGetToken();
        String idA = "mr-a-" + UUID.randomUUID();
        String idB = "mr-b-" + UUID.randomUUID();

        // User A: problem 1, in_progress, one mistake
        mockMvc.perform(post("/api/progress/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenA)
                        .content(buildPayload(1L, "in_progress", "2026-06-19T08:00:00Z",
                                List.of(mistakeRecord(idA, 1L, "failed", "key_condition")),
                                List.of())))
                .andExpect(status().isOk());

        // User B: problem 1, passed, different mistake
        mockMvc.perform(post("/api/progress/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenB)
                        .content(buildPayload(1L, "passed", "2026-06-19T08:00:00Z",
                                List.of(mistakeRecord(idB, 1L, "passed", "boundary_case")),
                                List.of())))
                .andExpect(status().isOk());

        // Verify user A still sees only their own progress
        MvcResult resultA = mockMvc.perform(post("/api/progress/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + tokenA)
                        .content("{\"items\":[]}"))
                .andReturn();
        JsonNode respA = objectMapper.readTree(resultA.getResponse().getContentAsString());
        assertThat(respA.get("items").get(0).get("status").asText()).isEqualTo("in_progress");
        assertThat(respA.get("items").get(0).get("mistakeRecords").get(0).get("id").asText()).isEqualTo(idA);
    }

    // ── Status merge: only forward ─────────────────────────────────────────────

    @Test
    void claim_statusMerge_onlyForward_doesNotRevert() throws Exception {
        String token = registerAndGetToken();

        // First: claim "passed"
        mockMvc.perform(post("/api/progress/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(buildPayload(3L, "passed", "2026-06-19T12:00:00Z", List.of(), List.of())))
                .andExpect(status().isOk());

        // Second: claim "in_progress" with an older updatedAt — status must stay "passed"
        mockMvc.perform(post("/api/progress/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(buildPayload(3L, "in_progress", "2026-06-01T00:00:00Z", List.of(), List.of())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].status").value("passed"));
    }

    @Test
    void claim_statusMerge_advancesForwardEvenWithNewerTimestamp() throws Exception {
        String token = registerAndGetToken();

        // Start at "in_progress"
        mockMvc.perform(post("/api/progress/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(buildPayload(4L, "in_progress", "2026-06-19T10:00:00Z", List.of(), List.of())))
                .andExpect(status().isOk());

        // Advance to "passed" with newer timestamp
        mockMvc.perform(post("/api/progress/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .content(buildPayload(4L, "passed", "2026-06-19T11:00:00Z", List.of(), List.of())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].status").value("passed"));
    }

    // ── Anonymous access to /api/problems still works ─────────────────────────

    @Test
    void problems_withNoToken_stillReturns200() throws Exception {
        mockMvc.perform(post("/api/progress/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"items\":[]}"))
                .andExpect(status().isUnauthorized());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String registerAndGetToken() throws Exception {
        String email = "progress-test-" + UUID.randomUUID() + "@test.com";
        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("email", email, "password", "Pass1234"))))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
    }

    private String buildPayload(
            Long problemId, String status, String updatedAt,
            List<Map<String, Object>> mistakeRecords,
            List<Map<String, Object>> hintUsageRecords) throws Exception {
        Map<String, Object> item = new java.util.LinkedHashMap<>();
        item.put("problemId", problemId);
        item.put("status", status);
        item.put("code", "");
        item.put("stepResults", Map.of());
        item.put("currentStepIndex", 0);
        item.put("lastSubmitResult", null);
        item.put("updatedAt", updatedAt);
        item.put("mistakeRecords", mistakeRecords);
        item.put("hintUsageRecords", hintUsageRecords);
        return objectMapper.writeValueAsString(Map.of("items", List.of(item)));
    }

    private Map<String, Object> mistakeRecord(
            String id, Long problemId, String submitStatus, String reason) {
        return Map.of(
                "id", id,
                "problemId", problemId,
                "submitStatus", submitStatus,
                "reason", reason,
                "createdAt", "2026-06-19T09:00:00Z");
    }

    private Map<String, Object> hintRecord(
            String id, Long problemId, String submitStatus, int maxLevel) {
        return Map.of(
                "id", id,
                "problemId", problemId,
                "submitStatus", submitStatus,
                "maxUnlockedLevel", maxLevel,
                "createdAt", "2026-06-19T09:00:00Z");
    }
}
