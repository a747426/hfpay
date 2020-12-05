package com.jiongzai.pay.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.jiongzai.pay.config.UnicomConfig;
import com.jiongzai.pay.util.HttpUtil;
import com.jiongzai.pay.util.http.Http;
import com.jiongzai.pay.util.http.Http.Response;

@Controller
public class WxController {

	@RequestMapping("/cgi-bin/mmpayweb-bin/checkmweb")
	public void listPage(HttpServletRequest request, HttpServletResponse response, ModelAndView mav) {
		String param=request.getQueryString();
		String ip=HttpUtil.getIpAddr(request);
		Map<String,String> headers=new HashMap<String,String>();
		headers.put("Referer", "https://unipay.10010.com/udpNewPortal/miniwappayment/miniWapPaymentChoose");
		headers.put("User-Agent", UnicomConfig.User_Agent);
		headers.put("HTTP_VIA",ip);
		headers.put("HTTP_X_FORWARDED_FOR",ip);
		headers.put("x-forwarded-for",ip);
		headers.put("Proxy-Client-IP",ip);
		 Response res=Http.create("https://wx.tenpay.com/cgi-bin/mmpayweb-bin/checkmweb?"+param).heads(headers).requestType(Http.RequestType.FORM).
				  get().send().getResponse(); 
		 String body = res.getResult();
		 OutputStream os=null;
		try {
			os=response.getOutputStream();
			os.write(body.getBytes());
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(os!=null) {
				try {
					os.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
}
