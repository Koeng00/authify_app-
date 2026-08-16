package com.tkcoder.authify.dto.users;

import lombok.Builder;

@Builder
public record ProfileResponse(
        String userId,
        String name,
        String email,
        Boolean isAccountVerified
) {
}
