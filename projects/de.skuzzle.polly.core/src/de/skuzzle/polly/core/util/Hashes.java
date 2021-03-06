package de.skuzzle.polly.core.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Hashes {

    public static String sha512(String s) {
        try {
            final MessageDigest m = MessageDigest.getInstance("SHA-512"); //$NON-NLS-1$
            m.update(s.getBytes(), 0, s.length());
            return new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    
    
    
    public static String md5(String s) {
        if (s == null) {
            s = ""; //$NON-NLS-1$
        }
        String result = ""; //$NON-NLS-1$
        try {
            MessageDigest m = MessageDigest.getInstance("MD5"); //$NON-NLS-1$
            m.update(s.getBytes(), 0, s.length());
            result = new BigInteger(1, m.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
