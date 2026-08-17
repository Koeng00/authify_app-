package com.tkcoder.authify.dto.auths;

import lombok.Builder;

@Builder
public record AuthResponse (
    String email,
    String token
){
}
