package org.example.goormssd.usermanagementbackend.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoTripleRepeatValidator implements ConstraintValidator<org.example.goormssd.usermanagementbackend.util.NoTripleRepeat, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;

        for (int i = 0; i < value.length() - 2; i++) {
            if (value.charAt(i) == value.charAt(i + 1) && value.charAt(i) == value.charAt(i + 2)) {
                return false;
            }
        }

        return true;
    }
}
