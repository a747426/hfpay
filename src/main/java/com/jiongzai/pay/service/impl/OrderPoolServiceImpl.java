package com.jiongzai.pay.service.impl;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jiongzai.pay.model.OrderBean;
import net.sf.json.JSONArray;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiongzai.pay.config.UnicomConfig;
import com.jiongzai.pay.dao.CardDao;
import com.jiongzai.pay.dao.OrderPoolDao;
import com.jiongzai.pay.model.CardBean;
import com.jiongzai.pay.model.OrderPoolBean;
import com.jiongzai.pay.service.ConfigService;
import com.jiongzai.pay.service.OrderPoolService;
import com.jiongzai.pay.util.ThreadPoolUtils;
import com.jiongzai.pay.util.http.Http;
import com.jiongzai.pay.util.http.Http.Response;
import com.jiongzai.pay.util.json.JSONUtil;
import com.jiongzai.pay.util.security.PassWrodsCreater;

import net.sf.json.JSONObject;

@Service
public class OrderPoolServiceImpl implements OrderPoolService {

	private static final Logger logger = LogManager.getLogger(OrderPoolServiceImpl.class);

	@Autowired
	private OrderPoolDao dao;

	@Autowired
	private CardDao cardDao;

	@Autowired
	private ConfigService configService;

	@Override
	public int insert(OrderPoolBean t) {
		// TODO Auto-generated method stub
		return dao.insert(t);
	}

	@Override
	public int deleteById(Long id) {
		// TODO Auto-generated method stub
		return dao.deleteById(id);
	}

	@Override
	public List<OrderPoolBean> queryList(OrderPoolBean t) {
		// TODO Auto-generated method stub
		return dao.queryList(t);
	}

	@Override
	public int update(OrderPoolBean t) {
		// TODO Auto-generated method stub
		return dao.update(t);
	}

	@Override
	public OrderPoolBean getById(Long Id) {
		// TODO Auto-generated method stub
		return dao.getById(Id);
	}

	@Override
	public long count(OrderPoolBean t) {
		// TODO Auto-generated method stub
		return dao.count(t);
	}

	@Override
	public PageInfo<OrderPoolBean> pageList(OrderPoolBean t, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<OrderPoolBean> list = dao.queryList(t);
		PageInfo<OrderPoolBean> pageInfo = new PageInfo<OrderPoolBean>(list);
		return pageInfo;
	}

	@Override
	public int removeById(Long id) {
		// TODO Auto-generated method stub
		return dao.removeById(id);
	}

	@Override
	public List<OrderPoolBean> queryAll() {
		// TODO Auto-generated method stub
		return dao.queryAll();
	}

	@Override
	public OrderPoolBean getOneCanUseOrder(String pay_type, Integer amount) {
		// TODO Auto-generated method stub
		return dao.getOneCanUseOrder(pay_type, amount);
	}

	//静态变量控制不要重复产码
	public static Integer flg = 0;

