package com.example.vetra_fitnessapp_tfg.utils;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class KeyStoreManager {

    // Atributos
    private static final String TAG = "KeyStoreManager";
    private static final String SECRET_KEY = "ThisIsMyStaticKeyForDemoPurposesOnly";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int IV_SIZE = 12;
    private static final int TAG_LENGTH = 128;

    private SecretKeySpec secretKey;

    public KeyStoreManager() {
        try {
            // Derivar una clave AES a partir del hash SHA-256 del string secreto
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(SECRET_KEY.getBytes("UTF-8"));
            secretKey = new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            // IV aleatorio
            byte[] iv = new byte[IV_SIZE];
            SecureRandom sr = new SecureRandom();
            sr.nextBytes(iv);

            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF-8"));

            // concatenar IV + ciphertext
            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

            // Base64 SIN saltos de línea
            return Base64.encodeToString(combined, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decrypt(String cipherText) {
        try {
            // Base64 SIN saltos de línea
            byte[] combined = Base64.decode(cipherText, Base64.NO_WRAP);

            // extraer IV
            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(combined, 0, iv, 0, IV_SIZE);

            // extraer ciphertext real
            int ctLen = combined.length - IV_SIZE;
            byte[] actualCipherText = new byte[ctLen];
            System.arraycopy(combined, IV_SIZE, actualCipherText, 0, ctLen);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);

            byte[] plainBytes = cipher.doFinal(actualCipherText);
            return new String(plainBytes, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
