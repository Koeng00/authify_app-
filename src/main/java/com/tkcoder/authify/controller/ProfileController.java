package com.tkcoder.authify.controller;

import com.tkcoder.authify.dto.ProfileRequest;
import com.tkcoder.authify.dto.ProfileResponse;
import com.tkcoder.authify.service.MailService;
import com.tkcoder.authify.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final MailService mailService;


    @PostMapping("/register")
    @Transactional
    public ResponseEntity<ProfileResponse> save(@Valid @RequestBody ProfileRequest request) {
        ProfileResponse response = profileService.createProfile(request);
        mailService.sentWelcomeEmail(response.email(), response.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(@CurrentSecurityContext(expression = "authentication?.name") String email){
        ProfileResponse response = profileService.getProfile(email);
        return ResponseEntity.ok(response);
    }

    // 1:28:59
}
