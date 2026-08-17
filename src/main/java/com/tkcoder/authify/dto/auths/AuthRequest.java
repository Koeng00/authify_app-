package com.tkcoder.authify.dto.auths;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record AuthRequest(
        @NotBlank(message = "Email is required")
        String email,
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
) {
}
