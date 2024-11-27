package com.App.Yogesh.Utilities;

import java.util.regex.Pattern;

public class ValidationUtilities {


    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final String OTP_REGEX = "^[0-9]{6}$";
    private static final String NAME_REGEX = "^[a-zA-Z]{2,30}$";
    private static final String GENDER_REGEX = "^(Male|Female|Other)$";
    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!]).{8,20}$";

    public static boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    public static boolean isValidOtp(String otp) {
        return Pattern.matches(OTP_REGEX, otp);
    }

    public static boolean isValidName(String name) {
        return Pattern.matches(NAME_REGEX, name);
    }


    public static boolean areValidNames(String firstName, String lastName) {
        return isValidName(firstName) && isValidName(lastName);
    }


    public static boolean isValidGender(String gender) {
        return Pattern.matches(GENDER_REGEX, gender);
    }


    public static boolean isValidPassword(String password) {
        return Pattern.matches(PASSWORD_REGEX, password);
    }
}