	@Override
	public void cardGetNewOrderTask() throws Exception {
		// TODO Auto-generated method stub
		int ready_order_status = Integer.parseInt(configService.getValueByKey("ready_order_flag"));
		if (ready_order_status != 1) return;
		List<CardBean> canUseCards = cardDao.getCanDispatchOrderList();
		if (canUseCards.size() == 0) {
			logger.info("暂无可下单手机号，无法下单");
			return;
		}
		int ready_20order_num = Integer.parseInt(configService.getValueByKey("ready_20order_num"));
		int ready_30order_num = Integer.parseInt(configService.getValueByKey("ready_30order_num"));
		int ready_50order_num = Integer.parseInt(configService.getValueByKey("ready_50order_num"));
		int ready_100order_num = Integer.parseInt(configService.getValueByKey("ready_100order_num"));
		int ready_200order_num = Integer.parseInt(configService.getValueByKey("ready_200order_num"));
		int ready_300order_num = Integer.parseInt(configService.getValueByKey("ready_300order_num"));
		Map<Integer, Integer> amountMap = new HashMap<Integer, Integer>();
		amountMap.put(20, ready_20order_num);
		amountMap.put(30, ready_30order_num);
		amountMap.put(50, ready_50order_num);
		amountMap.put(100, ready_100order_num);
		amountMap.put(200, ready_200order_num);
		amountMap.put(300, ready_300order_num);
		String first_dispatch_type = configService.getValueByKey("first_dispatch_type");
		List<OrderPoolBean> leftOrderPoolList = null;
		leftOrderPoolList = dao.getLeftOrderCountByPayType("alipay");
		for (OrderPoolBean op : leftOrderPoolList) {
			amountMap.put(op.getAmount(), amountMap.get(op.getAmount()) - op.getOrder_count());
		}
		logger.info("需下单数量20:" + amountMap.get(20) + ",30:" + amountMap.get(30) + ",50:" + amountMap.get(50) + ",100:" + amountMap.get(100) + ",200:" + amountMap.get(200) + ",300:" + amountMap.get(300));
		int threadNum = 0;
		int index = 0;
		a:
		for (Map.Entry<Integer, Integer> entry : amountMap.entrySet()) {
			b:
			for (int i = 0; i < entry.getValue(); i++) {
				threadNum++;
				final int currentThreadNum = threadNum;
				if (index >= canUseCards.size()) break a;
				CardBean card = canUseCards.get(index);
				ThreadPoolUtils.getOrderPool().execute(() -> {
					try {
						logger.info("开启下单子线程" + currentThreadNum + ",下单金额:" + entry.getKey());
						if("1".equals(configService.getValueByKey("pay_model"))){
							if(card.getApp_place_order_flag() == 1){
								getAppNewOrder(card.getId(), card.getCard_no(), entry.getKey(), card.get_uop_id_cookie(), card.getEn_channel_str(), card.getProxy_ip(), card.getProxy_port(), card.get_web_login_cookie());
							}else if(card.getWeb_place_order_flag() ==1) {
								getWebNewOrder(card.getId(), card.getCard_no(), entry.getKey(), card.get_uop_id_cookie(), card.getEn_channel_str(), card.get_web_login_cookie(), card.getProxy_ip(), card.getProxy_port());
							}
						}else {
							if (card.getApp_place_order_flag() == 1 && currentThreadNum % 2 == 0) {
								getAppNewOrder(card.getId(), card.getCard_no(), entry.getKey(), card.get_uop_id_cookie(), card.getEn_channel_str(), card.getProxy_ip(), card.getProxy_port(), card.get_web_login_cookie());
							}
							if (card.getWeb_place_order_flag() == 1 && currentThreadNum % 2 == 1) {
								getWebNewOrder(card.getId(), card.getCard_no(), entry.getKey(), card.get_uop_id_cookie(), card.getEn_channel_str(), card.get_web_login_cookie(), card.getProxy_ip(), card.getProxy_port());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					} finally {
						CardBean updateModel = new CardBean();
						updateModel.setBuy_card_last_time(System.currentTimeMillis() / 1000);
						updateModel.setId(card.getId());
						cardDao.update(updateModel);
						logger.info("下单子线程" + currentThreadNum + "已结束！");
					}
				});

				index++;
			}
		}
	}


	@Override
	public boolean getAppNewOrder(Long cardId, String phone, Integer amount, String uopIdCookie, String enChannelStr, String proxyIp, int proxyProt, String webCookie) throws Exception {
		// TODO Auto-generated method stub
		if (StringUtils.isEmpty(uopIdCookie)) {
			logger.info("uopIdCookie为空，无法下单");
			return false;
		}
		String unicomOrderIds = "";
		String unicomOrderId = "";
		Integer faceValue = 0;
		Long createTime = System.currentTimeMillis() / 1000;
		int time = 0;
		while (time < 6) {
			unicomOrderIds = this.getAppUnicomOrderId2(uopIdCookie, proxyIp, proxyProt, "buycard");
			if (!StringUtils.isEmpty(unicomOrderIds)) break;
			time++;
		}
		String orderNo = "";

		CardBean updateModel = new CardBean();
		updateModel.setBuy_card_last_time(Long.valueOf(System.currentTimeMillis() / 1000L));
		updateModel.setId(cardId);
		this.cardDao.update(updateModel);

		if (!StringUtils.isEmpty(unicomOrderIds)) {
			logger.info("联通订单号获取成功：" + unicomOrderIds);
			if(!StringUtils.isEmpty(unicomOrderIds)){
				String[] unicomOrderIdArr = unicomOrderIds.split("!");
				unicomOrderId = unicomOrderIdArr[0];
				faceValue = Integer.parseInt(unicomOrderIdArr[1]);
				createTime = Long.parseLong(unicomOrderIdArr[2]);
				cardDao.addBuyCardCount(cardId);//限制当日购卡次数
			}
		}else{
			orderNo = this.getAppOrderNo(cardId, phone, amount, uopIdCookie, proxyIp, proxyProt);//拿到订单号，去获取支付链接
			if (StringUtils.isEmpty(orderNo)) {
				logger.info("下载预产订单错误为空" + phone);
				return false;
			}

			String basePayUrl = "";
			basePayUrl = this.getBasePayUrl(orderNo, phone, uopIdCookie, proxyIp, proxyProt);
			if (StringUtils.isEmpty(basePayUrl)) {
				logger.info("卡密下单失败:" + orderNo);
				return false;
			}
			logger.info("原生支付链接获取成功");
			unicomOrderIds = this.getAppUnicomOrderId2(uopIdCookie, proxyIp, proxyProt, "buycard");
			if(!StringUtils.isEmpty(unicomOrderIds)){
				String[] unicomOrderIdArr = unicomOrderIds.split("!");
				unicomOrderId = unicomOrderIdArr[0];
				faceValue = Integer.parseInt(unicomOrderIdArr[1]);
				createTime = Long.parseLong(unicomOrderIdArr[2]);
				cardDao.addBuyCardCount(cardId);//限制当日购卡次数
			}
		}
		if(StringUtils.isEmpty(unicomOrderId)){
			logger.info("获取不到联通订单号"+unicomOrderId);
			return false;
		}
		insertOrder(cardId, unicomOrderId, phone,  uopIdCookie, enChannelStr,  proxyIp, proxyProt,
				webCookie,  faceValue,  createTime,  orderNo, "app" );
		return  true;
	}

	@Override
	public boolean getWebNewOrder(Long cardId, String phone, Integer amount, String uopIdCookie, String enChannelStr, String jutCookie,
								  String proxyIp, int proxyProt) throws Exception {
		if (StringUtils.isEmpty(uopIdCookie) || StringUtils.isEmpty(jutCookie)) {
			logger.info("uopIdCookie或loginCookie为空，无法下单");
			return false;
		}

		String orderNo = this.getWebOrderNo(cardId, phone, amount, jutCookie, proxyIp, proxyProt);//拿到订单号，去获取支付链接
		if (StringUtils.isEmpty(orderNo)) {
			return false;
		}
		cardDao.addBuyCardCount(cardId);//限制当日购卡次数

		CardBean updateModel = new CardBean();
		updateModel.setBuy_card_last_time(Long.valueOf(System.currentTimeMillis() / 1000L));
		updateModel.setId(cardId);
		this.cardDao.update(updateModel);

		String unicomOrderId = "";
		int time = 0;
		while (time < 6) {
			unicomOrderId = this.getWebUnicomOrderId(orderNo, phone, jutCookie, proxyIp, proxyProt);
			if (!StringUtils.isEmpty(unicomOrderId)) break;
			time++;
		}

		if (StringUtils.isEmpty(unicomOrderId)) {
			logger.info("web 获取订单号错误"+ orderNo);
			return false;
		}
		insertOrder(cardId, unicomOrderId, phone,  uopIdCookie, enChannelStr,  proxyIp, proxyProt,
				jutCookie,  amount,  System.currentTimeMillis() / 1000,  orderNo, "web" );
		return  true;
	}


	private void insertOrder(Long cardId,String unicomOrderId,String phone, String uopIdCookie,String enChannelStr, String proxyIp,int proxyProt,
							 String webCookie, Integer faceValue, Long createTime, String orderNo,String type ){
		int time;
		String alipay_url = null;//获取支付宝链接
		if("1".equals(configService.getValueByKey("pay_model"))){
			alipay_url = this.getAliPayUrl_newH5(cardId, unicomOrderId, phone, uopIdCookie, enChannelStr, proxyIp, proxyProt); // getSdk(uopIdCookie, proxyIp,proxyProt, basePayUrl);
		}else{
			time = 0;
			while (time < 4) {
				alipay_url = this.getAliPayUrl(cardId, unicomOrderId, phone, webCookie, enChannelStr, proxyIp, proxyProt);
				if (!StringUtils.isEmpty(alipay_url)) break;
				time++;
			}
		}
		if(StringUtils.isEmpty(alipay_url)){
			logger.info("未能生成支付宝订单"+type+ alipay_url);
		}
		//String alipay_url = urls[2];
		if (!StringUtils.isEmpty(alipay_url)) {
			logger.info("支付宝协议获取成功：" +type+ alipay_url);
			OrderPoolBean orderPool1 = new OrderPoolBean();
			orderPool1.setPay_url(alipay_url);
			orderPool1.setPay_type("alipay");
			orderPool1.setWx_h5_return("");
			orderPool1.setAmount(faceValue);
			orderPool1.setCard_id(cardId);
			orderPool1.setCard_no(phone);
			orderPool1.setCreate_time(createTime);
			orderPool1.setUpdate_time(orderPool1.getCreate_time());
			orderPool1.setUnicom_order_id(unicomOrderId);
			orderPool1.setOrder_no(orderNo);
			orderPool1.setOrder_type(type);
			orderPool1.setStatus(0);
			dao.insert(orderPool1);
		}
		dao.updateByUnicomOrderId(unicomOrderId);
	}


	private void setCardValueCode(Map<String, String> param, int face_value) {
		if (face_value == 20) {
			param.put("cardBean.cardValueCode", "01");
			param.put("cardBean.maxCardNum", "15");
		} else if (face_value == 30) {
			param.put("cardBean.cardValueCode", "02");
			param.put("cardBean.maxCardNum", "10");
		} else if (face_value == 50) {
			param.put("cardBean.cardValueCode", "03");
			param.put("cardBean.maxCardNum", "6");
		} else if (face_value == 100) {
			param.put("cardBean.cardValueCode", "04");
			param.put("cardBean.maxCardNum", "3");
		}
	}

	private void setOrderParam(Map<String, String> param, Integer amount) {
		String order_100_cdk_value = configService.getValueByKey("order_100_cdk_value");
		String order_200_cdk_value = configService.getValueByKey("order_200_cdk_value");
		String order_300_cdk_value = configService.getValueByKey("order_300_cdk_value");
		if (amount == 20) {
			param.put("cardBean.cardValue", "20");
			param.put("cardBean.buyCardAmount", "1");
			this.setCardValueCode(param, 20);
		} else if (amount == 30) {
			param.put("cardBean.cardValue", "30");
			param.put("cardBean.buyCardAmount", "1");
			this.setCardValueCode(param, 30);
		} else if (amount == 50) {
			param.put("cardBean.cardValue", "50");
			param.put("cardBean.buyCardAmount", "1");
			this.setCardValueCode(param, 50);
		} else if (amount == 100) {
			if (StringUtils.isEmpty(order_100_cdk_value)) {
				param.put("cardBean.cardValue", "100");
				param.put("cardBean.buyCardAmount", "1");
				this.setCardValueCode(param, 100);
			} else {
				int face_value = Integer.parseInt(order_100_cdk_value.trim());
				int buyCardCount = 100 / face_value;
				param.put("cardBean.cardValue", String.valueOf(face_value));
				param.put("cardBean.buyCardAmount", String.valueOf(buyCardCount));
				this.setCardValueCode(param, face_value);
			}
		} else if (amount == 200) {
			if (StringUtils.isEmpty(order_100_cdk_value)) {
				param.put("cardBean.cardValue", "100");
				param.put("cardBean.buyCardAmount", "2");
				this.setCardValueCode(param, 100);
			} else {
				int face_value = Integer.parseInt(order_200_cdk_value.trim());
				int buyCardCount = 200 / face_value;
				param.put("cardBean.cardValue", String.valueOf(face_value));
				param.put("cardBean.buyCardAmount", String.valueOf(buyCardCount));
				this.setCardValueCode(param, face_value);
			}
		} else if (amount == 300) {
			if (StringUtils.isEmpty(order_300_cdk_value)) {
				param.put("cardBean.cardValue", "100");
				param.put("cardBean.buyCardAmount", "3");
				this.setCardValueCode(param, 100);
			} else {
				int face_value = Integer.parseInt(order_300_cdk_value.trim());
				int buyCardCount = 300 / face_value;
				param.put("cardBean.cardValue", String.valueOf(face_value));
				param.put("cardBean.buyCardAmount", String.valueOf(buyCardCount));
				this.setCardValueCode(param, face_value);
			}
		}
	}


	/**
	 * 获取有用参数
	 *
	 * @param phone
	 * @param unicomOrderId
	 * @return
	 */
	private List<String> getSubmitOrder(String phone, String unicomOrderId, String proxyIp, Integer proxyProt, String uopIdCookie) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://upay.10010.com/npfwap/npfMobWeb/query/index.html?phone=" + phone);
		headers.put("User-Agent", UnicomConfig.User_Agent);
		Response res = Http.create(UnicomConfig.ORDER_REPAY_SERVER + "?orderNo=" + unicomOrderId + "&channelType=308")
				.proxy(proxyIp, proxyProt).heads(headers).cookies(uopIdCookie).requestType(Http.RequestType.FORM).get().send().getResponse();
		String body = res.getResult();
		if (res.getStatus() == 0 || StringUtils.isEmpty(body)) {
			return null;
		}
		if (!JSONUtil.isJson(body)) {
			return null;
		}
		JSONObject json = JSONObject.fromObject(body);
		if (!"success".equals(json.getString("result"))) {
			return null;
		}
		String basePayUrl = json.getString("payUrl");
		res = Http.create(basePayUrl).proxy(proxyIp, proxyProt).requestType(Http.RequestType.FORM).get().send().getResponse();
		String htmlResponse = res.getResult();
		uopIdCookie = basePayUrl;
		List<String> returnStr = new ArrayList<>();
		returnStr.add(htmlResponse);
		returnStr.add(basePayUrl);
		return returnStr;
	}


	private String getAliPayUrl(Long cardId, String unicomOrderId, String phone, String uopIdCookie, String enChannelStr, String proxyIp, int proxyProt) {
		String alipay_url = null;
		Map<String, String> headers = new HashMap<String, String>();
		logger.info("pc支付宝进入");
		headers.put("Referer", "https://upay.10010.com/npfweb/npfqueryweb/fee_search.htm");
		headers.put("User-Agent", UnicomConfig.Web_User_Agent);
		String url = "https://upay.10010.com/npfweb/NpfQueryWeb/Repay/repaySubmit.action?orderNo=" + unicomOrderId + "&channelType=101";
		Response res = Http.create(url)
				.proxy(proxyIp, proxyProt).heads(headers).cookies(uopIdCookie).requestType(Http.RequestType.FORM).get().send().getResponse();
		String body = res.getResult();
		if (body == null) {
			logger.info("pc支付宝" + url + "cookies===" + uopIdCookie);
		} else {
			logger.info("pc支付宝1" + body + "====" + url + "cookies===" + uopIdCookie);
		}

		if (res.getStatus() == 0 || StringUtils.isEmpty(body)) {
			cardDao.addProxyFailTimes(cardId);
			return alipay_url;
		}
		if (!JSONUtil.isJson(body)) {
			return alipay_url;
		}
		JSONObject json = JSONObject.fromObject(body);
		if (!"success".equals(json.getString("result"))) {
			return alipay_url;
		}
		String basePayUrl = json.getString("payUrl");
		res = Http.create(basePayUrl).proxy(proxyIp, proxyProt).requestType(Http.RequestType.FORM).get().send().getResponse();
		String htmlResponse = res.getResult();
		if (res.getStatus() == 0 || StringUtils.isEmpty(htmlResponse)) return alipay_url;
		headers = new HashMap<String, String>();
		headers.put("Referer", basePayUrl);
		headers.put("User-Agent", UnicomConfig.Web_User_Agent);
		Map<String, String> params = new HashMap<String, String>();
		String pattern = "<input[\\s\\S]*?type=\"hidden\"[\\s\\S]*?name=\"(.*?)\"[\\s\\S]*?value=\"(.*?)\"";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(htmlResponse);
		while (m.find()) {
			params.put(m.group(1), m.group(2));
		}
		//params.put("bankSelect","alipayApp&PwkANbkAVfM=&07");
		params.put("bankSelect", "AliPay01&PwkANbkAVfM=&01");
		if (params.get("enServiceOrder") == null) {
			return alipay_url;
		}
		// params.put("enChannelStr", enChannelStr);
		String getPayUrl = "https://unipay.10010.com/udpNewPortal/payment/paymentAffirm";
		//UnicomConfig.GET_PAY_URL_SERVER
		res = Http.create(getPayUrl).proxy(proxyIp, proxyProt).heads(headers).redirect(false).heads(headers).cookies("MUT_V=" + UnicomConfig.version).body(params).requestType(Http.RequestType.FORM).post().send().getResponse();
		logger.info("pc支付宝请求" + res.getResult());

		return res.getResult();
	}


	private String getAliPayUrl_newH5(Long cardId, String unicomOrderId, String phone, String uopIdCookie, String enChannelStr, String proxyIp, int proxyProt) {
		String alipay_url = null;
		Map<String, String> headers = new HashMap<>();
		headers.put("Referer", "https://upay.10010.com/npfwap/npfMobWeb/query/index.html?phone=" + phone);
		headers.put("User-Agent", "okhttp/3.9.1");
		Http.Response res = Http.create("http://upay.10010.com/npfwap/NpfMobAppQuery/Repay/repaySubmit.action?orderNo=" + unicomOrderId + "&channelType=308").proxy(proxyIp, proxyProt).heads(headers).cookies(uopIdCookie).requestType(Http.RequestType.FORM).get().send().getResponse();
		String body = res.getResult();
		if (res.getStatus() == 0 || StringUtils.isEmpty(body)) {
			this.cardDao.addProxyFailTimes(cardId);
			return alipay_url;
		}
		if (!JSONUtil.isJson(body))
			return alipay_url;
		JSONObject json = JSONObject.fromObject(body);
		if (!"success".equals(json.getString("result")))
			return alipay_url;
		String basePayUrl = json.getString("payUrl");
		res = Http.create(basePayUrl)/*.proxy(proxyIp, proxyProt)*/.requestType(Http.RequestType.FORM).get().send().getResponse();
		String htmlResponse = res.getResult();
		if (res.getStatus() == 0 || StringUtils.isEmpty(htmlResponse))
			return alipay_url;
		headers = new HashMap<>();
		headers.put("Referer", basePayUrl);
		headers.put("User-Agent", "okhttp/3.9.1");
		Map<String, String> params = new HashMap<>();
		String pattern = "<input[\\s\\S]*?type=\"hidden\"[\\s\\S]*?name=\"(.*?)\"[\\s\\S]*?value=\"(.*?)\"";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(htmlResponse);
		while (m.find())
			params.put(m.group(1), m.group(2));
		params.put("bankSelect", "alipayApp&PwkANbkAVfM=&07");
		if (params.get("enServiceOrder") == null)
			return alipay_url;
		res = Http.create("https://unipay.10010.com/udpNewPortal/miniwappayment/miniWapPaymentAffirm").proxy(proxyIp, proxyProt).heads(headers).redirect(false).heads(headers).cookies("MUT_V=android@6.0100").body(params).requestType(Http.RequestType.FORM).post().send().getResponse();
		if (res.getStatus() == 0 || res.getHeads().get("Location") == null)
			return alipay_url;
		String location = res.getHeads().get("Location").toString();
		location = location.replaceAll("unipay://alipay/", "");
		return  location;
	}

	/**
	 * 卡密微信下单
	 *
	 * @param cardId
	 * @param unicomOrderId
	 * @param phone
	 * @param uopIdCookie
	 * @param enChannelStr
	 * @param proxyIp
	 * @param proxyProt
	 * @return
	 */
	private String getWechatUrl(Long cardId, String unicomOrderId, String phone, String uopIdCookie, String enChannelStr, String proxyIp, int proxyProt) {
		String wechat_url = null;
		List<String> htmllist = getSubmitOrder(phone, unicomOrderId, proxyIp, proxyProt, uopIdCookie);
		if (StringUtils.isEmpty(htmllist.get(0))) return wechat_url;
		Map<String, String> headers = new HashMap<String, String>();
		headers = new HashMap<String, String>();
		headers.put("Referer", htmllist.get(1));
		headers.put("User-Agent", UnicomConfig.User_Agent);
		Map<String, String> params = new HashMap<String, String>();
		String pattern = "<input[\\s\\S]*?type=\"hidden\"[\\s\\S]*?name=\"(.*?)\"[\\s\\S]*?value=\"(.*?)\"";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(htmllist.get(0));
		while (m.find()) {
			params.put(m.group(1), m.group(2));
		}
		params.put("bankSelect", "weixinH5&U7Q0XM48GVQ=&07");
		if (params.get("enServiceOrder") == null) {
			return wechat_url;
		}
		// params.put("enChannelStr", enChannelStr);
		Response res = Http.create(UnicomConfig.GET_PAY_URL_SERVER).proxy(proxyIp, proxyProt).heads(headers).redirect(false).heads(headers).cookies("MUT_V=" + UnicomConfig.version).body(params).requestType(Http.RequestType.FORM).post().send().getResponse();
		if (res.getStatus() == 0 || res.getHeads().get("Location") == null) {
			return "enChannelStr";
		}
		String location = res.getHeads().get("Location").toString();
		String wx_h5_return = URLDecoder.decode(location.substring(location.indexOf(
				"redirect_url=") + "redirect_url=".length(), location.length())).replace(
				"weixinH5ReturnPre", "weixinH5Return");
		return location + "!" + wx_h5_return + "!";
	}

	private String getBasePayUrl(String orderNo, String phone, String uopIdCookie, String proxyIp, int proxyProt) {
		String basePayUrl = "";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Host", "upay.10010.com");
		headers.put("Referer", "https://upay.10010.com/npfwap/npfMobWap/buycard/init");
		headers.put("User-Agent", UnicomConfig.User_Agent);
		Map<String, String> params = new HashMap<String, String>();
		params.put("secstate.state", orderNo);
		Response res = Http.create(UnicomConfig.BUY_CARD_SUBMIT_SERVER).proxy(proxyIp, proxyProt).cookies(uopIdCookie).heads(headers).body(params).requestType(Http.RequestType.FORM).post().send().getResponse();
		String body = res.getResult();
		if (res.getStatus() == 0 || StringUtils.isEmpty(body)) return basePayUrl;
		if (JSONUtil.isJson(body)) {
			JSONObject json = JSONObject.fromObject(body);
			if ("success".equals(json.getString("rst"))) {
				return json.getString("payResultUrl");
			}
		}
		return basePayUrl;
	}




	/**
	 * 卡密订单
	 *
	 * @param cardId
	 * @param phone
	 * @param amount
	 * @param uopIdCookie
	 * @param proxyIp
	 * @param proxyProt
	 * @return
	 */
	private String getAppOrderNo(Long cardId, String phone, Integer amount, String uopIdCookie, String proxyIp, int proxyProt) {
		String orderNo = "";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Host", "upay.10010.com");
		headers.put("Referer", "https://upay.10010.com/npfwap/npfMobWap/buycard/init");
		headers.put("User-Agent", UnicomConfig.User_Agent);
		Map<String, String> params = new HashMap<String, String>();
		params.put("commonBean.orgCode", "03");
		params.put("commonBean.channelCode", "alipayApp");
		params.put("cardBean.buyCardPhoneNo", phone);
		params.put("phoneVerifyCode", "");
		params.put("cardBean.minCardNum", "1");
		params.put("cardBean.maxCardNum", "15");
		this.setOrderParam(params, amount);
		Response res = null;
		String body = "";
		int i = 0;
		while (i < 3) {
			res = Http.create(UnicomConfig.BUY_CARD_CHECK_SERVER).proxy(proxyIp, proxyProt).cookies(uopIdCookie).heads(headers).body(params).requestType(Http.RequestType.FORM).post().send().getResponse();
			body = res.getResult();
			if (res.getStatus() == 0 || StringUtils.isEmpty(body)) {
				cardDao.addProxyFailTimes(cardId);
				return orderNo;
			}
			JSONObject json = JSONObject.fromObject(body);
			logger.info("手机号：" + phone + ",app下单结果:" + body);
			String out = json.getString("out");
			if ("success".equals(out)) {
				orderNo = json.getString("secstate");
				break;
			} else if (out.contains("频繁")) {
				break;
			} else if (out.contains("请重新登陆")) {
				break;
			} else if (out.contains("9991") || out.contains("黑名单"))//限额，更改card_status
			{
				CardBean updateModel = new CardBean();
				updateModel.setUpdate_time(System.currentTimeMillis() / 1000);
				updateModel.setApp_place_order_flag(2);
				updateModel.setId(cardId);
				cardDao.update(updateModel);
				break;
			} else if (out.contains("限额") || out.contains("订单数过多"))//限额，更改card_status
			{
				CardBean updateModel = new CardBean();
				updateModel.setUpdate_time(System.currentTimeMillis() / 1000);
				updateModel.setApp_place_order_flag(0);
				updateModel.setId(cardId);
				cardDao.update(updateModel);
				break;
			}else if(out.contains("手机验证码已失效")) {
				orderNo = json.getString("secstate");
				cardDao.addProxyFailTimes(cardId);
				break;
			}
			i++;
		}
		return orderNo;
	}

	/**
	 * 获取直冲 enChannelStr
	 *
	 * @param proxyIp
	 * @param proxyProt
	 * @return
	 */
	private String getEnChannelStr(String proxyIp, int proxyProt, String uopIdCookie, String phone) {
		String UnicomOrderId = getAppUnicomOrderId2(uopIdCookie, proxyIp, proxyProt, "payfee");
		String htmlResponse = getSubmitOrder(phone, UnicomOrderId, proxyIp, proxyProt, uopIdCookie).get(0);
		String pattern = "<input[\\s\\S]*?type=\"hidden\"[\\s\\S]*?name=\"enChannelStr\"[\\s\\S]*?value=\"(.*?)\"";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(htmlResponse);
		if (m.find()) {
			return m.group(1);
		}
		return null;
	}

	/**
	 * 获取或者卡密订单
	 *
	 * @param uopIdCookie
	 * @param proxyIp
	 * @param proxyProt
	 * @param queryType
	 * @return
	 */
	private String getAppUnicomOrderId2(String uopIdCookie, String proxyIp, int proxyProt, String queryType) {
		String url = "https://upay.10010.com/npfwap/NpfMobAppQuery/feeSearch/queryOrderNew";
		//String param = "pageFlag=1&time=1594374438850&queryType=buycard";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://upay.10010.com/npfwap/npfMobWeb/query/index.html");
		headers.put("User-Agent", UnicomConfig.User_Agent);

		Map<String, String> params = new HashMap<String, String>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		params.put("startDate", "2019-09-01");
		params.put("endDate", df.format(new Date().getTime()));//df.format(new Date().getTime()));
		params.put("pageFlag", "1");
		params.put("time", (System.currentTimeMillis()) + "");
		params.put("queryType", queryType);
		Response res = Http.create(url)
				.proxy(proxyIp, proxyProt).heads(headers).cookies(uopIdCookie).body(params).requestType(Http.RequestType.FORM).post().send().getResponse();
		String body = res.getResult();
		//
		//{"RETURN_CODE":"0000","orderList":[[{"sortTime":"2020-07-10 17:15:22","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308072007101715220506196","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":2000,"incomeTotalMoney":2000,"invoiceTotalMoney":0,"orderTime":"2020-07-10T17:15:22","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-07-10 16:44:23","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308162007101644230508426","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":2000,"incomeTotalMoney":2000,"invoiceTotalMoney":0,"orderTime":"2020-07-10T16:44:23","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-07-10 15:55:11","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308122007101555110641366","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":2000,"incomeTotalMoney":2000,"invoiceTotalMoney":0,"orderTime":"2020-07-10T15:55:11","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-07-10 15:02:37","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308072007101502370599978","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":2000,"incomeTotalMoney":2000,"invoiceTotalMoney":0,"orderTime":"2020-07-10T15:02:37","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-07-10 14:08:19","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308082007101408180410178","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":2000,"incomeTotalMoney":2000,"invoiceTotalMoney":0,"orderTime":"2020-07-10T14:08:19","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}}],[{"sortTime":"2020-06-29 16:14:50","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101002006291614500501092","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-29T16:14:50","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-26 14:04:05","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308182006261404050368271","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":3000,"incomeTotalMoney":3000,"invoiceTotalMoney":0,"orderTime":"2020-06-26T14:04:05","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:53:18","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308192006181453170502755","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:53:18","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:53:16","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308062006181453150470621","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:53:16","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:53:15","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308042006181453150539583","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:53:15","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:53:14","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308222006181453130545461","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:53:14","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:53:10","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308032006181453090597929","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:53:10","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:53:08","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308032006181453070267082","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:53:08","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:53:06","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308232006181453050631397","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:53:06","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:53:04","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308152006181453030581253","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:53:04","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:53:02","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308172006181453010581229","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:53:02","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:53:00","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308162006181452590601631","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:53:00","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:52:58","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308112006181452570497688","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:52:58","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:52:56","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308022006181452550497656","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:52:56","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:52:55","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308142006181452540331478","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:52:55","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:52:52","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308082006181452510469572","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:52:52","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:52:50","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308072006181452490569247","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:52:50","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:52:48","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308042006181452470458315","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:52:48","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:52:46","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308032006181452450601433","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:52:46","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:52:44","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308112006181452430412792","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:52:44","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2020-06-18 14:52:42","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308072006181452410266681","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2020-06-18T14:52:42","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}}],[{"sortTime":"2020-05-24 17:50:47","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100807308232005241750470457708","loginType":"06","deliverCarrier":"130****3890&1","channelType":"308","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":2000,"incomeTotalMoney":2000,"invoiceTotalMoney":0,"orderTime":"2020-05-24T17:50:47","serviceNo":"13039223890","channelName":"手厅客户端","tradeId":"tradeId","onlineState":"up"}}],[],[],[],[],[],[],[],[{"sortTime":"2019-09-27 16:26:45","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101031909271626440109062","loginType":"06","deliverCarrier":"130****3890&15","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":30000,"incomeTotalMoney":30000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T16:26:45","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 16:21:11","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101221909271621100109154","loginType":"06","deliverCarrier":"130****3890&15","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":30000,"incomeTotalMoney":30000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T16:21:11","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 14:51:50","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101121909271451490015666","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":2000,"incomeTotalMoney":2000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T14:51:50","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 14:50:56","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101231909271450550150027","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":2000,"incomeTotalMoney":2000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T14:50:56","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 14:50:38","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101191909271450380086933","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":3000,"incomeTotalMoney":3000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T14:50:38","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 14:49:48","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101031909271449480109700","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":3000,"incomeTotalMoney":3000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T14:49:48","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 14:40:26","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101141909271440260083887","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":2000,"incomeTotalMoney":2000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T14:40:26","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 14:36:40","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101201909271436390074014","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":3000,"incomeTotalMoney":3000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T14:36:40","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 14:36:04","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101131909271436040072056","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T14:36:04","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 14:35:25","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101201909271435250011809","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":5000,"incomeTotalMoney":5000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T14:35:25","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 14:16:56","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101001909271416550071147","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":3000,"incomeTotalMoney":3000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T14:16:56","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 14:16:21","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101071909271416200086334","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":2000,"incomeTotalMoney":2000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T14:16:21","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 14:13:05","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101071909271413050158974","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":2000,"incomeTotalMoney":2000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T14:13:05","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 14:10:22","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101061909271410210070138","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":2000,"incomeTotalMoney":2000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T14:10:22","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 14:07:24","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101081909271407230088238","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":2000,"incomeTotalMoney":2000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T14:07:24","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 14:06:06","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101091909271406050084283","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T14:06:06","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 14:02:41","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101051909271402400100320","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T14:02:41","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 14:00:50","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101081909271400490071516","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T14:00:50","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 11:50:42","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101061909271150410151299","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T11:50:42","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 11:50:22","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101171909271150210103750","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T11:50:22","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 11:49:46","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101161909271149460101205","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T11:49:46","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 11:48:29","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101111909271148280105590","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":10000,"incomeTotalMoney":10000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T11:48:29","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}},{"sortTime":"2019-09-27 10:49:16","_source":{"serviceType":"08","invoiceOrderNo":"","orderNo":"100801101181909271049150018905","loginType":"06","deliverCarrier":"130****3890&1","channelType":"101","customID":"6781909271048060","orderState":"00","fundType":"1","topayTotalMoney":2000,"incomeTotalMoney":2000,"invoiceTotalMoney":0,"orderTime":"2019-09-27T10:49:16","serviceNo":"13039223890","channelName":"网厅","tradeId":"tradeId","onlineState":"up"}}],[]]}
		if (body == null) {
			logger.info("直冲订单获取信息错误" + body);
			return "";
		}
		String UnicomOrderId = "";
		String faceValue = "";
		String createtime = "";
		if (JSONUtil.isJson(body)) {
			JSONObject json = JSONObject.fromObject(body);
			if ("0000".equals(json.getString("RETURN_CODE"))) {
				JSONArray orderList = json.getJSONArray("orderList");
				if (orderList.isEmpty())
					return "";
				JSONArray jsonArray = orderList.getJSONArray(0);
				for (int i = 0; i < jsonArray.size(); i++) {
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					JSONObject source = jsonObject.optJSONObject("_source");
					String time = jsonObject.getString("sortTime");
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date = new Date();
					try {
						date = simpleDateFormat.parse(time);
					}catch (Exception e){
					}
					long timeStamp = System.currentTimeMillis()/1000;
					long ts = date.getTime()/1000;
					String orderState = source.optString("orderState");
					if ("00".equalsIgnoreCase(orderState) && timeStamp - ts < 60*20) {
						UnicomOrderId = source.optString("orderNo");
						faceValue = (Integer.parseInt(source.getString("topayTotalMoney")) / 100) + "";
						createtime = ts+"";
						break;
					}
				}
			}
		}
		if (StringUtils.isEmpty(UnicomOrderId)) {
			logger.info("获取直冲订单号错误" + UnicomOrderId);
			return  "";
		}
		if(dao.getByUnicomOrderId(UnicomOrderId) == null){
			return UnicomOrderId+"!"+faceValue+"!"+createtime;
		}else{
			return  "";
		}
	}

	public static void main(String[] args) {
		String basePayUrl = "http://unipay.10010.com/udpNewPortal/miniwappayment/miniWapPaymentChoose?ServicePlatID=10&PayReqObj=kje7veI4XTf2qR1Iz79n7cy9B1T4%2BkptMYYZg08uvvAbUCXbdJ5w1Ntfv%2Bi7GyWuKSvXGxsyLnmP3sOtFae02GHwDOxJ4YX8KcUTFZ2dKEGiuxROxDdJ0i4O6%2F%2BlnWd4zDsKgROE9W%2BwxeHu01ufvYDhV4jczI1uzFkqRJFHUwKH04iawFbak1z6J671xCafjGA3c6n%2F6MPPpdUEZKI040GYHLnYiSnmmcgBNahAsOqtt9OcCTai6urWGXsk2uVQ%2FUeFsEaCSXm6ql2s6206rrv3GZNVB8GEcLzoAzzGwvILf%2FkgyfOPST9DvQ41CaLo35psUlM9HZ%2BLwLDhAueLbgnGyNmlg975Y5ondrylWZGgF5hR5fbxWWAPDt9dhLHa5CEzAsYVeFsZWd4f3RlEG9hK%2FDLs0ciQyFp%2FgkiWxhvuEQZwrC9LIv%2FbNHROr8sTsc9IC7gSRBD%2FmHGexdyLA4v9QQBCwl22NVzUFUInZ2bn0sRo%2B1pZOMrgxXk0ikFUCUP605nYbVQWQGCCTApUGz1ORElwiPAvtDbJwG0hchH4FMM%2BamR3h8r8p7kpHbjUw%2FybH6DzGZn2a0typVm%2FzTFVhkpO81LeaOF2bloaGfj13B0yTCgHFeWNDmHeSiJDYSh34%2BRhARhMBCT0sWggLU4V7n76F1hanpMWKqQh8LZMv%2F2m5MPcLG9wHNeK6XMkTJ3pWDTV5trekmvcXzuzT5hTlIiX%2FTRhmb0B1xmtSc8Ulv8a1r5QgMMcbsv3W4m0vhOG7dE%2F%2FBl2u1lyAjwO9wN1bgaiQAPJ6gayv0XiMX3pfL%2Bfs%2BoqEAqlNqu0zpIAhVfMr29UB22R1HdKUNzbVsPThfHlWeTCLjsO4%2B1jw8mgHNRA%2FGtrnc7M5FHyfAqWhAttZXko6UkV%2BMtKqfPhhZnFwJxQXppuiW30VD7kVrC8s8c76jTqn8oCmFuPXKs5AMWKtjJnt%2BxGemMTOLNQehAe%2F%2FUgR77DkUdtpmiZ0Vexp1Wtun1hlHd6kg9EPh3ta4KGkSDcdUsjCGziN58GNMg9aq7f3Eb5y3dTtHBAaFi7HbvHTArZuiX4v%2BuJgwAc85syKhbSL2t2qn0CrhCU0FhPpnr5mH8RIE8VtHVTZfP7iiwSibIRbu%2B0KE%2Bf7igm8WNv0NsR83NovHmjK8Opezlcjs1IcZeeOH4zlMgGVz7nKEuZH4Foke6Esy3b9ef5rcBxg40wmIxrwxz0LlxCbUPqqrTDqJOBAdMbsUFzbsGzLW1o1sxXLJ4GD23Ru6%2FSgk0TAH7LZuqfjRtsVlo%2BaqRJ58RuEGJaHJTOKa8%2FB7PUo07mIV1SCXQiE7%2BXdlCw8cWpep1P7RNx8p9MCNyvNSeFGC4tVxig9aUhhg89H6ZMWnwmhCL20nqgyxi7yqkBPDl6McxdHuf9h0UjU5a8uZM0yBTyKFvCZLOSGi2K%2FVlYLTZrrcqfss5jR6WyZnwGJuVwSI4idK4bWxnnuLusUA%3D%3D&pageType=07";
		Response res = Http.create(UnicomConfig.GET_WECHAT_PRE_SERVER + basePayUrl.substring(basePayUrl.indexOf("?")))
				.requestType(Http.RequestType.FORM).timeout(8).get().send().getResponse();
		String htmlResponse = res.getResult();
		System.err.println(htmlResponse);
	}

	@Override
	public String getNewEnChannelStr(Long cardId, String phone, String uopIdCookie, String proxyIp, int proxyProt)
			throws Exception {
		if (StringUtils.isEmpty(uopIdCookie)) {
			logger.info("uopIdCookie为空，无法获取EnChannelStr");
			return "";
		}
		String orderNo = this.getAppChargeOrderNo(cardId, phone, 20, uopIdCookie, proxyIp, proxyProt);
		if (StringUtils.isEmpty(orderNo)) {
			return "";
		}
		String basePayUrl = "";
		int time = 0;
		while (time < 4) {
			basePayUrl = this.getChargeBasePayUrl(orderNo, phone, uopIdCookie, proxyIp, proxyProt);
			if (!StringUtils.isEmpty(basePayUrl)) break;
			Thread.sleep(800);
			time++;
		}
		if (StringUtils.isEmpty(basePayUrl)) {
			return "";
		}
		String enChannelStr = "";
		time = 0;
		while (time < 4) {
			enChannelStr = this.getEnChannelStr(proxyIp, proxyProt, uopIdCookie, phone);
			if (!StringUtils.isEmpty(enChannelStr)) break;
			time++;
		}
		if (StringUtils.isEmpty(enChannelStr)) {
			cardDao.addProxyFailTimes(cardId);
			return "";
		}
		logger.info(phone + ",EnChannelStr获取成功：" + enChannelStr);
		CardBean updateModel = new CardBean();
		updateModel.setId(cardId);
		updateModel.setEn_channel_str(enChannelStr);
		updateModel.setEn_channel_str_update_time(System.currentTimeMillis() / 1000);
		cardDao.update(updateModel);
		return enChannelStr;
	}

	private String getChargeBasePayUrl(String orderNo, String phone, String uopIdCookie, String proxyIp, int proxyProt) {
		String basePayUrl = "";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://upay.10010.com/npfwap/npfMobWeb/bankcharge/init");
		headers.put("User-Agent", UnicomConfig.User_Agent);
		Map<String, String> params = new HashMap<String, String>();
		params.put("secstate.state", orderNo);
		Response res = Http.create(UnicomConfig.BANK_CHARGE_SUBMIT_SERVER).proxy(proxyIp, proxyProt).cookies(uopIdCookie).heads(headers).body(params).requestType(Http.RequestType.FORM).post().send().getResponse();
		String body = res.getResult();
		if (res.getStatus() == 0 || StringUtils.isEmpty(body)) return basePayUrl;
		if (JSONUtil.isJson(body)) {
			JSONObject json = JSONObject.fromObject(body);
			return json.optString("payUrl");
		}
		return basePayUrl;
	}

	private String getAppChargeOrderNo(Long cardId, String phone, Integer amount, String uopIdCookie, String proxyIp, int proxyProt) {
		String orderNo = "";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Host", "m.client.10010.com");
		headers.put("User-Agent", UnicomConfig.User_Agent);
		headers.put("Referer", "https://upay.10010.com/npfwap/npfMobWeb/bankcharge/init");
		Response res = null;
		int i = 0;
		while (i < 5) {
			String chargePhone = phone;
			String param = "commonBean.phoneNo=" + chargePhone + "&commonBean.areaCode=&commonBean.provinceCode=090&commonBean.cityCode=901&bussineTypeInput=0&commonBean.payAmount=29.85&cardBean.cardValue=" + amount * 100 + "&cardBean.cardValueCode=02&commonBean.userChooseMode=0&invoiceBean.is_mailing=0&invoiceBean.need_invoice=0&invoiceBean.invoice_type=&invoiceBean.id_cardno=&invoiceBean.invoice_head=&invoiceBean.card_type=&commonBean.reserved1=false&numberType=&commonBean.channelType=308&channelKey=&commonBean.bussineType=06&commonBean.netAccount=&pointBean.payMethod=&pointBean.pointNumber=&browserVersion=&commonBean.activityType=02&commonBean.offerate=0.995&commonBean.offerateId=18072608450524800000&commonBean.orgCode=31&commonBean.channelCode=weixinH5&emergencyContact=&commonBean.ticketNo=&commonBean.reserved2=&ticketNew=ticket&commonBean.numberAttribution=&commonBean.token=&commonBean.urlSign=&commonBean.msgTimeStamp=&commonBean.serviceNo=&commonBean.natCode=&commonBean.saleChannel=null&commonBean.deviceId=null&commonBean.model=null&commonBean.vipCode=null&commonBean.activityOrderId=&joinSign=&presentActivityId=";
			res = Http.create(UnicomConfig.BANK_CHARGE_CHECK_SERVER).proxy(proxyIp, proxyProt).cookies(uopIdCookie).heads(headers).bodys(param).requestType(Http.RequestType.FORM).post().send().getResponse();
			String body = res.getResult();
			if (res.getStatus() == 0 || StringUtils.isEmpty(body)) {
				cardDao.addProxyFailTimes(cardId);
				return orderNo;
			}
			JSONObject json = JSONObject.fromObject(body);
			logger.info("手机号：" + phone + ",获取EnChannelStr下单结果:" + body);
			String out = json.getString("out");
			if ("success".equals(out)) {
				orderNo = json.getString("secstate");
				break;
			} else if (out.contains("频繁")) {
				cardDao.addProxyFailTimes(cardId);
				return orderNo;
			} else if (out.contains("号码不存在")) {
				//continue;
			} else if ("1001".equals(out) || out.contains("请重新登陆")) {
				break;
			} else if (out.contains("9991") || out.contains("黑名单") || out.contains("订单数过多"))//限额，更改card_status
			{
				CardBean updateModel = new CardBean();
				updateModel.setUpdate_time(System.currentTimeMillis() / 1000);
				updateModel.setApp_place_order_flag(2);
				updateModel.setId(cardId);
				cardDao.update(updateModel);
				logger.info("手机号：" + phone + ",获取EnChannelStr黑名单");
				break;
			}
			i++;
		}
		return orderNo;
	}

	@Override
	public List<OrderPoolBean> getLeftOrderCount() {
		// TODO Auto-generated method stub
		return dao.getLeftOrderCount();
	}

	@Override
	public void cardRePayOrderTask() throws Exception {
		// TODO Auto-generated method stub
		int removeTimeOutOrderCount = dao.removeTimeOutOrder();//删除30分钟过期订单
		logger.info("删除过期订单缓存 数量：" + removeTimeOutOrderCount);
		//int rePayAlipayOrderCount=dao.rePayAlipayOrder();//更新8分钟支付超时或未使用订单
		//logger.info("支付宝二次下单 数量："+rePayAlipayOrderCount);

		List<OrderPoolBean> cannotUseOrderList = dao.getCannotUseOrder();
		logger.info("过期需重新下单数量：" + cannotUseOrderList.size());
		int threadNum = 0;
		for (OrderPoolBean op : cannotUseOrderList) {
			threadNum++;
			final int currentThreadNum = threadNum;
			ThreadPoolUtils.getOrderRePayPool().execute(() -> {
				try {
					logger.info("开启二次下单子线程" + currentThreadNum + ",订单号:" + op.getUnicom_order_id() + ",下单金额:" + op.getAmount());
					getRePayOrder(op.getId(), op.getCard_id(), op.getUnicom_order_id(), op.getPay_type());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					logger.info("二次下单子线程" + currentThreadNum + "已结束！");
				}
			});
		}
		while (true) {
			Thread.sleep(1000);
			if (ThreadPoolUtils.getOrderRePayPool().getActiveCount() == 0) {
				logger.info("二次下单主线程结束");
				break;
			}
		}
	}

	@Override
	public boolean getRePayOrder(Long orderPoolId, Long cardId, String unicomOrderId, String pay_type) throws Exception {
		// TODO Auto-generated method stub
		CardBean card = cardDao.getById(cardId);
		if (card == null || card.getStatus() != 1 || card.getCard_status() != 1) {
			logger.info("手机号："  + ",登录状态异常，二次下单失败");
			return false;
		}
		OrderPoolBean orderPool = new OrderPoolBean();
		orderPool.setUpdate_time(System.currentTimeMillis() / 1000);
		orderPool.setId(orderPoolId);
		orderPool.setStatus(1);
		dao.update(orderPool);
		logger.info("手机号："  +cardId+ ",登录状态异常，二次下单成功");
		return true;
	}



	private String getWebOrderNo(Long cardId, String phone, Integer amount, String jutCookie, String proxyIp, int proxyProt) {
		String orderNo = "";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://upay.10010.com/npfweb/npfbuycardweb/buycard_recharge_fill.htm");
		headers.put("User-Agent", UnicomConfig.Web_User_Agent);
		Map<String, String> params = new HashMap<String, String>();
		params.put("commonBean.channelType", "101");
		params.put("cardBean.buyCardPhoneNo", phone);
		params.put("phoneVerifyCode", "");
		params.put("secstate.state", "3mCBuETgA/YTbuZO79gHFA==^@^0.0.1");
		params.put("cardBean.minCardNum", "1");
		params.put("cardBean.maxCardNum", "3");

		params.put("offerPriceStrHidden", amount + ".00");
		params.put("offerRateStrHidden", "1");
		params.put("MaxThreshold01", "15");
		params.put("MinThreshold01", "1");
		params.put("MaxThreshold02", "10");
		params.put("MinThreshold02", "1");
		params.put("MaxThreshold03", "6");
		params.put("MinThreshold03", "1");
		params.put("MaxThreshold04", "3");
		params.put("MinThreshold04", "1");

		this.setOrderParam(params, amount);
		Response res = null;
		String body = "";
		int i = 0;
		while (i < 5) {
			res = Http.create(UnicomConfig.WEB_BUY_CARD_CHECK_SERVER).proxy(proxyIp, proxyProt).cookies(jutCookie).heads(headers).body(params).requestType(Http.RequestType.FORM).post().send().getResponse();
			body = res.getResult();
			if (res.getStatus() == 0 || StringUtils.isEmpty(body)) {
				cardDao.addProxyFailTimes(cardId);
				return orderNo;
			}
			if (!JSONUtil.isJson(body)) {
				if (body.contains("login")) {
					CardBean updateModel = new CardBean();
					updateModel.setWeb_update_time(System.currentTimeMillis() / 1000);
					updateModel.setWeb_login_status(2);
					updateModel.setId(cardId);
					cardDao.update(updateModel);
				}
				break;
			}
			JSONObject json = JSONObject.fromObject(body);
			logger.info("手机号：" + phone + ",web下单结果:" + body);
			String out = json.getString("out");
			if ("success".equals(json.getString("out"))) {
				orderNo = json.getString("secstate");
				break;
			}
			if (out.contains("频繁")) {
				break;
			} else if (out.contains("9991") || out.contains("黑名单"))// 限额，更改card_status
			{
				CardBean updateModel = new CardBean();
				updateModel.setUpdate_time(System.currentTimeMillis() / 1000);
				updateModel.setWeb_place_order_flag(2);
				updateModel.setId(cardId);
				cardDao.update(updateModel);
				break;
			} else if (out.contains("限额") || out.contains("订单数过多"))// 限额，更改card_status
			{
				CardBean updateModel = new CardBean();
				updateModel.setWeb_update_time(System.currentTimeMillis() / 1000);
				updateModel.setWeb_place_order_flag(0);
				updateModel.setId(cardId);
				cardDao.update(updateModel);
				break;
			}
			i++;
		}
		return orderNo;
	}

	private String getWebUnicomOrderId(String orderNo, String phone, String jutIdCookie, String proxyIp, int proxyProt) {
		String unicomOrderId = "";
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://upay.10010.com/npfweb/npfbuycardweb/buycard_recharge_fill.htm");
		headers.put("User-Agent", UnicomConfig.Web_User_Agent);
		Map<String, String> params = new HashMap<String, String>();
		params.put("secstate.state", orderNo);
		Response res = Http.create(UnicomConfig.WEB_BUY_CARD_SUBMIT_SERVER).proxy(proxyIp, proxyProt).heads(headers).cookies(jutIdCookie).body(params).requestType(Http.RequestType.FORM).post().send().getResponse();
		if (res.getStatus() == 200) {
			String body = res.getResult();
			if (StringUtils.isEmpty(body)) return unicomOrderId;
			if (body.contains("订单号：")) {
				String pattern = "<p>订单号：(.*?)</p>";
				Pattern r = Pattern.compile(pattern);
				Matcher m = r.matcher(body);
				m.find();
				return m.group(1);
			}
		} else if (res.getStatus() == 302) {
			if (res.getHeads().get("Location") != null) {
				String location = res.getHeads().get("Location").toString();
				res = Http.create(location).proxy(proxyIp, proxyProt).heads(headers).requestType(Http.RequestType.FORM).get().send().getResponse();
				String body = res.getResult();
				if (StringUtils.isEmpty(body)) {
					logger.info("web支付二次请求错误"+location);
					return unicomOrderId;
				}
				else {
					String pattern = "<p>订单号：(.*?)</p>";
					Pattern r = Pattern.compile(pattern);
					Matcher m = r.matcher(body);
					m.find();
					return m.group(1);
				}
			}
		}
		return unicomOrderId;
	}

	@Override
	public int removeByCardId(Long cardId) {
		// TODO Auto-generated method stub
		return dao.removeByCardId(cardId);
	}

	@Override
	public int removeByGroupId(Long groupId) {
		// TODO Auto-generated method stub
		return dao.removeByGroupId(groupId);
	}

	@Override
	public List<OrderPoolBean> getLeftOrderCountByPayType(String pay_type) {
		// TODO Auto-generated method stub
		return dao.getLeftOrderCountByPayType(pay_type);
	}

}
