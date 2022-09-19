package com.example.applichatlimayrac;

import com.google.firebase.database.DataSnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternChecker {

    public static boolean isEmailAddressValid(String sEmail) {

        final Pattern EmailPattern = Pattern.compile(Globals.EMAIL_PATTERN);
        Matcher EmailMatcher = EmailPattern.matcher(sEmail);

        return EmailMatcher.matches();
    }

    public static boolean isPasswordValid(String sPassword) {

        final Pattern PasswordPattern = Pattern.compile(Globals.PASSWORD_PATTERN);
        Matcher PasswordMatcher = PasswordPattern.matcher(sPassword);

        return PasswordMatcher.matches();
    }

}
