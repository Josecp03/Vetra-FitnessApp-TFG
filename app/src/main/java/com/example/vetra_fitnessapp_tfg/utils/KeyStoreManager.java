package com.example.vetra_fitnessapp_tfg.utils;

import android.util.Base64;

import java.security.MessageDigest;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Gestor de cifrado y descifrado de datos sensibles usando AES-GCM.
 * Proporciona métodos seguros para encriptar y desencriptar información
 * del usuario como nombres, emails y otros datos personales.
 *
 * @author José Corrochano Pardo
 * @version 1.0
 */
public class KeyStoreManager {

    /** Tag para logging */
    private static final String TAG = "KeyStoreManager";

    /** Clave secreta estática para demostración (en producción debería ser más segura) */
    private static final String SECRET_KEY = "ThisIsMyStaticKeyForDemoPurposesOnly";

    /** Algoritmo de transformación para el cifrado */
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    /** Tamaño del vector de inicialización en bytes */
    private static final int IV_SIZE = 12;

    /** Longitud del tag de autenticación en bits */
    private static final int TAG_LENGTH = 128;

    /** Clave secreta derivada para el cifrado */
    private SecretKeySpec secretKey;

    /**
     * Constructor que inicializa el gestor de cifrado.
     * Deriva una clave AES de 256 bits a partir de la clave secreta usando SHA-256.
     */
    public KeyStoreManager() {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = digest.digest(SECRET_KEY.getBytes("UTF-8"));
            secretKey = new SecretKeySpec(keyBytes, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Cifra un texto plano usando AES-GCM.
     * Genera un IV aleatorio para cada operación de cifrado y lo incluye
     * en el resultado final codificado en Base64.
     *
     * @param plainText Texto plano a cifrar
     * @return Texto cifrado codificado en Base64, o null si ocurre un error
     */
    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            byte[] iv = new byte[IV_SIZE];
            SecureRandom sr = new SecureRandom();
            sr.nextBytes(iv);

            GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes("UTF-8"));

            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

            return Base64.encodeToString(combined, Base64.NO_WRAP);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Descifra un texto cifrado que fue codificado en Base64.
     * Extrae el IV del inicio del texto cifrado y lo usa para el descifrado.
     *
     * @param cipherText Texto cifrado codificado en Base64
     * @return Texto plano descifrado, o null si ocurre un error
     */
    public String decrypt(String cipherText) {
        try {
            byte[] combined = Base64.decode(cipherText, Base64.NO_WRAP);

            byte[] iv = new byte[IV_SIZE];
            System.arraycopy(combined, 0, iv, 0, IV_SIZE);

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
