package com.tkcoder.authify.controller;

import com.tkcoder.authify.dto.users.ProfileRequest;
import com.tkcoder.authify.dto.users.ProfileResponse;
import com.tkcoder.authify.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<ProfileResponse> save(@Valid @RequestBody ProfileRequest request)
    {
        ProfileResponse response = profileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 1:28:59
}
