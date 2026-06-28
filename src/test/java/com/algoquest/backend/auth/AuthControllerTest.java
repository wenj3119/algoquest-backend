package com.algoquest.backend.auth;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private String uniqueEmail;

    @BeforeEach
    void setUp() {
        uniqueEmail = "test-" + UUID.randomUUID() + "@example.com";
    }

    // ── Register ──────────────────────────────────────────────────────────────

    @Test
    void register_success() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("email", uniqueEmail, "password", "Pass1234", "displayName", "Alice")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.email").value(uniqueEmail))
                .andExpect(jsonPath("$.displayName").value("Alice"));
    }

    @Test
    void register_defaultsDisplayNameFromEmail() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("email", uniqueEmail, "password", "Pass1234")))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.displayName").value(uniqueEmail.split("@")[0]));
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        String payload = body("email", uniqueEmail, "password", "Pass1234");
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isConflict());
    }

    @Test
    void register_passwordTooShort_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("email", uniqueEmail, "password", "Ab1")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_passwordNoDigit_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("email", uniqueEmail, "password", "Password")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_passwordNoLetter_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("email", uniqueEmail, "password", "12345678")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_invalidEmail_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("email", "not-an-email", "password", "Pass1234")))
                .andExpect(status().isBadRequest());
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Test
    void login_success_returnsJwt() throws Exception {
        // register first
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("email", uniqueEmail, "password", "Pass1234")))
                .andExpect(status().isCreated());

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("email", uniqueEmail, "password", "Pass1234")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andReturn();

        String token = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("token").asText();
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void login_wrongPassword_returns401WithGenericMessage() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("email", uniqueEmail, "password", "Pass1234")))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("email", uniqueEmail, "password", "WrongPass9")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_emailNotFound_returns401WithSameMessageAsWrongPassword() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("email", "ghost-" + uniqueEmail, "password", "Pass1234")))
                .andExpect(status().isUnauthorized());
    }

    // ── /me ───────────────────────────────────────────────────────────────────

    @Test
    void me_validToken_returnsUserInfo() throws Exception {
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body("email", uniqueEmail, "password", "Pass1234", "displayName", "Bob")))
                .andExpect(status().isCreated())
                .andReturn();

        String token = objectMapper.readTree(registerResult.getResponse().getContentAsString())
                .get("token").asText();

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(uniqueEmail))
                .andExpect(jsonPath("$.displayName").value("Bob"));
    }

    @Test
    void me_invalidToken_returns401() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void me_noToken_returns401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized());
    }

    // ── Anonymous problem access still works ──────────────────────────────────

    @Test
    void problems_listWithNoToken_returns200() throws Exception {
        mockMvc.perform(get("/api/problems"))
                .andExpect(status().isOk());
    }

    @Test
    void problems_detailWithNoToken_returns200() throws Exception {
        mockMvc.perform(get("/api/problems/1"))
                .andExpect(status().isOk());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private String body(String... keysAndValues) throws Exception {
        Map<String, Object> map = new java.util.LinkedHashMap<>();
        for (int i = 0; i < keysAndValues.length - 1; i += 2) {
            if (keysAndValues[i + 1] != null) {
                map.put(keysAndValues[i], keysAndValues[i + 1]);
            }
        }
        return objectMapper.writeValueAsString(map);
    }
}
