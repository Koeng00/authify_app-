package com.tkcoder.authify.dto;

import lombok.Builder;

@Builder
public record AuthResponse (
    String email,
    String access_token
){
}
