package com.tkcoder.authify.mapper.users;

import com.tkcoder.authify.dto.users.ProfileRequest;
import com.tkcoder.authify.dto.users.ProfileResponse;
import com.tkcoder.authify.entity.UserEntity;

import java.util.UUID;

public class UserMapper {

    public static UserEntity toEntity(ProfileRequest request, String password)
    {
        return UserEntity.builder()
                .email(request.email())
                .userId(UUID.randomUUID().toString())
                .name(request.name())
                .password(password)
                .isAccountVerified(false)
                .resetOtpExpireAt(0L)
                .verifyOtp(null)
                .verifyOtpExpireAt(0L)
                .resetOtp(null)
                .build();
    }

    public static ProfileResponse toResponse(UserEntity profile)
    {
        return ProfileResponse.builder()
                .userId(profile.getUserId())
                .email(profile.getEmail())
                .name(profile.getName())
                .isAccountVerified(profile.getIsAccountVerified())
                .build();
    }

}
