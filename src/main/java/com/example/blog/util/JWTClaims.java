package com.example.blog.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class JWTClaims {

    private String subject = "";
    private String issuer = "";
    private LocalDateTime issuedAt = LocalDateTime.now();
    private LocalDateTime expiration = LocalDateTime.now();

    private final Map<String, String> additionalClaims = new HashMap<>();

    JWTClaims() {

    }

    JWTClaims(Claims claims) {
        subject = claims.getSubject();
        issuer = claims.getIssuer();
        issuedAt = toLocalDateTime(claims.getIssuedAt());
        expiration = toLocalDateTime(claims.getExpiration());

        claims
              .entrySet()
              .stream()
              .filter(this::notPreselect)
              .forEach(e -> addClaim(e.getKey(), e.getValue().toString()));
    }

    private boolean notPreselect(Map.Entry<String, Object> e) {
        return !List.of(Claims.ISSUED_AT, Claims.ISSUER, Claims.EXPIRATION, Claims.SUBJECT).contains(e.getKey());
    }

    Claims toClaims() {
        return Jwts.claims(new HashMap<>(additionalClaims))
                   .setExpiration(toDate(expiration))
                   .setIssuer(issuer)
                   .setIssuedAt(toDate(issuedAt))
                   .setSubject(subject);
    }

    private Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    private LocalDateTime toLocalDateTime(Date dateToConvert) {
        if(dateToConvert == null) {
            return null;
        }
        return dateToConvert.toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();
    }

    public void addClaim(String key, String value) {
        additionalClaims.put(key, value);
    }

    public String getClaim(String key) {
       return additionalClaims.get(key);
    }
}
