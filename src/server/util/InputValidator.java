package server.util;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;


public class InputValidator {

    //Validation for Email
	private static final String EmailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
	private static final Pattern EmailPattern = Pattern.compile(EmailRegex, Pattern.CASE_INSENSITIVE);
	
	public static boolean validateEmail(String email) {
		
		try {
			//Check if email is null
			if (email == null) {
				JOptionPane.showMessageDialog(null, "Email can't be null", 
						"Validation error", JOptionPane.ERROR_MESSAGE);
				return false;
				}
			
			//Check if email is empty
			if (email.trim().isEmpty()) {
				JOptionPane.showMessageDialog(null, "Email can't be empty", 
						"Validation error", JOptionPane.ERROR_MESSAGE);
				return false;
				}
			
			//Check leangth of email
			 if (email.length() > 254) {
				 JOptionPane.showMessageDialog(null, "Email exceeds maximum length",
		                "Validation Error", JOptionPane.ERROR_MESSAGE);
	                return false;
	                }
	            
	         //Check for consecutive dots
			 if (email.contains("..")) {
				 JOptionPane.showMessageDialog(null, "Email cannot contain consecutive dots",
		                "Validation Error", JOptionPane.ERROR_MESSAGE);
	                return false;
	                }
			 
			 //Check if email starts or ends with dot
			 if (email.startsWith(".") || email.endsWith(".")) {
				 JOptionPane.showMessageDialog(null, "Email cannot start or end with a dot",
		                "Validation Error", JOptionPane.ERROR_MESSAGE);
	                return false;
	                }
			 
		//Validate with regex pattern
		Matcher matcher = EmailPattern.matcher(email);
		return matcher.matches() ;
		
		//Error handling
		}catch (IllegalArgumentException ie) {
			JOptionPane.showMessageDialog(null, "Illegal input during validation" + ie.getMessage(),
	                "Validation Error", JOptionPane.ERROR_MESSAGE);

		}catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Unexpected error during validation" + e.getMessage(),
	                "Validation Error", JOptionPane.ERROR_MESSAGE);
		}
		return false;
			
	}
	
	
    //Validation for Password
	public static boolean validatePassword(String password) {
		
		try {
			 //Check if password is null 
	        if (password == null) {
	            JOptionPane.showMessageDialog(null, "Password cannot be null",
	                    "Validation Error", JOptionPane.ERROR_MESSAGE);
	            return false;
	        }

	        //Check if password is empty
	        if (password.isEmpty()) {
	            JOptionPane.showMessageDialog(null, "Password cannot be empty",
	                    "Validation Error", JOptionPane.ERROR_MESSAGE);
	            return false;
	        }
	        
	      //Check password minimum length
	        if (password.length() < 8) {
	            JOptionPane.showMessageDialog(null, "Password is too short",
	                    "Validation Error", JOptionPane.ERROR_MESSAGE);
	            return false;
	        }

	      //Check password max length
	        if (password.length() > 64) {
	            JOptionPane.showMessageDialog(null, "Password is exceeds limit",
	                    "Validation Error", JOptionPane.ERROR_MESSAGE);
	            return false;
	        }
	        
	        //Call checkPassword method
	        if(checkPassword(password)) {
	        	
	        	if (password.length() >= 8 && password.length() <= 64) {
	                JOptionPane.showMessageDialog(null, "Password is strong!",
	                        "Valid password", JOptionPane.INFORMATION_MESSAGE);
	            }
					return true;
				}
				else 
				{
					JOptionPane.showMessageDialog(null, "Password is weak. Must contain:\n" 
							+ "- Uppercase letter\n"
							+ "- Lowercase letter\n"
							+ "- Number\n"
							+ "- Special characters (! @ # $ ^ * & %)", 
							"Validation Error", JOptionPane.ERROR_MESSAGE);
					return false;
				}
	        
			}catch (NullPointerException npe) {
				JOptionPane.showMessageDialog(null, "Password can't be null" + npe.getMessage(),
		                "Validation Error", JOptionPane.ERROR_MESSAGE);
			}catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Unexpected error during validation: " + e.getMessage(),
		                "Validation Error", JOptionPane.ERROR_MESSAGE);
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
		JOptionPane.showMessageDialog(null, "Error while checking password: " + e.getMessage(),
                "Validation Error", JOptionPane.ERROR_MESSAGE);
        return false;
        }
		return false;
		}
	
}
