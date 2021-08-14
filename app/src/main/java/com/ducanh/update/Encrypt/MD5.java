package com.ducanh.update.Encrypt;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    public String MD5(String text) {
        if (text != "") {
            try {
                MessageDigest md = MessageDigest.getInstance ("MD5");
                byte[] messageDigest = md.digest (text.getBytes ());
                BigInteger number = new BigInteger (1, messageDigest);
                String hashtext = number.toString (16);
                while (hashtext.length () < 32) {
                    hashtext = "0" + hashtext;
                }
                return hashtext;
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException (e);
            }
        }
        return "";
    }
}
