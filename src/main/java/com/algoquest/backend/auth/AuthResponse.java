package com.algoquest.backend.auth;

public record AuthResponse(
    String token,
    long id,
    String email,
    String displayName
) {}
