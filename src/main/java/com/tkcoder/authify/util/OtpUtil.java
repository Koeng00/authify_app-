package com.tkcoder.authify.util;

import com.tkcoder.authify.entity.OtpData;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class OtpUtil {

    private static final int OTP_MIN = 100000;
    private static final int OTP_MAX = 1000000;

    public OtpData generateOtp(Long OTP_EXPIRATION) {

        String otp = String.valueOf(
                ThreadLocalRandom.current()
                        .nextInt(OTP_MIN, OTP_MAX)
        );

        long expiryTime =
                System.currentTimeMillis()
                        + OTP_EXPIRATION;

        return new OtpData(otp, expiryTime);
    }
}
