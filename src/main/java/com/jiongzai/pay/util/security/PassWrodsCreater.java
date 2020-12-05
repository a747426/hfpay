package com.jiongzai.pay.util.security;

import java.util.Random;
import java.util.UUID;
 
public class PassWrodsCreater {
 
	public static String generatePassword (int length) {
        // 最终生成的密码
        String password = "";
        Random random = new Random();
        for (int i = 0; i < length; i ++) {
            // 随机生成0或1，用来确定是当前使用数字还是字母 (0则输出数字，1则输出字母)
            int charOrNum = random.nextInt(2);
            if (charOrNum == 1) {
                // 随机生成0或1，用来判断是大写字母还是小写字母 (0则输出小写字母，1则输出大写字母)
                int temp = random.nextInt(2) == 1 ? 65 : 97;
                password += (char) (random.nextInt(26) + temp);
            } else {
                // 生成随机数字
                password += random.nextInt(10);
            }
        }
        return password;
    }
	
	public static String generateRandomNumber(int length) {
        // 最终生成的密码
		 String charList = "0123456789";
	     String rev = "";
	     Random f = new Random();
	     for(int i=0;i<length;i++)
	     {
	        rev += charList.charAt(Math.abs(f.nextInt())%charList.length());
	     }
	     return rev;
    }
	
	 public static String getUUIDStr() {
	        String s = UUID.randomUUID().toString();
	        //去掉“-”符号
	        return s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24);
	 }
	
	 public static String getUUID() {
	        //随机生成一位整数
	        int random = (int) (Math.random() * 9 + 1);
	        String valueOf = String.valueOf(random);
	        //生成uuid的hashCode值
	        int hashCode = UUID.randomUUID().toString().hashCode();
	        //可能为负数
	        if (hashCode < 0) {
	            hashCode = -hashCode;
	        }
	        return valueOf + String.format("%015d", hashCode);
	    }
	
	

    public static void main(String[] args) {
        System.out.println(PassWrodsCreater.getUUIDStr());
    }
 
}
