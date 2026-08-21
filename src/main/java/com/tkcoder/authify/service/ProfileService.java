package com.tkcoder.authify.service;

import com.tkcoder.authify.dto.ProfileRequest;
import com.tkcoder.authify.dto.ProfileResponse;

import java.util.List;
import java.util.Map;

public interface ProfileService {
    ProfileResponse createProfile(ProfileRequest request);
    ProfileResponse getProfile(String email);

    void sendResetOtp(String email);
    void resetPassword(String email, String otp, String newPassword);
    void sentOtp(String email);
    void verifyOtp(String email, String otp);
}
