package com.example.chatapp.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    //||... validate mobile ...||
    public boolean validateMobileNumber(String mobile) {
        String regex = "\\d{3}-\\d{3}-\\d{4}";
        return mobile.matches(regex);
    }

    //||... validate email ...||
    public  boolean validateEmail(String emailStr) {
        Matcher matcher = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(emailStr);
        return matcher.find();
    }

    //||... validate password ...||
    public  boolean validatePassword(String password) {
        String regex = "^(?=.*[0-9])"
                + "(?=.*[a-z])(?=.*[A-Z])"
                + "(?=.*[@#$%^&+=])"
                + "(?=\\S+$).{8,20}$";
        return Pattern.compile(regex).matcher(password).matches();
    }
}
