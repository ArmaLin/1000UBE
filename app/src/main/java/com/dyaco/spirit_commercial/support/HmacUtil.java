package com.dyaco.spirit_commercial.support;

import android.util.Log;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacUtil {

    private static final String CIPHER_ENCRYPT_TYPE_HMAC = "HmacSHA256";
    public static final String HMAC_KEY = "IE6Yf1xFbsSpBMJUfBUcNSCUfmOzQi8cFupjsuMWTBs=";
    private static final String TAG = "HmacUtil";

    /**
     * @return Base64String
     */
//    public static String hash(String value, byte[] hashKey){
    public static String hash(String value) {
        try {

            //  byte[] hashKey = HMAC_KEY.getBytes(Charsets.UTF_8);
            byte[] hashKey = Base64.getDecoder().decode(HMAC_KEY);

            Mac sha256HMAC = Mac.getInstance(CIPHER_ENCRYPT_TYPE_HMAC);
            sha256HMAC.init(new SecretKeySpec(hashKey, CIPHER_ENCRYPT_TYPE_HMAC));
            byte[] bytes = sha256HMAC.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (Exception e) {
            Log.d(TAG, "hash: " + e.getLocalizedMessage());
            return "";
        }
    }

    /**
     * @return Base64String
     */
    public static String createHMAC2Key() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(CIPHER_ENCRYPT_TYPE_HMAC);
            keyGenerator.init(256);
            byte[] bytes = keyGenerator.generateKey().getEncoded();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (NoSuchAlgorithmException e) {
            Log.d(TAG, "hash: " + e.getLocalizedMessage());
            return "";
        }
    }

   // public static void main(String[] args) {
    public static void ttttt(String clientRequestJson) {
        String sharedHasKeyBase64String = createHMAC2Key();
//        System.out.println("HMAC Key:" + sharedHasKeyBase64String);
      //  Log.d(TAG, "HmacUtil: " + sharedHasKeyBase64String);

// Client: Generate HMAC Value
        long timestamp = System.currentTimeMillis();
     //   String clientRequestJson = "{\"company\": \"dyaco\", \"timestamp\":" + timestamp + "}";
        byte[] hashKey = Base64.getDecoder().decode(sharedHasKeyBase64String);
        String clientHashValue = hash(clientRequestJson);
//        System.out.println("Hash Value:" + clientHashValue);

        Log.d(TAG, "Hash Value: " + clientHashValue);

// Server: Verify HMAC Value
        String serverHashValue = hash(clientRequestJson);
        Log.d(TAG, "Is trusted Client: " + clientHashValue.equals(serverHashValue));
//        System.out.println("Is trusted Client:" + clientHashValue.equals(serverHashValue)); // Expected: true
    }
}
