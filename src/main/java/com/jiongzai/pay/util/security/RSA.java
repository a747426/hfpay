package com.jiongzai.pay.util.security;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.commons.io.IOUtils;

public class RSA {
	
	 public static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDc+CZK9bBA9IU+gZUOc6FUGu7yO9WpTNB0PzmgFBh96Mg1WrovD1oqZ+eIF4LjvxKXGOdI79JRdve9NPhQo07+uqGQgE4imwNnRx7PFtCRryiIEcUoavuNtuRVoBAm6qdB0SrctgaqGfLgKvZHOnwTjyNqjBUxzMeQlEC2czEMSwIDAQAB";
	 
	 public static String encrypt(String data, String publicKeyPath) throws Exception{
		 	KeyFactory keyFactory = KeyFactory.getInstance("RSA");
	        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decode(publicKey));
	        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
	        //RSA加密
	        Cipher cipher = Cipher.getInstance("RSA");
	        cipher.init(Cipher.ENCRYPT_MODE, key);
	        return Base64.encode(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes("UTF-8"), key.getModulus().bitLength()));
	  }
	 
	 private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) {
	        int maxBlock = 0;  //最大块
	        if (opmode == Cipher.DECRYPT_MODE) {
	            maxBlock = keySize / 8;
	        } else {
	            maxBlock = keySize / 8 - 11;
	        }
	        ByteArrayOutputStream out = new ByteArrayOutputStream();
	        int offSet = 0;
	        byte[] buff;
	        int i = 0;
	        try {
	            while (datas.length > offSet) {
	                if (datas.length - offSet > maxBlock) {
	                    //可以调用以下的doFinal（）方法完成加密或解密数据：
	                    buff = cipher.doFinal(datas, offSet, maxBlock);
	                } else {
	                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
	                }
	                out.write(buff, 0, buff.length);
	                i++;
	                offSet = i * maxBlock;
	            }
	        } catch (Exception e) {
	            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
	        }
	        byte[] resultDatas = out.toByteArray();
	        IOUtils.closeQuietly(out);
	        return resultDatas;
	    }
	 
	 public static String unicomEncryption(String str) throws Exception
	 {
		return URLEncoder.encode(RSA.encrypt(str+PassWrodsCreater.generateRandomNumber(6), RSA.publicKey));
	 }
}
