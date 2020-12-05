package com.jiongzai.pay.util.security;

import com.jiongzai.pay.util.security.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class AES {
    public static final String EncryptMode = "ECB";

    public static String AES_Encrypt(Object plaintext, String Key) {
        String PlainText = null;
        try {
            PlainText = plaintext.toString();
            if (Key == null) {
                return null;
            }
            byte[] raw = Key.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(1, skeySpec);
            byte[] encrypted = cipher.doFinal(PlainText.getBytes("utf-8"));
            String encryptedStr = Base64.encode(encrypted);
            return encryptedStr;
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }


    public static String AES_Decrypt(Object cipertext, String Key) {
        String CipherText = null;
        try {
            CipherText = cipertext.toString();

            if (Key == null) {
                return null;
            }
            byte[] raw = Key.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            cipher.init(2, skeySpec);

            byte[] encrypted1 = Base64.decode(CipherText);

            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original, "utf-8");
                return originalString;
            } catch (Exception e) {
                System.out.println(e.toString());
                return null;
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            return null;
        }
    }

    public static void main(String[] args) {
    }
}
