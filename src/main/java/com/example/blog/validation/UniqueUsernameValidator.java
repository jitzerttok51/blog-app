package com.example.blog.validation;

import com.example.blog.service.IUserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    private final IUserService userService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return userService.findByUsername(value).isEmpty();
    }
}
