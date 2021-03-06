/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.itopen.simplerest.microservice.loadbalancer;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author roland
 */
public class EncryptionHelper {

    /**
     *
     * @param key
     * @param initVector
     * @param value
     * @return
     */
    public static String aesEncrypt(String key, String initVector, String value) {
        return Base64.encodeBase64URLSafeString(aesEncrypt(key, initVector, value.getBytes()));
    }

    /**
     *
     * @param key
     * @param initVector
     * @param value
     * @return
     */
    public static byte[] aesEncrypt(String key, String initVector, byte[] value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            return cipher.doFinal(value);
        } catch (UnsupportedEncodingException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @param key
     * @param initVector
     * @param encrypted
     * @return
     */
    public static String aesDecrypt(String key, String initVector, String encrypted) {
        return new String(aesDecrypt(key, initVector, Base64.decodeBase64(encrypted)));
    }

    /**
     *
     * @param key
     * @param initVector
     * @param encrypted
     * @return
     */
    public static byte[] aesDecrypt(String key, String initVector, byte[] encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

            return cipher.doFinal(encrypted);

        } catch (UnsupportedEncodingException | InvalidAlgorithmParameterException | InvalidKeyException | NoSuchAlgorithmException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private static String pad(String source, int length, String padding) {
        if (source == null) {
            return null;
        }
        while (source.length() < length) {
            source += padding; // padding;
        }
        if (source.length() > length) {
            source = source.substring(0, length);
        }
        return source;
    }

    /**
     *
     * @param key
     * @return
     */
    public static String correctKEY(String key) {
        return pad(key, 128 / 8, "-");
    }

    /**
     *
     * @param key
     * @return
     */
    public static String correctINITV(String key) {
        return pad(key, 16, "-");
    }

    /**
     *
     * @param input
     * @return
     */
    public static String sha512(String input) {
        try {
            // getInstance() method is called with algorithm SHA-512
            MessageDigest md = MessageDigest.getInstance("SHA-512");

            // digest() method is called
            // to calculate message digest of the input string
            // returned as array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);

            // Add preceding 0s to make it 32 bit
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }

            // return the HashText
            return hashtext;
        } // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

}
