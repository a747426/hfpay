package com.jiongzai.pay.config;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.util.StringUtils;

import com.jiongzai.pay.util.http.Http;
import com.jiongzai.pay.util.http.Http.Response;

public class UnicomConfig {
	
	public static final String deviceOS="android6.0.1";
	
	public static final String version="android@6.0100";

	public static final String deviceModel="DUK-AL20";
	
	public static final String appId="1f7af72ad6912d306b5053abf90c7ebbf423b29d1f12b394b0e7db4892784e14f8b9e35bec202986ea9951c841b74692e6cb554ced26d0ce20b3879b37764f21";
	
	public static final String deviceBrand="HUAWEI";
	
	public static final String deviceCode="867993020561013";
	
	public static final String User_Agent="Mozilla%2f5.0+(Linux%3b+Android+5.1.1%3b+DUK-AL20+Build%2fLMY48Z)+AppleWebKit%2f537.36+(KHTML%2c+like+Gecko)+Version%2f4.0+Chrome%2f39.0.0.0+Safari%2f537.36%3b+unicom%7bversion%3aandroid%406.0100%2cdesmobile%3a13076540194%7d%3bdevicetype%7bdeviceBrand%3aHUAWEI%2cdeviceModel%3aDUK-AL20%7d";
	
	public static final String Web_User_Agent="Mozilla%2f5.0+(Windows+NT+10.0%3b+WOW64)+AppleWebKit%2f537.36+(KHTML%2c+like+Gecko)+Chrome%2f80.0.3987.162+Safari%2f537.36";
			
	public static final String SEND_LOGIN_SMS_SERVER="https://m.client.10010.com/mobileService/sendRadomNum.htm";
	
	public static final String CARD_LOGIN_SERVER="https://m.client.10010.com/mobileService/radomLogin.htm";
	
	public static final String LOGIN_ONLINE_SERVER="https://m.client.10010.com/mobileService/onLine.htm";
	
	public static final String GET_UOP_ID_SERVEER="https://m.client.10010.com/mobileService/openPlatform/openPlatLine.htm?to_url=https://upay.10010.com/npfwap/npfMobWap/buycard/init";
	
	public static final String BUY_CARD_CHECK_SERVER="http://upay.10010.com/npfwap/NpfMob/buyCard/buyCardCheck.action";
	
	public static final String BANK_CHARGE_CHECK_SERVER="https://upay.10010.com/npfwap/NpfMob/mobAppBankCharge/bankChargeCheck.action";
	
	public static final String BANK_CHARGE_SUBMIT_SERVER="https://upay.10010.com/npfwap/NpfMob/mobAppBankCharge/bankChargeSubmit.action";
	
	public static final String BUY_CARD_SUBMIT_SERVER="https://upay.10010.com/npfwap/NpfMob/buyCard/buyCardSubmit.action";
	
	public static final String GET_PAY_URL_SERVER="https://unipay.10010.com/udpNewPortal/miniwappayment/miniWapPaymentAffirm";
	
	public static final String GET_WECHAT_PRE_SERVER="http://unipay.10010.com/udpNewPortal/miniwappayment/miniWapPaymentChoose";
	
	public static final String ORDER_REPAY_SERVER="http://upay.10010.com/npfwap/NpfMobAppQuery/Repay/repaySubmit.action";
	
	public static final String QUERY_BUY_CARD_ORDER_SERVER="https://upay.10010.com/npfwap/NpfMobAppQuery/feeSearch/queryBuyCardOrder.action";
	
	public static final String QUERY_BANK_CHARGE_ORDER_SERVER="https://upay.10010.com/npfwap/NpfMobAppQuery/feeSearch/queryOrder.action";
	
	public static final String DEL_ORDER_SERVER="https://upay.10010.com/npfwap/NpfMobAppQuery/CancelOrder/delOrder.action";
	
	public static final String WEB_DEL_ORDER_SERVER="https://upay.10010.com/npfweb/NpfQueryWeb/CancelOrder/delOrder.action";
	
	public static final String GET_CKUUID_SERVER="https://uac.10010.com/portal/Service/CheckNeedVerify";
	
	public static final String WEB_QUERY_BUY_CARD_ORDER_SERVER="https://upay.10010.com/npfweb/NpfQueryWeb/buycardSearch/BuyCardSearchNew";
	
	public static final String WEB_BUY_CARD_CHECK_SERVER="https://upay.10010.com/npfweb/NpfWeb/buyCard/buyCardCheck";
	
	public static final String WEB_BUY_CARD_SUBMIT_SERVER="https://upay.10010.com/npfweb/NpfWeb/buyCard/buyCardSubmit";
	
	public static final String WEB_CARD_LOGIN_SERVER="https://uac.10010.com/portal/Service/MallLogin";
	
	public static final String WEB_SEND_LOGIN_SMS_SERVER="https://uac.10010.com/portal/Service/SendMSG";
	
	public static final String WEB_LOGIN_ONLINE_SERVER="https://uac.10010.com/portal/homeLoginNew";
	
	public static final String WEB_SEND_GET_CDKEY_SERVER="https://upay.10010.com/npfweb/NpfQueryWeb/buycarddownload/SendPhoneVerifyCode";
	
	public static final String WEB_GET_CDKEY_EXCEL_SERVER="https://upay.10010.com/npfweb/NpfQueryWeb/buycarddownload/CardListToExcel";
	
