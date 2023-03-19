package com.example.blog.dto;

import com.example.blog.validation.PasswordMatch;
import com.example.blog.validation.UniqueEmail;
import com.example.blog.validation.UniqueUsername;
import com.example.blog.validation.ValidEmail;
import com.example.blog.validation.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@PasswordMatch
public class UserCreateDTO {

    @NotNull
    @NotBlank
    @UniqueUsername
    private String username;

    @ValidEmail
    @NotNull
    @NotBlank
    @UniqueEmail
    private String email;

    @NotNull
    @NotBlank
    @ValidPassword
    private String password;

    @NotNull
    @NotBlank
    private String confirmPassword;
}
