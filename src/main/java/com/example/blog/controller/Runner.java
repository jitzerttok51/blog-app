package com.example.blog.controller;

import com.example.blog.util.JWTKeystore;
import com.example.blog.util.JWTUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Runner implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {

        var keystore = JWTKeystore.fromClasspath("keystore", "manage");
        var entry = keystore.getEntry("jwt_key", "manage");

        var claims = JWTUtils.newClaims();
        claims.setIssuer("Ivan");
        claims.setExpiration(LocalDateTime.now().plusHours(1));
        var token = JWTUtils.sign(claims, entry);
        System.out.println(JWTUtils.verify(token, entry));
    }
}
