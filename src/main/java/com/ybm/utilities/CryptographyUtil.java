/*
 * Copyright (c) 2023. The code is intellectual property of Yogeshwar Mahalle.
 */

package com.ybm.utilities;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * CryptoTool is class to encrypt and decrypt data.
 * @author Yogeshwar Mahalle
 *
 */
public class CryptographyUtil
{
    /**
     * Encryptor
     */
    Cipher ecipher;
    /**
     * Decryptor
     */
    Cipher dcipher;
    /**
     * Constructor with key as String
     * @param key Key
     */
    public CryptographyUtil(String key)
    {
        this.init(key);
    }
    /**
     * Constructor with key as SecretKey
     * @param key String key to encrypt or decrypt data
     */
    public CryptographyUtil(SecretKey key)
    {
        this.init(key);
    }
    /**
     * Init key
     * @param key SecretKey to encrypt or decrypt data
     * @return true if success and false if failed
     */
    public boolean init(SecretKey key)
    {
        try
        {
            ecipher = Cipher.getInstance("AES");
            dcipher = Cipher.getInstance("AES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            dcipher.init(Cipher.DECRYPT_MODE, key);
        }
        catch (javax.crypto.NoSuchPaddingException e)
        {
            e.printStackTrace();
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (java.security.InvalidKeyException e)
        {
            e.printStackTrace();
        }
        return true;
    }
    /**
     * Init key
     * @param key Key
     * @return true if success and false if failed
     */
    public boolean init(String key)
    {
        while( key.length() < 16)
        {
            key += "&";
        }
        byte[] bkey = (key).getBytes();
        bkey = Arrays.copyOf(bkey, 16); // use only first 128 bit
        SecretKeySpec skey2 = new SecretKeySpec(bkey, "AES");
        try
        {
            ecipher = Cipher.getInstance("AES");
            dcipher = Cipher.getInstance("AES");
            ecipher.init(Cipher.ENCRYPT_MODE, skey2);
            dcipher.init(Cipher.DECRYPT_MODE, skey2);
        }
        catch (javax.crypto.NoSuchPaddingException e)
        {
            e.printStackTrace();
        }
        catch (java.security.NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (java.security.InvalidKeyException e)
        {
            e.printStackTrace();
        }
        return true;
    }
    /**
     * Encrypt plain text into cipher text
     * @param input Plain text to be encrypted
     * @return String containing cipher text
     */
    public String encrypt(String input)
    {
        try
        {
            // Encode the string into bytes using utf-8
            byte[] utf8 = input.getBytes("UTF8");
            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);
            // Encode bytes to base64 to get a string
            return new String(this.base64Encode(enc));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Decrypt cipher text into plain text
     * @param input Cipher text
     * @return Plain text
     */
    public String decrypt(String input)
    {
        try
        {
            // Decode base64 to get bytes
            byte[] dec = this.base64Decode(input);
            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);
            // Decode using utf-8
            return new String(utf8, "UTF8");
        }
        catch (javax.crypto.BadPaddingException e)
        {
            e.printStackTrace();
        }
        catch (IllegalBlockSizeException e)
        {
            e.printStackTrace();
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * Base64 encoding
     * @param input String to be encoded
     * @return Encoded string
     */
    public String base64Encode(byte[] input)
    {
        try
        {
            return Base64.getEncoder().encodeToString(input);
        }
        catch(NullPointerException e)
        {
            return "";
        }
    }
    /**
     * Base64 decoding
     * @param input String to be decoded
     * @return Decoded string
     */
    public byte[] base64Decode(String input)
    {
        try
        {
            return Base64.getDecoder().decode(input);
        }
        catch(NullPointerException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get SHA-1 digest of string
     * @param input Input string
     * @return SHA-1 digest
     */
    public static String sha1(String input)
    {
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(input.getBytes("UTF-8"));
            sha1 = byteArrayToHexString(crypt.digest());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return sha1;
    }
    /**
     * Get MD5 digest of string
     * @param input Input string
     * @return MD5 digest
     */
    public static String md5(String input)
    {
        String sha1 = "";
        try
        {
            MessageDigest crypt = MessageDigest.getInstance("MD5");
            crypt.reset();
            crypt.update(input.getBytes("UTF-8"));
            sha1 = byteArrayToHexString(crypt.digest());
        }
        catch(NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch(UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return sha1;
    }


    /**
     * Convert array byte to string contains hexadecimal number
     * @param b array byte
     * @return String contains hexadecimal number
     */
    public static String byteArrayToHexString(byte[] b)
    {
        String result = "";
        for (int i=0; i < b.length; i++)
        {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }
}
