package com.example.depu.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class SecurityUtil {
    private static final String ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;
    
    /**
     * 生成随机盐值
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
    
    /**
     * 对密码进行加密
     */
    public static String encryptPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
            digest.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("密码加密失败", e);
        }
    }
    
    /**
     * 验证密码是否正确
     */
    public static boolean verifyPassword(String password, String salt, String hashedPassword) {
        String encryptedPassword = encryptPassword(password, salt);
        return encryptedPassword.equals(hashedPassword);
    }
    
    /**
     * 生成完整的密码存储字符串（包含盐值和哈希密码）
     */
    public static String generatePasswordHash(String password) {
        String salt = generateSalt();
        String hashedPassword = encryptPassword(password, salt);
        return salt + ":" + hashedPassword;
    }
    
    /**
     * 从密码存储字符串中提取盐值
     */
    public static String extractSalt(String passwordHash) {
        return passwordHash.split(":")[0];
    }
    
    /**
     * 从密码存储字符串中提取哈希密码
     */
    public static String extractHashedPassword(String passwordHash) {
        return passwordHash.split(":")[1];
    }
}
