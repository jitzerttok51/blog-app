package com.example.blog.util;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.List;

public class JWTKeystore {

    private final KeyStore keyStore;

    private List<String> alias = null;

    public static JWTKeystore fromClasspath(String resource, String password) {
        try(var in = ClassLoader.getSystemResourceAsStream(resource)) {
            return new JWTKeystore(in, password);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private JWTKeystore(InputStream in, String password) {
        try {
            keyStore = KeyStore.getInstance("JKS");
            keyStore.load(in, password.toCharArray());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> aliases() {
        try {
            if(alias == null) {
                alias = new ArrayList<>();
                var e = keyStore.aliases();
                while (e.hasMoreElements()) {
                    alias.add(e.nextElement());
                }
            }
            return alias;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public JWTEntry getEntry(String alias, String password) {
        try {
            var pass = new KeyStore.PasswordProtection(password.toCharArray());
            return new JWTEntry((KeyStore.PrivateKeyEntry) keyStore.getEntry(alias, pass));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
