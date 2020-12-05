package com.jiongzai.pay.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.jiongzai.pay.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.jiongzai.pay.common.Result;
import com.jiongzai.pay.config.UnicomConfig;
import com.jiongzai.pay.model.CardBean;
import com.jiongzai.pay.model.MerchantBean;
import com.jiongzai.pay.model.MessageBean;
import com.jiongzai.pay.model.OrderBean;
import com.jiongzai.pay.util.HttpUtil;
import com.jiongzai.pay.util.http.Http;
import com.jiongzai.pay.util.http.Http.Response;
import com.jiongzai.pay.util.json.JSONUtil;
import com.jiongzai.pay.util.security.Base64;
import com.jiongzai.pay.util.security.MD5;

import net.sf.json.JSONObject;


@Controller
@RequestMapping("/api")
public class PayController {

	private static final Logger log = LogManager.getLogger(PayController.class);

	@Autowired
	private OrderService orderService;

	@Autowired
	private MerchantService merchantService;

	@Autowired
	private CardService cardService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private ConfigService configService;

	@ResponseBody
	@RequestMapping(value = "/pay")
	public Result pay(HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		String merchant_no=request.getParameter("merchant_no");
		String out_order_no=request.getParameter("out_order_no");
		String amount=request.getParameter("amount");
		String pay_type=request.getParameter("pay_type");
		String notify_url=request.getParameter("notify_url");
		String sign=request.getParameter("sign");
		Result result = new Result();
		if (StringUtils.isEmpty(merchant_no)) {
			result.setCode(101);
			result.setMsg("参数缺失merchant_no");
			result.setData("");
			return result;
		}
		if (StringUtils.isEmpty(out_order_no)) {
			result.setCode(102);
			result.setMsg("参数缺失out_order_no");
			result.setData("");
			return result;
		}
		if (StringUtils.isEmpty(amount)) {
			result.setCode(103);
			result.setMsg("参数错误amount");
			result.setData("");
			return result;
		}
		String[] array = {"20","30","50","100","200","300"};
		if(!Arrays.asList(array).contains(amount))
		{
			result.setCode(104);
			result.setMsg("amount只能为20,30,50,100,200,300");
			result.setData("");
			return result;
		}
		if (StringUtils.isEmpty(pay_type)) {
			result.setCode(105);
			result.setMsg("参数缺失pay_type");
			result.setData("");
			return result;
		}
		if(!"wechat".equals(pay_type)&&!"alipay".equals(pay_type))
		{
			result.setCode(106);
			result.setMsg("pay_type只能为alipay或wechat");
			result.setData("");
			return result;
		}
		if (StringUtils.isEmpty(notify_url)) {
			result.setCode(107);
			result.setMsg("参数缺失notify_url");
			result.setData("");
			return result;
		}
		if (StringUtils.isEmpty(sign)) {
			result.setCode(108);
			result.setMsg("参数缺失sign");
			result.setData("");
			return result;
		}
		try {
			MerchantBean merchant = merchantService.getById(Long.parseLong(merchant_no));
			if(merchant==null)
			{
				result.setCode(201);
				result.setMsg("商户不存在");
				result.setData("");
				return result;
			}
			if(merchant.getStatus()==0)
			{
				result.setCode(202);
				result.setMsg("商户被禁用");
				result.setData("");
				return result;
			}
			String signstr=merchant_no+out_order_no+amount+pay_type+notify_url;
			log.info("signstr:"+signstr);
			String md5=MD5.getMD5(signstr+merchant.getPay_key());
			log.info("md5:"+md5);
			if(!sign.equals(md5))
			{
				result.setCode(203);
				result.setMsg("签名错误");
				result.setData("");
				return result;
			}
			return orderService.pay(request, out_order_no, pay_type, amount, merchant.getId(), notify_url);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setCode(500);
			result.setMsg("server error");
			result.setData("");
			return result;
		}
	}

