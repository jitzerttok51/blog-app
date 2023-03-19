package com.example.blog.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.passay.WhitespaceRule;

import java.util.List;

@RequiredArgsConstructor
public class PasswordRulesValidator implements ConstraintValidator<ValidPassword, String> {

    private static final PasswordValidator VALIDATOR = new PasswordValidator(List.of(
        new LengthRule(8, 30),
        new WhitespaceRule()
    ));

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        var result = VALIDATOR.validate(new PasswordData(value));
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(
                   String.join(",", VALIDATOR.getMessages(result)))
               .addConstraintViolation();
        return result.isValid();
    }
}
