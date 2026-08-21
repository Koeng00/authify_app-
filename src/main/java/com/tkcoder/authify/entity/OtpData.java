package com.tkcoder.authify.entity;

public record OtpData(
        String otp,
        Long expiryTime
) {
}