	@ResponseBody
	@RequestMapping(value = "/query")
	public Result query(HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		String merchant_no=request.getParameter("merchant_no");
		String out_order_no=request.getParameter("out_order_no");
		String sign=request.getParameter("sign");
		Result result = new Result();
		if (StringUtils.isEmpty(merchant_no)) {
			result.setCode(101);
			result.setMsg("参数缺失merchant_no");
			result.setData("");
			return result;
		}
		if (StringUtils.isEmpty(out_order_no)) {
			result.setCode(102);
			result.setMsg("参数缺失out_order_no");
			result.setData("");
			return result;
		}
		if (StringUtils.isEmpty(sign)) {
			result.setCode(108);
			result.setMsg("参数缺失sign");
			result.setData("");
			return result;
		}
		try {
			MerchantBean merchant = merchantService.getById(Long.parseLong(merchant_no));
			if(merchant==null)
			{
				result.setCode(201);
				result.setMsg("商户不存在");
				result.setData("");
				return result;
			}
			if(merchant.getStatus()==0)
			{
				result.setCode(202);
				result.setMsg("商户被禁用");
				result.setData("");
				return result;
			}
			String signstr=merchant_no+out_order_no;
			String md5=MD5.getMD5(signstr+merchant.getPay_key());
			if(!sign.equals(md5))
			{
				result.setCode(203);
				result.setMsg("签名错误");
				result.setData("");
				return result;
			}
			OrderBean queryModel=new OrderBean();
			queryModel.setMerchant_order_no(out_order_no);
			List<OrderBean> orderList=orderService.queryList(queryModel);
			if(orderList==null||orderList.size()==0)
			{
				result.setCode(204);
				result.setMsg("订单不存在");
				result.setData("");
				return result;
			}
			OrderBean order=orderList.get(0);
			Map<String,String> map=new HashMap<String,String>();
			map.put("merchant_no", order.getMerchant_id().toString());
			map.put("order_no", order.getId().toString());
			map.put("out_order_no", order.getMerchant_order_no());
			map.put("amount", order.getAmount().toString());
			map.put("pay_type", order.getPay_type());
			map.put("code", order.getStatus().toString());
			result.setCode(1);
			result.setMsg("请求成功");
			result.setData(map);
			return result;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			result.setCode(500);
			result.setMsg("server error");
			result.setData("");
			return result;
		}
	}



