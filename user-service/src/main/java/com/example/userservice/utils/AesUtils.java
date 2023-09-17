package com.example.userservice.utils;

import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Hex;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class AesUtils {


    private final Environment env;

    public String enCodeString(String plainText) {

        final String secretKey = env.getProperty("aes.secret");
        SecretKeySpec spec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec iv = new IvParameterSpec(secretKey.substring(0, 16).getBytes());

        byte[] encryptionByte = null;

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, spec, iv);

            encryptionByte = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Hex.encodeHexString(encryptionByte);
    }

    public String decodeString(String encodeText) {
        final String secretKey = env.getProperty("aes.secret");
        SecretKeySpec spec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        IvParameterSpec iv = new IvParameterSpec(secretKey.substring(0, 16).getBytes());

        byte[] decodeByte;
        Cipher cipher;
        String decodeString;

        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, spec, iv);

            decodeByte = Hex.decodeHex(encodeText.toCharArray());
            decodeString = new String(cipher.doFinal(decodeByte), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }



        return decodeString;
    }

}
