package util;

import java.security.SecureRandom;
import java.util.Base64;

public class PasswordHashingUtil {
    // PBKDF2 parameters
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    private static final int ITERATIONS = 65536;  // NIST recommendation
    private static final int KEY_LENGTH = 256;    // 256 bits
    private static final int SALT_LENGTH = 32;    // 32 bytes


    public static String hashPassword(String plainPassword) {
        try {
            LoggingUtil.debug(PasswordHashingUtil.class, "Hashing password for user authentication");

            // Generate random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Hash the password with salt using PBKDF2
            javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance(ALGORITHM);
            javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
                    plainPassword.toCharArray(),
                    salt,
                    ITERATIONS,
                    KEY_LENGTH
            );
            javax.crypto.SecretKey hash = factory.generateSecret(spec);
            byte[] hashedPassword = hash.getEncoded();

            // Encode salt and hash to Base64 for storage
            String saltBase64 = Base64.getEncoder().encodeToString(salt);
            String hashBase64 = Base64.getEncoder().encodeToString(hashedPassword);

            LoggingUtil.debug(PasswordHashingUtil.class, "Password hashed successfully");
            // Return in format: salt:hash
            return saltBase64 + ":" + hashBase64;

        } catch (Exception e) {
            LoggingUtil.error(PasswordHashingUtil.class, "Error while hashing password", e);
            throw new RuntimeException("Error hashing password", e);
        }
    }


    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        try {
            LoggingUtil.debug(PasswordHashingUtil.class, "Verifying password");

            // Split the stored hash into salt and hash
            String[] parts = hashedPassword.split(":");
            if (parts.length != 2) {
                LoggingUtil.warn(PasswordHashingUtil.class, "Invalid hash format - expected salt:hash format");
                return false;
            }

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] storedHash = Base64.getDecoder().decode(parts[1]);

            javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance(ALGORITHM);
            javax.crypto.spec.PBEKeySpec spec = new javax.crypto.spec.PBEKeySpec(
                    plainPassword.toCharArray(),
                    salt,
                    ITERATIONS,
                    KEY_LENGTH
            );
            javax.crypto.SecretKey hash = factory.generateSecret(spec);
            byte[] hashedPassword2 = hash.getEncoded();

            // Compare the hashes in a constant-time manner to prevent timing attacks
            boolean matches = constantTimeEquals(storedHash, hashedPassword2);

            if (matches) {
                LoggingUtil.debug(PasswordHashingUtil.class, "Password verification successful");
            } else {
                LoggingUtil.warn(PasswordHashingUtil.class, "Password verification failed - passwords do not match");
            }

            return matches;

        } catch (IllegalArgumentException e) {
            LoggingUtil.error(PasswordHashingUtil.class, "Invalid hash format provided for verification", e);
            return false;
        } catch (Exception e) {
            LoggingUtil.error(PasswordHashingUtil.class, "Error while verifying password", e);
            return false;
        }
    }


    private static boolean constantTimeEquals(byte[] array1, byte[] array2) {
        if (array1.length != array2.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < array1.length; i++) {
            result |= array1[i] ^ array2[i];
        }
        return result == 0;
    }


    public static void main(String[] args) {
        String password = "MySecurePassword123!";

        // Hash the password
        String hashedPassword = PasswordHashingUtil.hashPassword(password);
        System.out.println("Original password: " + password);
        System.out.println("Hashed password: " + hashedPassword);
        System.out.println();

        // Verify with correct password
        boolean isCorrect = PasswordHashingUtil.verifyPassword(password, hashedPassword);
        System.out.println("Verification with correct password: " + isCorrect);

        // Verify with incorrect password
        boolean isIncorrect = PasswordHashingUtil.verifyPassword("WrongPassword", hashedPassword);
        System.out.println("Verification with incorrect password: " + isIncorrect);
    }
}
