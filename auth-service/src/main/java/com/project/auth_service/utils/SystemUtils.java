package com.project.auth_service.utils;

import java.security.SecureRandom;

public class SystemUtils {

    public static String generateOtp() {
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}
