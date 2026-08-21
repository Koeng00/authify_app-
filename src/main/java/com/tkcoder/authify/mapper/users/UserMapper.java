package com.tkcoder.authify.mapper.users;

import com.tkcoder.authify.dto.ProfileRequest;
import com.tkcoder.authify.dto.ProfileResponse;
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
                .user_uuid(profile.getUserId())
                .email(profile.getEmail())
                .name(profile.getName())
                .is_account_verified(profile.getIsAccountVerified())
                .build();
    }

}
