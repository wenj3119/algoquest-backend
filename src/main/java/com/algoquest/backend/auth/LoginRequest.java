package com.algoquest.backend.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "邮箱不能为空")
    String email,

    @NotBlank(message = "密码不能为空")
    String password
) {}
