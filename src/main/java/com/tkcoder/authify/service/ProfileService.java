package com.tkcoder.authify.service;

import com.tkcoder.authify.dto.users.ProfileRequest;
import com.tkcoder.authify.dto.users.ProfileResponse;

public interface ProfileService {
    ProfileResponse createProfile(ProfileRequest request);
}
