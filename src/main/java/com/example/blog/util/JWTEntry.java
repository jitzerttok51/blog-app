package com.example.blog.util;

import lombok.RequiredArgsConstructor;

import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;

@RequiredArgsConstructor
public class JWTEntry {

    private final KeyStore.PrivateKeyEntry entry;

    public PrivateKey getPrivateKey() {
        return entry.getPrivateKey();
    }

    public PublicKey getPublicKey() {
        return entry.getCertificate().getPublicKey();
    }
}