	public static final String Wap_User_Agent="Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.132 Mobile Safari/537.36";
	
	public static final String WAP_CHARGE_INDEX_URL="http://upay.10010.com/npfwap/npfMobWap/bankcharge/index.html";
	
	public static final String WAP_CHARGE_CHECK_URL="http://upay.10010.com/npfwap/NpfMob/mobWapBankCharge/wapBankChargeCheck.action";
	
	
	public static void main(String[] args) {
		String basePayUrl="https://unipay.10010.com/udpNewPortal/miniwappayment/miniWapPaymentChoose?ServicePlatID=10&PayReqObj=kje7veI4XTdW2PMBjJejQnzyB92SZzmhY%2BX5ZCrAJiXY0Q%2BrKdjyDjLTAlw%2BoEbpu5V9iVL14LasI0gOOmQz1DA1dXK1arMayi34r7BOfFPKiumFZVkj2JbqQM92f3aCQmUK9YGYAbAm1Oq%2BOuyIBus8Zs2jwRxzMnfUtHNPHPgJ6JYBz%2F8mowVCBZ4lw%2B71p8ls2HQHsYESNMpd5E%2Bg0M5Xaixiw7azGFGNza0dMd0vT6duaJcQczUDoHmzvOFhoecUh2SQCi%2BVOtoPLmTJy6lO0zyiF3UNiOvgjom8mWDvLHLU5Wu4TJew42cRyqOlW4jrtAgBN%2FUWn2YBEcEfUorq8GAiaUpsqsCpNXP5INxHp69uj7mkIPhtAJwRqIu%2BIb0K7%2FOv%2FFedr6W1fP60pSthEsuisCe%2F7LbUgOGDpH4xra%2BqrYqOcXCM9ej0o9l2Xp%2B%2BgPaZrlQObbX%2BghlpqTLzLzdJRr9lQEyw0hqVnFzykrurdHBF92e%2BDL%2BkcnSWRxPEHm6Ta0aRRVwP9jPXhKoiva0VDRQ%2BcwF6I3AuTr%2B95Gz6JMYAGI%2Fpgam8r6QEAT6OFfSDAE2Z52D9fyJDO2na34tyM9P%2FjBb7tRr92Id%2FD7d%2BYmN7nL3BiFw1kbZsSZhJOeVGJn3fJ5DRQ31s5oXLQcpzQVuuM%2BcRFmqyinFljQMg8ZTh5NgPgYSmSpLqiOdD%2FC%2FNvVUWC3%2BlBTopLAK83PyzO%2BSzMASQ6O85NsbUWRBWVFP66EGVa3VESw8jAyZ%2B%2FQAZHQg1gRoVeuf12FgVi%2BmSzN6xWc0C3BDKIAtOH%2BwzqywdsR6c11ComrwPMtVZeSrEkzV%2FHLT%2BEiQN%2FKA%2BrKRlPs48DUMaZwim2T6UnOzjCzO685dOy2N81%2BJawJW2k996vgJydldxPF36gwtHKWgv5FNhOKAOOV0LS8DQ4yABINnJQQ2hLNMWP9t0nJa5UqLo%2FA%2FfnloOVur%2BBpHQ%2FbY%2BFAAXvRNXQ1%2BMn5Voh%2FfTsDd0qV8BnauYrVr56Px11Mat2EjGjOvfeQ5BHM%2FpXCwroNVt54ZvlezY720vLhOWw2eLTqfeFfYABsExM6GkfoRQ5fm0UzVIKaHh5Jh5NonIzBAhv7f4nbw4R76KAzUIVI0exhefTwKWIA4Z2DRRegOPbP76qZ8HCS%2BL9yAdYM28yfdn%2BVGpHm6BIVG%2B0bZBO8vyJiVvOAASOQ6KxrufbmPCRZLRq7LEXaIevQ9rj2zcAFaqtpwriFLUDR4%3D&pageType=07";
		String enChannelStr="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";
		Response res = Http.create(basePayUrl).requestType(Http.RequestType.FORM).get().send().getResponse();
		String htmlResponse=res.getResult();
		if(res.getStatus()==0||StringUtils.isEmpty(htmlResponse))return;
		Map<String,String> headers=new HashMap<String,String>();
		headers.put("Referer", basePayUrl);
		headers.put("User-Agent", UnicomConfig.User_Agent);
		Map<String,String> params=new HashMap<String,String>();
		String pattern = "<input[\\s\\S]*?type=\"hidden\"[\\s\\S]*?name=\"(.*?)\"[\\s\\S]*?value=\"(.*?)\"";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(htmlResponse);
		while (m.find()) {
			params.put(m.group(1), m.group(2));
		}
		//params.put("bankSelect","alipayApp&PwkANbkAVfM=&07");
		params.put("bankSelect","alipaywap&PwkANbkAVfM=&01");
		if(params.get("enServiceOrder")==null)
		{
			return;
		}
		params.put("enChannelStr", enChannelStr);
		res=Http.create(UnicomConfig.GET_PAY_URL_SERVER).heads(headers).redirect(false).heads(headers).cookies("MUT_V="+UnicomConfig.version).body(params).requestType(Http.RequestType.FORM).post().send().getResponse();
		if(res.getStatus()==0||res.getHeads().get("Location")==null)return;
		System.out.println(res.getHeads().get("Location").toString());
	}
	
}
