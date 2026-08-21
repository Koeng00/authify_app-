package com.tkcoder.authify.dto;

import lombok.Builder;

@Builder
public record ProfileResponse(
        String user_uuid,
        String name,
        String email,
        Boolean is_account_verified
) {
}
