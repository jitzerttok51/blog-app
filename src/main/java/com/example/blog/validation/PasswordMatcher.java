package com.example.blog.validation;

import com.example.blog.dto.UserCreateDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PasswordMatcher implements ConstraintValidator<PasswordMatch, UserCreateDTO> {

    @Override
    public boolean isValid(UserCreateDTO value, ConstraintValidatorContext context) {
        var p1 = value.getPassword();
        var p2 = value.getConfirmPassword();
        return p1 != null && p1.equals(p2);
    }
}
