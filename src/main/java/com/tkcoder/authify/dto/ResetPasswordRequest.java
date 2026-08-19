package com.tkcoder.authify.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(

        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "OPT is required")
        String otp,

        @Size(min = 6, message = "New password must be at least 6 characters")
        @NotBlank(message = "New password is required")
        String newPassword
) {
}
