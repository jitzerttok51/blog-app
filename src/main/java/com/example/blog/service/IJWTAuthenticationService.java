package com.example.blog.service;

import com.example.blog.dto.AuthDTO;
import com.example.blog.dto.AuthResponseDTO;
import com.example.blog.exceptions.AuthenticationException;

public interface IJWTAuthenticationService {

    AuthResponseDTO generateToken(AuthDTO auth) throws AuthenticationException;

    void loginWithKey(String token) throws AuthenticationException;
}
