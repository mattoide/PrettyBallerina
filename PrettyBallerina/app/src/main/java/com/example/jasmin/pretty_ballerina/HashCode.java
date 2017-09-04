package com.example.jasmin.pretty_ballerina;

import java.nio.charset.Charset;
import java.security.MessageDigest;

/**
 * Created by anon on 02/09/17.
 */

public class HashCode {

    public  static String hashCode(String pass) {

        String algoritmo = "SHA-1";
        try {
            MessageDigest md = MessageDigest.getInstance(algoritmo);
            if (Charset.isSupported("CP1252"))

                md.update(pass.getBytes(Charset.forName("CP1252")));
            else
                md.update(pass.getBytes(Charset.forName("ISO-8859-1")));

            byte[] bytes = md.digest();
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < bytes.length; i++)

                str.append(Integer.toHexString((bytes[i] & 0xFF) | 0x100).substring(1, 3));

            return str.toString();
        } catch (Exception e) {
            return "Errore: " + e.getMessage();

        }
    }
}
