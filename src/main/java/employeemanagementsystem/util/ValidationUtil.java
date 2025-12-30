package employeemanagementsystem.util;

import java.util.regex.Pattern;

// Utility class for input validation
public class ValidationUtil {

    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, email) && email.endsWith("@gmail.com");
    }

    public static boolean isValidPhone(String phone) {
        String phoneRegex = "^\\+2519\\d{8}$";
        return Pattern.matches(phoneRegex, phone);
    }

    public static boolean isValidName(String name) {
        return name != null && name.matches("[a-zA-Z\\s]+") && name.length() >= 2;
    }

    public static boolean isFieldEmpty(String field) {
        return field == null || field.trim().isEmpty();
    }
}