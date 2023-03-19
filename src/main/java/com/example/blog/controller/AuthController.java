package com.example.blog.controller;

import com.example.blog.dto.AuthDTO;
import com.example.blog.dto.AuthResponseDTO;
import com.example.blog.service.IJWTAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final IJWTAuthenticationService authenticationService;

    @PostMapping
    public AuthResponseDTO auth(@RequestBody AuthDTO auth) {
        return authenticationService.generateToken(auth);
    }
}
