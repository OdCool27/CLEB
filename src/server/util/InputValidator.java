package util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputValidator {
    private static final Logger logger = LogManager.getLogger(InputValidator.class);
    //Validation for Email
    private static final String EmailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
    private static final Pattern EmailPattern = Pattern.compile(EmailRegex, Pattern.CASE_INSENSITIVE);

    public static boolean validateEmail(String email) {

        try {
            //Check if email is null
            if (email == null) {
                logger.warn("Validation error: Email can't be null");
                return false;
            }

            //Check if email is empty
            if (email.trim().isEmpty()) {
                logger.warn("Validation error: Email can't be empty");
                return false;
            }

            //Check length of email
            if (email.length() > 254) {
                logger.warn("Validation error: Email exceeds maximum length");
                return false;
            }

            //Check for consecutive dots
            if (email.contains("..")) {
                logger.warn("Validation error: Email cannot contain consecutive dots");
                return false;
            }

            //Check if email starts or ends with dot
            if (email.startsWith(".") || email.endsWith(".")) {
                logger.warn("Validation error: Email cannot start or end with a dot");
                return false;
            }

            //Validate with regex pattern
            Matcher matcher = EmailPattern.matcher(email);
            return matcher.matches() ;

            //Error handling
        }catch (IllegalArgumentException ie) {
            logger.error("Illegal input during validation", ie);
        }catch (Exception e) {
            logger.error("Unexpected error during validation", e);
        }
        return false;

    }


    //Validation for Password
    public static boolean validatePassword(String password) {

        try {
            //Check if password is null
            if (password == null) {
                logger.warn("Validation error: Password cannot be null");
                return false;
            }

            //Check if password is empty
            if (password.isEmpty()) {
                logger.warn("Validation error: Password cannot be empty");
                return false;
            }

            //Check password minimum length
            if (password.length() < 8) {
                logger.warn("Validation error: Password is too short");
                return false;
            }

            //Check password max length
            if (password.length() > 64) {
                logger.warn("Validation error: Password exceeds limit");
                return false;
            }

            //Call checkPassword method
            if(checkPassword(password)) {

                if (password.length() >= 8 && password.length() <= 64) {
                    logger.debug("Password strength validation passed");
                }
                return true;
            }
            else
            {
                logger.warn("Validation error: Password is weak. Must contain: Uppercase, Lowercase, Number, and Special characters.");
                return false;
            }

        }catch (NullPointerException npe) {
            logger.error("Password can't be null", npe);
        }catch (Exception e) {
            logger.error("Unexpected error during validation", e);
            return false;
        }

        return false;
    }


    public static boolean checkPassword(String password) {
        boolean hasNumber = false;
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasSpecial = false;
        char c;

        try {
            //Loops through each character of password
            for(int k = 0; k < password.length(); k++) {

                //Assign each character in password to c
                c = password.charAt(k);

                //Check if character is a number
                if(Character.isDigit(c)) {
                    hasNumber = true;
                }

                //Check if character is Uppercase
                else if(Character.isUpperCase(c)) {
                    hasUpperCase = true;
                }

                //Check if character is Lowercase
                else if(Character.isLowerCase(c)) {
                    hasLowerCase = true;
                }

                //Check for special characters
                else if(c == '!' || c == '@' || c == '#' || c == '$' ||
                        c == '%' || c == '^' || c == '&' || c == '*') {

                    hasSpecial = true;
                }
                if(hasNumber && hasLowerCase && hasUpperCase && hasSpecial) {
                    return true;
                }
            }


        }catch (Exception e) {
            logger.error("Error while checking password", e);
            return false;
        }
        return false;
    }
}
