package com.tkcoder.authify.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileRequest (
        @NotBlank(message = "Name should not be empty")
        String name,

        @Email(message = "Enter invalid email address")
        @NotBlank(message = "Email should not be empty")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
) {
}
