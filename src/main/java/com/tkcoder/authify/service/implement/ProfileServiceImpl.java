package com.tkcoder.authify.service.implement;

import com.tkcoder.authify.dto.ProfileRequest;
import com.tkcoder.authify.dto.ProfileResponse;
import com.tkcoder.authify.entity.OtpData;
import com.tkcoder.authify.entity.UserEntity;
import com.tkcoder.authify.mapper.users.UserMapper;
import com.tkcoder.authify.repository.UserRepository;
import com.tkcoder.authify.service.MailService;
import com.tkcoder.authify.service.ProfileService;
import com.tkcoder.authify.util.OtpUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final OtpUtil otpUtil;

    @Override
    public ProfileResponse createProfile(ProfileRequest request) {
        if (!userRepository.existsByEmail(request.email())) {
            UserEntity savedUser = userRepository.save(UserMapper.toEntity(request, passwordEncoder.encode(request.password())));
            return UserMapper.toResponse(savedUser);
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
    }

    @Override
    public ProfileResponse getProfile(String email) {

        UserEntity profile = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
        return UserMapper.toResponse(profile);
    }

    @Override
    public void sendResetOtp(String email) {
        UserEntity existingEntity = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: "+ email));

        OtpData otpData = otpUtil.generateOtp(15 * 60 * 1000L);

        // update the profile/user
        existingEntity.setResetOtp(otpData.otp());
        existingEntity.setResetOtpExpireAt(otpData.expiryTime()); // 15 minutes

        // save into the database
        userRepository.save(existingEntity);

        try {
            // send otp to email
            mailService.sendResetOtpEmail(existingEntity.getEmail(), otpData.otp());
        } catch (Exception e){
            throw new RuntimeException("Unable to send email: ", e);
        }
    }


    @Override
    public void resetPassword(String email, String otp, String newPassword) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: "+ email));

        if (existingUser.getResetOtp() == null || !existingUser.getResetOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP");
        }

        if (existingUser.getResetOtpExpireAt() < System.currentTimeMillis()){
            throw new RuntimeException("OTP Expired");
        }

        existingUser.setPassword(passwordEncoder.encode(newPassword));
        existingUser.setResetOtp(null);
        existingUser.setResetOtpExpireAt(0L);

        userRepository.save(existingUser);
    }

    @Override
    public void sentOtp(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (user.getIsAccountVerified() != null  && user.getIsAccountVerified()){
            return;
        }

        OtpData otpData = otpUtil.generateOtp(1000 * 60 * 60 * 24L); // 24 hours
        user.setVerifyOtp(otpData.otp());
        user.setVerifyOtpExpireAt(otpData.expiryTime());
        userRepository.save(user);

        try{
            mailService.sendOtpEmail(user.getEmail(), otpData.otp());
        } catch (Exception e){
            throw new RuntimeException("Unable to send email: ", e);
        }
    }

    @Override
    public void verifyOtp(String email, String otp) {
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: "+ email));

        if (existingUser.getVerifyOtp() == null || !existingUser.getVerifyOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP");
        }

        if (existingUser.getVerifyOtpExpireAt() < System.currentTimeMillis()){
            throw new RuntimeException("OTP Expired");
        }

        existingUser.setIsAccountVerified(true);
        existingUser.setVerifyOtp(null);
        existingUser.setVerifyOtpExpireAt(0L);

        userRepository.save(existingUser);
    }

}
