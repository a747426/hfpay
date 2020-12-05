package com.jiongzai.pay.util.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 描述：<p>加密字符串</p>
 * @author xiejiong
 */
public final class MD5 {
	private MD5() {
	}

	/**
	 * 获取input对应的MD5代码
	 * @param input
	 * @return
	 */
	public static final String getMD5(String input) {
		try {
			byte[] inputByte = input.getBytes();
			MessageDigest md;

			md = MessageDigest.getInstance("md5");

			md.update(inputByte);
			byte[] digest = md.digest();
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < digest.length; i++) {
				int val = ((int) digest[i]) & 0xff;
				if (val < 16) {
					buf.append("0");
				}
				buf.append(Integer.toHexString(val));
			}
			return buf.toString();
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	public static final String getMD516(String input){
		String md5 = getMD5(input);
		return md5.substring(8,24);
	}
	public static final String getMD532(String input){
		return getMD5(input);
	}
	public static void main(String[] args) {
		System.out.println(getMD5("123456"));
	}
}
