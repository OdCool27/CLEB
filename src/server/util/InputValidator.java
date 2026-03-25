package server.util;

//import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class InputValidator {

    //Validation for Email
	private static final String EmailRegex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$";
	private static final Pattern EmailPattern = Pattern.compile(EmailRegex, Pattern.CASE_INSENSITIVE);
	
	public static boolean validateEmail(String email) {
		
		try {
			//Check if email is null
			if (email == null) {
				System.err.println("Validation error: Email can't be null");
				return false;
				}
			
			//Check if email is empty
			if (email.trim().isEmpty()) {
				System.err.println("Validation error: Email can't be empty");
				return false;
				}
			
			//Check length of email
			 if (email.length() > 254) {
				 System.err.println("Validation error: Email exceeds maximum length");
	                return false;
	                }
	            
	         //Check for consecutive dots
			 if (email.contains("..")) {
				 System.err.println("Validation error: Email cannot contain consecutive dots");
	                return false;
	                }
			 
			 //Check if email starts or ends with dot
			 if (email.startsWith(".") || email.endsWith(".")) {
				 System.err.println("Validation error: Email cannot start or end with a dot");
	                return false;
	                }
			 
		//Validate with regex pattern
		Matcher matcher = EmailPattern.matcher(email);
		return matcher.matches() ;
		
		//Error handling
		}catch (IllegalArgumentException ie) {
			System.err.println("Illegal input during validation: " + ie.getMessage());
			ie.printStackTrace();
		}catch (Exception e) {
			System.err.println("Unexpected error during validation: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
			
	}
	
	
    //Validation for Password
	public static boolean validatePassword(String password) {
		
		try {
			//Check if password is null 
	        if (password == null) {
	            System.err.println("Validation error: Password cannot be null");
	            return false;
	        }

	        //Check if password is empty
	        if (password.isEmpty()) {
	            System.err.println("Validation error: Password cannot be empty");
	            return false;
	        }
	        
	        //Check password minimum length
	        if (password.length() < 8) {
	            System.err.println("Validation error: Password is too short");
	            return false;
	        }

	        //Check password max length
	        if (password.length() > 64) {
	            System.err.println("Validation error: Password exceeds limit");
	            return false;
	        }
	        
	        //Call checkPassword method
	        if(checkPassword(password)) {
	        	
	        	if (password.length() >= 8 && password.length() <= 64) {
	                System.out.println("Password is strong!");
	            }
					return true;
				}
				else 
				{
					System.err.println("Validation error: Password is weak. Must contain:\n" 
							+ "- Uppercase letter\n"
							+ "- Lowercase letter\n"
							+ "- Number\n"
							+ "- Special characters (! @ # $ ^ * & %)");
					return false;
				}
	        
			}catch (NullPointerException npe) {
				System.err.println("Password can't be null: " + npe.getMessage());
				npe.printStackTrace();
			}catch (Exception e) {
				System.err.println("Unexpected error during validation: " + e.getMessage());
				e.printStackTrace();
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
		System.err.println("Error while checking password: " + e.getMessage());
		e.printStackTrace();
        return false;
        }
		return false;
		}
	
}