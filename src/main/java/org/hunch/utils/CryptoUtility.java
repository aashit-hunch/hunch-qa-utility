package org.hunch.utils;


import org.hunch.constants.Config;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class CryptoUtility {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final int IV_SIZE = 16;

    private static SecretKey encryptionKey;
    private static SecretKey signingKey;

    //private static CryptoUtility _instance =null;

    private CryptoUtility(){

    }

    static
    {
        byte[] encKeyBytes = Base64.getDecoder().decode(Config.ENCRYPTION_KEY);
        byte[] signKeyBytes = Base64.getDecoder().decode(Config.SIGNING_KEY);
        encryptionKey = new SecretKeySpec(encKeyBytes, ALGORITHM);
        signingKey = new SecretKeySpec(signKeyBytes, HMAC_ALGORITHM);

    }

    /**
     * Generates a random AES key
     */
    private static SecretKey generateKey(){
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
            keyGen.init(256);
            return keyGen.generateKey();
        }
        catch (Exception e){

        }
        return  null;
    }

    /**
     * Encrypts a string and returns Base64 encoded result with HMAC signature
     * Format: Base64(IV + EncryptedData + HMAC)
     */
    public static String encrypt(String stringToEncrypt) {
        if (stringToEncrypt == null || stringToEncrypt.isEmpty()) {
            throw new IllegalArgumentException("String to encrypt cannot be null or empty");
        }

        // Generate random IV
        byte[] iv = new byte[IV_SIZE];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Encrypt the data
        List<byte[]> encryptedBytes = new ArrayList<>();
        try {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, ivSpec);
            encryptedBytes = Arrays.asList(cipher.doFinal(stringToEncrypt.getBytes(StandardCharsets.UTF_8)));
        }
        catch (Exception e){
            System.out.println("Exception Occurred :"+e.getMessage());
        }

        // Combine IV and encrypted data
        byte[] combined = new byte[iv.length + encryptedBytes.get(0).length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedBytes.get(0), 0, combined, iv.length, encryptedBytes.get(0).length);

        // Generate HMAC signature
        byte[] signature = generateHMAC(combined);

        // Combine all (IV + EncryptedData + HMAC)
        byte[] finalData = new byte[combined.length + signature.length];
        System.arraycopy(combined, 0, finalData, 0, combined.length);
        System.arraycopy(signature, 0, finalData, combined.length, signature.length);

        // Return Base64 encoded string
        return Base64.getEncoder().encodeToString(finalData);
    }

    /**
     * Decrypts a Base64 encoded string and verifies HMAC signature
     */
    public static String decrypt(String stringToDecrypt) {
        if (stringToDecrypt == null || stringToDecrypt.isEmpty()) {
            throw new IllegalArgumentException("String to decrypt cannot be null or empty");
        }

        // Decode from Base64
        byte[] decodedData = Base64.getDecoder().decode(stringToDecrypt);

        // Extract HMAC signature (last 32 bytes for SHA256)
        int signatureLength = 32;
        if (decodedData.length < IV_SIZE + signatureLength) {
            throw new IllegalArgumentException("Invalid encrypted data format");
        }

        byte[] dataWithoutSignature = new byte[decodedData.length - signatureLength];
        byte[] receivedSignature = new byte[signatureLength];

        System.arraycopy(decodedData, 0, dataWithoutSignature, 0, dataWithoutSignature.length);
        System.arraycopy(decodedData, dataWithoutSignature.length, receivedSignature, 0, signatureLength);

        // Verify HMAC signature
        byte[] computedSignature = generateHMAC(dataWithoutSignature);
        if (!java.security.MessageDigest.isEqual(computedSignature, receivedSignature)) {
            throw new SecurityException("HMAC signature verification failed - data may be tampered");
        }

        // Extract IV
        byte[] iv = new byte[IV_SIZE];
        System.arraycopy(dataWithoutSignature, 0, iv, 0, IV_SIZE);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        // Extract encrypted data
        byte[] encryptedData = new byte[dataWithoutSignature.length - IV_SIZE];
        System.arraycopy(dataWithoutSignature, IV_SIZE, encryptedData, 0, encryptedData.length);

        // Decrypt
        List<byte[]> decryptedBytes = new ArrayList<>();
        try
        {
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey, ivSpec);
            decryptedBytes = Arrays.asList(cipher.doFinal(encryptedData));
        }
        catch (Exception e){

        }

        return new String(decryptedBytes.get(0), StandardCharsets.UTF_8);
    }

    /**
     * Generates HMAC signature for the given data
     */
    private static byte[] generateHMAC(byte[] data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(signingKey);
            return mac.doFinal(data);
        }
        catch (Exception e){

        }
        return null;
    }

    /**
     * Returns the encryption key as Base64 string (for storage/reuse)
     */
    public static String getEncryptionKeyBase64() {
        return Base64.getEncoder().encodeToString(encryptionKey.getEncoded());
    }

    /**
     * Returns the signing key as Base64 string (for storage/reuse)
     */
    public static String getSigningKeyBase64() {
        return Base64.getEncoder().encodeToString(signingKey.getEncoded());
    }


}
