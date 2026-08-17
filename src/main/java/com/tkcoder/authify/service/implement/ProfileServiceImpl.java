package com.tkcoder.authify.service.implement;

import com.tkcoder.authify.dto.users.ProfileRequest;
import com.tkcoder.authify.dto.users.ProfileResponse;
import com.tkcoder.authify.entity.UserEntity;
import com.tkcoder.authify.mapper.users.UserMapper;
import com.tkcoder.authify.repository.UserRepository;
import com.tkcoder.authify.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        if (!userRepository.existsByEmail(request.email())) {
            UserEntity savedUser = userRepository.save(UserMapper.toEntity(request, passwordEncoder.encode(request.password())));
            return UserMapper.toResponse(savedUser);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }

    // 37:02
}
