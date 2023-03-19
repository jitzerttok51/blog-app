package com.example.blog.service.impl;

import com.example.blog.dto.AuthDTO;
import com.example.blog.dto.AuthResponseDTO;
import com.example.blog.dto.UserDTO;
import com.example.blog.exceptions.AuthenticationException;
import com.example.blog.service.IJWTAuthenticationService;
import com.example.blog.service.IUserService;
import com.example.blog.util.JWTClaims;
import com.example.blog.util.JWTEntry;
import com.example.blog.util.JWTExpiredException;
import com.example.blog.util.JWTInvalidClaimsException;
import com.example.blog.util.JWTInvalidSignatureException;
import com.example.blog.util.JWTKeystore;
import com.example.blog.util.JWTUtils;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class JWTAuthenticationServiceImpl implements IJWTAuthenticationService {

    private JWTEntry entry;

    private static final String KEYSTORE_NAME = "keystore";

    private static final String KEYSTORE_PASS = "manage";

    private static final String ALIAS = "jwt_key";

    private final IUserService userService;

    private static final long DURATION_IN_MIN = 30;

    @PostConstruct
    public void init() {
        var keystore = JWTKeystore.fromClasspath(KEYSTORE_NAME, KEYSTORE_PASS);
        entry = keystore.getEntry(ALIAS, KEYSTORE_PASS);
    }

    @Override
    public AuthResponseDTO generateToken(AuthDTO auth) {
        var user = userService.findByUsername(auth.getUsername())
                   .orElseThrow(() -> new AuthenticationException("Invalid User credentials", HttpStatus.BAD_REQUEST));

        if(!userService.validatePassword(auth.getUsername(), auth.getPassword())) {
            throw new AuthenticationException("Invalid User credentials", HttpStatus.BAD_REQUEST);
        }

        return generateJwt(user);
    }

    @Override
    public void loginWithKey(String token) {
        try {
            var claims = JWTUtils.verify(token, entry);
            authenticate(getUsername(claims));
        } catch (JWTInvalidClaimsException | JWTExpiredException | JWTInvalidSignatureException e) {
            throw new AuthenticationException("Invalid JWT token", HttpStatus.BAD_REQUEST);
        }
    }

    private void authenticate(String username) {
        var user = userService.loadUserByUsername(username);
        var authToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(),
            user.getAuthorities());
        if(SecurityContextHolder.getContext().getAuthentication() == null) {
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }

    private AuthResponseDTO generateJwt(UserDTO user) {
        var claims = JWTUtils.newClaims();
        claims.setSubject("User Details");
        claims.setIssuer("blog-app");
        claims.addClaim("username", user.getUsername());
        var exp = LocalDateTime.now().minusMinutes(DURATION_IN_MIN);
        claims.setExpiration(exp);
        var token = JWTUtils.sign(claims, entry);
        return new AuthResponseDTO(token, exp, "Bearer");
    }

    private String getUsername(JWTClaims claims) {
        return claims.getClaim("username");
    }
}