	@RequestMapping(value = "/payPage.html")
	public ModelAndView payPage(HttpServletRequest request,HttpServletResponse response,PrintWriter out,ModelAndView mav) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		String id=request.getParameter("id");
		Result result = new Result();
		if(StringUtils.isEmpty(id))
		{
			result.setCode(101);
			result.setMsg("订单不存在");
			result.setData("");
			out.write(JSONUtil.toJson(result));
			return null;
		}
		OrderBean order=orderService.getById(Long.parseLong(id));
		if (order==null) {
			result.setCode(102);
			result.setMsg("订单不存在");
			result.setData("");
			out.write(JSONUtil.toJson(result));
			return null;
		}
		long leftTime=(order.getCreate_time()+180)-System.currentTimeMillis()/1000;
		mav.addObject("status", order.getStatus());
		mav.addObject("amount", order.getAmount());
		mav.addObject("leftTime", leftTime);
		mav.addObject("payType", order.getPay_type());
		//mav.addObject("qrUrl", HttpUtil.getServerPath(request)+"api/qrUrl.html?id="+id);
		if("wechat".equals(order.getPay_type()))
		{
			//if(order.getPay_url().startsWith("weixin:"))
			mav.addObject("wechat_safe_flag", 0);
			//else
			//	mav.addObject("wechat_safe_flag", 1);
			String payUrl=order.getPay_url().replace("https://wx.tenpay.com/", HttpUtil.getServerPath(request));
			mav.addObject("payUrl", payUrl);
		}
		else
		{
			String payUrl = HttpUtil.getServerPath(request)+"api/ali/"+order.getId();
			//只开苹果h5模式
			String first_dispatch_type = configService.getValueByKey("first_dispatch_type");
			String payH5Url_session = "";
			if("2".equals(first_dispatch_type)){
				String sdk = "unipay://alipay/"+order.getPay_url();
				Map<String ,String> mp = new HashMap<String, String>();
				mp.put("sdk", sdk);
				String body = HttpUtil.post("http://106.53.28.25/main/session", mp);
				if(!StringUtils.isEmpty(body)){
					if(!"error".equals(body)){
						//	payUrl = "https://mclient.alipay.com/cashierRoutePay.htm?route_pay_from=h5&init_from=SDKLite&session="+body+"&utdid=&tid=&cc=y";
						payH5Url_session = body;
						payUrl ="https://wappaygw.alipay.com/h5Continue.htm?h5_route_token="+body;
					}
				}
			}
			mav.addObject("payH5Url_session", payH5Url_session);
			mav.addObject("payUrl",  payUrl);
		}
		mav.setViewName(order.getPay_type());
		return mav;
	}

	@RequestMapping(value = "/alipay.html")
	public ModelAndView alipay(HttpServletRequest request,HttpServletResponse response,PrintWriter out,ModelAndView mav) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		String id=request.getParameter("id");
		Result result = new Result();
		if(StringUtils.isEmpty(id))
		{
			result.setCode(101);
			result.setMsg("订单不存在");
			result.setData("");
			out.write(JSONUtil.toJson(result));
			return null;
		}
		OrderBean order=orderService.getById(Long.parseLong(id));
		String payUrl = order.getPay_url();
		try {
			response.setHeader("Content-type", "text/html;charset=UTF-8");
			response.getWriter().append(payUrl).close();
		} catch (Exception e) {
			log.error("同步网关响应html异常", e);
		}
		return null;
	}

	@RequestMapping(value = "/qrUrl.html")
	public ModelAndView qrUrl(HttpServletRequest request,HttpServletResponse response,PrintWriter out,ModelAndView mav) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		String id=request.getParameter("id");
		Result result = new Result();
		if(StringUtils.isEmpty(id))
		{
			result.setCode(101);
			result.setMsg("订单不存在");
			result.setData("");
			out.write(JSONUtil.toJson(result));
			return null;
		}
		OrderBean order=orderService.getById(Long.parseLong(id));
		if (order==null) {
			result.setCode(102);
			result.setMsg("订单不存在");
			result.setData("");
			out.write(JSONUtil.toJson(result));
			return null;
		}
		mav.setViewName("redirect:alipays://platformapi/startApp?appId=20000125&orderSuffix="+order.getPay_url()+"#Intent;scheme=alipays;package=com.tencent.mm;end");
		return mav;
	}

	@ResponseBody
	@RequestMapping(value = "/testPay.html")
	public Result testPay(HttpServletRequest request,HttpServletResponse response) throws IOException {
		response.setContentType("text/html;charset=utf-8");
		Result result=new Result();
		String merchant_no="10000";
		String out_order_no=""+System.currentTimeMillis()/1000;
		String amount=request.getParameter("amount");
		String pay_type=request.getParameter("pay_type");
		String notify_url=HttpUtil.getServerPath(request)+"api/testNotifyUrl.html";
		String sign=MD5.getMD5(merchant_no+out_order_no+amount+pay_type+notify_url+"1b7f1d545af6b368e65b0c2c016b9926");
		Map<String,String> param=new HashMap<String,String>();
		param.put("merchant_no", merchant_no);
		param.put("out_order_no", out_order_no);
		param.put("amount", amount);
		param.put("pay_type", pay_type);
		param.put("notify_url", notify_url);
		param.put("sign", sign);
		Response res=Http.create(HttpUtil.getServerPath(request)+"api/pay").body(param).requestType(Http.RequestType.FORM).post().send().getResponse();
		if(StringUtils.isEmpty(res.getResult()))
		{
			result.setCode(0);
			result.setMsg("网络异常");
			result.setData("");
			return result;
		}
		JSONObject json=JSONObject.fromObject(res.getResult());
		if(json.getInt("code")==1)
		{
			result.setCode(json.getInt("code"));
			result.setMsg(json.getString("msg"));
			result.setData(json.getJSONObject("data").getString("pay_url"));
			return result;
		}
		result.setCode(json.getInt("code"));
		result.setMsg(json.getString("msg"));
		result.setData("");
		return result;
	}

	@RequestMapping(value = "/testPayPage.html")
	public ModelAndView testPayPage(HttpServletRequest request,HttpServletResponse response,PrintWriter out,ModelAndView mav,String pay_type) throws IOException {
		mav.setViewName("testPay");
		return mav;
	}


	@ResponseBody
	@RequestMapping(value = "/testNotifyUrl.html")
	public String testNotifyUrl(HttpServletRequest request,HttpServletResponse response) throws IOException {
		return "success";
	}

	/**
	 * 删除所有的HTML标签
	 *
	 * @param source 需要进行除HTML的文本
	 * @return
	 */
	public  String deleteAllHTMLTag(String source) {

		if(source == null) {
			return "";
		}

		String s = source;
		/** 删除普通标签  */
		s = s.replaceAll("<(S*?)[^>]*>.*?|<.*? />", "");
		/** 删除转义字符 */
		s = s.replaceAll("&.{2,6}?;", "");
		return s;
	}

	@ResponseBody
	@RequestMapping(value = "/receiveSmsCode.html")
	public String receiveSmsCode(HttpServletRequest request,HttpServletResponse response) throws IOException {
		String phone=request.getParameter("phone");
		String content=request.getParameter("content");
		content = deleteAllHTMLTag(content);
		log.info(phone+",接收到短信："+content);
		if(StringUtils.isEmpty(phone)||StringUtils.isEmpty(content))
		{
			return "手机号或短信为空";
		}
		if(!content.contains("中国联通"))
		{
			return "不是联通短信";
		}

		CardBean card = new CardBean();
		card.setCard_no(phone);
		List<CardBean> cards = cardService.queryList(card);
		if(cards.size() == 0){
			return "号码不存在，不能接受此短信";
		}

		MessageBean msg=new MessageBean();
		msg.setPhone(phone);
		msg.setContent(content);
		msg.setCreate_time(System.currentTimeMillis()/1000);
		msg.setType(0);
		messageService.insert(msg);

		String verCode="";
		Pattern pattern = Pattern.compile("\\d{6}");
		Matcher matcher = pattern.matcher(content);
		if (matcher.find()) {
			verCode=matcher.group();
			if(content.contains("登录随机码"))//web登录燕子矶
			{
				cardService.updateWebVerCodeByPhone(phone,verCode);
			}
			else if(content.contains("查获取充值卡信息"))//获取卡密验证码
			{
				orderService.updateGetCdkSmsBySendTime(phone, verCode);
			}
			return "success";
		}
		pattern = Pattern.compile("\\d{4}");
		matcher = pattern.matcher(content);
		if (matcher.find()) {//app登录验证码
			verCode=matcher.group();
			cardService.updateVerCodeByPhone(phone,verCode);
			return "success";
		}
		return "success";
	}



}
