package com.example.blog.validation;

import com.example.blog.service.IUserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    private final IUserService userService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return userService.findByEmail(value).isEmpty();
    }
}
