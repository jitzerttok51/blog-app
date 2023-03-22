package com.example.blog.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {

    public static String calculateSha256(Path file) throws IOException {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }
        try(var fis = Files.newInputStream(file)) {
            var buffer = new byte[1024];
            int n = 0;
            while ((n = fis.read(buffer)) != -1) {
                md.update(buffer, 0, n);
            }

            return bytesToHex(md.digest());
        }
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes)
            result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
}
