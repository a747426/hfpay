package com.jiongzai.pay.service.impl;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jiongzai.pay.util.security.RSAJS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiongzai.pay.config.UnicomConfig;
import com.jiongzai.pay.dao.CardDao;
import com.jiongzai.pay.dao.ConfigDao;
import com.jiongzai.pay.model.CardBean;
import com.jiongzai.pay.model.IpPoolBean;
import com.jiongzai.pay.service.CardService;
import com.jiongzai.pay.service.IpPoolService;
import com.jiongzai.pay.service.OrderPoolService;
import com.jiongzai.pay.util.ThreadPoolUtils;
import com.jiongzai.pay.util.date.DateTimeUtil;
import com.jiongzai.pay.util.http.Http;
import com.jiongzai.pay.util.http.Http.Response;
import com.jiongzai.pay.util.json.JSONUtil;
import com.jiongzai.pay.util.security.PassWrodsCreater;
import com.jiongzai.pay.util.security.RSA;

import net.sf.json.JSONObject;

@Service
public class CardServiceImpl implements CardService{
	
	private static final Logger logger = LogManager.getLogger(CardServiceImpl.class);
	
	private static Map<String, String> callback = new HashMap<String, String>();
	
	@Autowired
	private CardDao dao;
	
	@Autowired
	private IpPoolService ipPoolService;
	
	@Autowired
	private OrderPoolService orderPoolService;
	
	@Autowired
	private ConfigDao configDao;

	@Override
	public int insert(CardBean t) {
		// TODO Auto-generated method stub
		return dao.insert(t);
	}

	@Override
	public int deleteById(Long id) {
		// TODO Auto-generated method stub
		return dao.deleteById(id);
	}

	@Override
	public List<CardBean> queryList(CardBean t) {
		// TODO Auto-generated method stub
		return dao.queryList(t);
	}

	@Override
	public int update(CardBean t) {
		// TODO Auto-generated method stub
		return dao.update(t);
	}

	@Override
	public CardBean getById(Long Id) {
		// TODO Auto-generated method stub
		return dao.getById(Id);
	}

	@Override
	public long count(CardBean t) {
		// TODO Auto-generated method stub
		return dao.count(t);
	}

	@Override
	public PageInfo<CardBean> pageList(CardBean t, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, pageSize);
		List<CardBean> list = dao.adminQueryList(t);
		PageInfo<CardBean> pageInfo= new PageInfo<CardBean>(list);
		return pageInfo;
	}

	@Override
	public int removeById(Long id) {
		// TODO Auto-generated method stub
		return dao.removeById(id);
	}

	@Override
	public List<CardBean> queryAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean sendLoginSms(Long cardId,String phone,String proxyIp,int proxyProt) throws Exception {
		// TODO Auto-generated method stub
		String param="send_flag=&mobile="+RSA.unicomEncryption(phone)+"&version="+UnicomConfig.version+"&keyVersion=";
		Map<String,String> headers=new HashMap<String,String>();
		headers.put("Host", "m.client.10010.com");
		headers.put("User-Agent", UnicomConfig.User_Agent);
		Response res=Http.create(UnicomConfig.SEND_LOGIN_SMS_SERVER).proxy(proxyIp, proxyProt).heads(headers).bodys(param).requestType(Http.RequestType.FORM).post().send().getResponse();
		String body=res.getResult();
		if(res.getStatus()==0||StringUtils.isEmpty(body))
		{
			dao.addProxyFailTimes(cardId);
			return false;
		}
		logger.info("手机号："+phone+",登录验证码发送结果:"+body);
		JSONObject json=null;
		try {
			json = JSONObject.fromObject(body);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			dao.addProxyFailTimes(cardId);
			return false;
		}
		if("0000".equals(json.getString("rsp_code")))
		{
			CardBean updateModel=new CardBean();
			updateModel.setCard_status(-1);
			updateModel.setId(cardId);
			updateModel.setVer_code("");
			updateModel.setUpdate_time(System.currentTimeMillis()/1000);
			dao.update(updateModel);
			return true;
		}
		else
		{
			CardBean updateModel=new CardBean();
			updateModel.setCard_status(2);
			updateModel.setId(cardId);
			updateModel.setUpdate_time(System.currentTimeMillis()/1000);
			dao.update(updateModel);
			return false;
		}
	}
	
	@Override
	public boolean cardLoginWeb(Long cardId,String phone, String verCode,String proxyIp,int proxyProt) throws Exception {
		// TODO Auto-generated method stub
		Map<String,String> headers=new HashMap<String,String>();
		headers.put("Referer", "https://uac.10010.com/portal/homeLoginNew");
		headers.put("User-Agent", UnicomConfig.Web_User_Agent);
		String param = "?callback=" + callback.get(phone) + "&req_time=" + System.currentTimeMillis() + "&redirectURL=http%3A%2F%2Fwww.10010.com%2Fnet5%2F&userName=" + phone + "&password=" + RSAJS.excuteJs(verCode) + "&pwdType=02&productType=01&redirectType=01&rememberMe=1&_=" + System.currentTimeMillis();
		Response res=Http.create(UnicomConfig.WEB_CARD_LOGIN_SERVER+param).proxy(proxyIp, proxyProt).heads(headers).requestType(Http.RequestType.FORM).get().send().getResponse();
		String body=res.getResult();
		if(res.getStatus()==0||StringUtils.isEmpty(body))
		{
			dao.addProxyFailTimes(cardId);
			return false;
		}
		try {
			logger.info("手机号："+phone+",Web验证码登录结果:"+body);
			if(body.contains("\"0000\""))
			{
				String _login_cookie=res.getHeads().get("Set-Cookie").toString();
				CardBean updateModel=new CardBean();
				updateModel.setWeb_ver_code("");
				updateModel.set_web_login_cookie(_login_cookie);
				updateModel.setWeb_login_status(1);
				updateModel.setWeb_update_time(System.currentTimeMillis()/1000);
				updateModel.setId(cardId);
				dao.update(updateModel);
				return true;
			}
			else
			{
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CardBean updateModel=new CardBean();
			updateModel.setWeb_login_status(2);
			updateModel.setWeb_ver_code("");
			updateModel.setId(cardId);
			dao.update(updateModel);
			return false;
		}
	}

	@Override
	public boolean cardLogin(Long cardId,String phone, String verCode,String proxyIp,int proxyProt) throws Exception {
		// TODO Auto-generated method stub
		Map<String,String> headers=new HashMap<String,String>();
		headers.put("Host", "m.client.10010.com");
		headers.put("User-Agent", UnicomConfig.User_Agent);
		String deviceCode = "86";
		deviceCode += phone.substring(5, 8);
		deviceCode += phone.substring(8);
		deviceCode += "26";
		deviceCode += phone.substring(3, 5);
		deviceCode += phone.substring(0, 3);
//		String param="isRemberPwd=true&loginStyle=0&deviceId="+UnicomConfig.deviceCode+"&password="+RSA.unicomEncryption(verCode)+"&netWay=Wifi&mobile="+RSA.unicomEncryption(phone)+"&yw_code=&timestamp="+DateTimeUtil.getCurrentDateTimeyyyyMMddHHmmss()+"&appId="+UnicomConfig.appId+"&keyVersion=&deviceBrand="+UnicomConfig.deviceBrand+"&voiceoff_flag=1&pip=192.168.2.180&voice_code=&provinceChanel=general&version="+UnicomConfig.version+"&deviceModel="+UnicomConfig.deviceModel+"&deviceOS="+UnicomConfig.deviceOS+"&deviceCode="+UnicomConfig.deviceCode;
		String param="isRemberPwd=true&loginStyle=0&deviceId="+deviceCode+"&password="+RSA.unicomEncryption(verCode)+"&netWay=Wifi&mobile="+RSA.unicomEncryption(phone)+"&yw_code=&timestamp="+DateTimeUtil.getCurrentDateTimeyyyyMMddHHmmss()+"&appId="+UnicomConfig.appId+"&keyVersion=&deviceBrand="+UnicomConfig.deviceBrand+"&voiceoff_flag=1&pip=192.168.2.180&voice_code=&provinceChanel=general&version="+UnicomConfig.version+"&deviceModel="+UnicomConfig.deviceModel+"&deviceOS="+UnicomConfig.deviceOS+"&deviceCode="+deviceCode;
		Response res=Http.create(UnicomConfig.CARD_LOGIN_SERVER).proxy(proxyIp, proxyProt).heads(headers).bodys(param).requestType(Http.RequestType.JSON).post().send().getResponse();
		String body=res.getResult();
		logger.info("请求连接："+res+",请求参数:"+param);
		if(res.getStatus()==0||StringUtils.isEmpty(body))
		{
			dao.addProxyFailTimes(cardId);
			return false;
		}
		try {
			JSONObject json=JSONObject.fromObject(body);
			logger.info("手机号："+phone+",验证码登录结果:"+body);

			if("0".equals(json.getString("code")))
			{
				String _login_cookie=res.getHeads().get("Set-Cookie").toString();
				CardBean updateModel=new CardBean();
				updateModel.setVer_code("");
				updateModel.set_login_token(json.getString("token_online"));
				updateModel.set_login_cookie(_login_cookie);
				updateModel.setCard_status(1);
				updateModel.setPro_name(json.getJSONArray("list").getJSONObject(0).getString("proName"));
				updateModel.setCity_name(json.getJSONArray("list").getJSONObject(0).getString("cityName"));
				updateModel.setUpdate_time(System.currentTimeMillis()/1000);
				updateModel.setId(cardId);
				dao.update(updateModel);
				new Thread(new Runnable() {
					public void run() {
						try {
							getNewUopIdCookie2(cardId,phone,_login_cookie,proxyIp,proxyProt);//登录成功，立马获取UopId，拒绝等待
						} catch (Exception e) {
							e.printStackTrace();
							logger.info(MessageFormat.format("手机号:{0}，登录后获取uopId出现异常", phone));
						}
					}
				}).start();
				return true;
			}
			else
			{
				CardBean updateModel=new CardBean();
				updateModel.setCard_status(2);
				updateModel.setVer_code("");
				updateModel.setId(cardId);
				dao.update(updateModel);
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			CardBean updateModel=new CardBean();
			updateModel.setCard_status(2);
			updateModel.setVer_code("");
			updateModel.setId(cardId);
			dao.update(updateModel);
			return false;
		}
	}
	
	@Override
	public boolean loginOnLine(Long cardId,String phone,String loginToken,String proxyIp,int proxyProt) throws Exception {
		// TODO Auto-generated method stub
		Map<String,String> headers=new HashMap<String,String>();
		headers.put("Host", "m.client.10010.com");
		headers.put("User-Agent", UnicomConfig.User_Agent);
		String deviceCode = "86";
		deviceCode += phone.substring(5, 8);
		deviceCode += phone.substring(8);
		deviceCode += "26";
		deviceCode += phone.substring(3, 5);
		deviceCode += phone.substring(0, 3);
//		String param="deviceId="+UnicomConfig.deviceCode+"&netWay=Wifi&reqtime="+System.currentTimeMillis()/1000+"&version="+UnicomConfig.version+"&deviceModel="+UnicomConfig.deviceModel+"&token_online="+loginToken+"&appId="+UnicomConfig.appId+"&deviceBrand="+UnicomConfig.deviceBrand+"&deviceCode="+UnicomConfig.deviceCode;
		String param="deviceId="+deviceCode+"&netWay=Wifi&reqtime="+System.currentTimeMillis()/1000+"&version="+UnicomConfig.version+"&deviceModel="+UnicomConfig.deviceModel+"&token_online="+loginToken+"&appId="+UnicomConfig.appId+"&deviceBrand="+UnicomConfig.deviceBrand+"&deviceCode="+deviceCode;
		Response res=Http.create(UnicomConfig.LOGIN_ONLINE_SERVER).proxy(proxyIp, proxyProt).heads(headers).bodys(param).requestType(Http.RequestType.FORM).post().send().getResponse();
		String body=res.getResult();
		if(res.getStatus()==0||StringUtils.isEmpty(body))
		{
			dao.addProxyFailTimes(cardId);
			return false;
		}
		if(!JSONUtil.isJson(body)) return false;
		JSONObject json=JSONObject.fromObject(body);
		if("0".equals(json.getString("code")))
		{
			String _login_cookie=res.getHeads().get("Set-Cookie").toString();
			CardBean updateModel=new CardBean();
			updateModel.setId(cardId);
			updateModel.set_login_token(json.getString("token_online"));
			updateModel.set_login_cookie(_login_cookie);
			updateModel.setCard_status(1);
			dao.update(updateModel);
			return true;
		}
		else
		{
			CardBean updateModel=new CardBean();
			updateModel.setCard_status(2);
			updateModel.setId(cardId);
			dao.update(updateModel);
			return false;
		}
	}
	
	public boolean getNewUopIdCookie2(Long cardId,String phone,String loginCookie,String proxyIp,int proxyPort) throws Exception {
		// TODO Auto-generated method stub
		Response res=Http.create("http://m.client.10010.com/mobileService/paymanager/businessRecharge/bankCardRecharge.htm").proxy(proxyIp, proxyPort).cookies(loginCookie).requestType(Http.RequestType.FORM).post().send().getResponse();
		if(res.getStatus()==0)
		{
			dao.addProxyFailTimes(cardId);
			return false;
		}
		if(res.getStatus()==302)
		{
			if(res.getHeads().get("Location")!=null)
			{
				String location=res.getHeads().get("Location").toString();
				res=Http.create(location).proxy(proxyIp, proxyPort).requestType(Http.RequestType.FORM).post().send().getResponse();
				if(res.getStatus()==0||StringUtils.isEmpty(res.getCookies())||!res.getCookies().contains("_uop_id"))
				{
					return false;
				}
				else
				{
					CardBean updateModel=new CardBean();
					updateModel.setUpdate_time(System.currentTimeMillis()/1000);
					updateModel.set_uop_id_cookie(res.getCookies());
					updateModel.setId(cardId);
					dao.update(updateModel);
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public boolean getNewUopIdCookie(Long cardId,String phone,String loginCookie,String proxyIp,int proxyPort) throws Exception {
		// TODO Auto-generated method stub
		Map<String,String> headers=new HashMap<String,String>();
		headers.put("Host", "m.client.10010.com");
		headers.put("User-Agent", UnicomConfig.User_Agent);
		String param="yw_code=&desmobile="+phone+"&version="+UnicomConfig.version;
		Response res=Http.create(UnicomConfig.GET_UOP_ID_SERVEER).proxy(proxyIp, proxyPort).requestCount(2).cookies(loginCookie).heads(headers).bodys(param).requestType(Http.RequestType.FORM).post().send().getResponse();
		if(res.getStatus()==0)
		{
			dao.addProxyFailTimes(cardId);
			return false;
		}
		if(res.getStatus()==200)
		{
			String cookie=res.getCookies();
			if(cookie.contains("_uop_id"))
			{
				CardBean updateModel=new CardBean();
				updateModel.setUpdate_time(System.currentTimeMillis()/1000);
				updateModel.set_uop_id_cookie(res.getCookies());
				updateModel.setId(cardId);
				dao.update(updateModel);
				return true;
			}
			return false;
		}
		else if(res.getStatus()==302)
		{
			if(res.getHeads().get("Location")!=null)
			{
				String location=res.getHeads().get("Location").toString();
				res=Http.create(location).proxy(proxyIp, proxyPort).requestCount(2).heads(headers).requestType(Http.RequestType.FORM).post().send().getResponse();
				if(res.getStatus()==0||StringUtils.isEmpty(res.getCookies())||!res.getCookies().contains("_uop_id"))
				{
					return false;
				}
				else
				{
					CardBean updateModel=new CardBean();
					updateModel.setUpdate_time(System.currentTimeMillis()/1000);
					updateModel.set_uop_id_cookie(res.getCookies());
					updateModel.setId(cardId);
					dao.update(updateModel);
					return true;
				}
			}
		}
		return false;
		
	}

	@Override
	public void cardSmsLoginTask() throws Exception {
		// TODO Auto-generated method stub
		CardBean queryModel=new CardBean();
		queryModel.setStatus(1);
		queryModel.setCard_status(-1);
		List<CardBean> loginList = dao.queryCanLoginList(queryModel);
		int threadNum = 0;
		for (CardBean card : loginList) {
			if (card.getUpdate_time() < System.currentTimeMillis() / 1000 - 120)// 更新卡号
			{
				CardBean updateModel = new CardBean();
				updateModel.setCard_status(2);
				updateModel.setId(card.getId());
				dao.update(updateModel);
				continue;
			}
			String verCode = card.getVer_code();
			if (!StringUtils.isEmpty(verCode)) {
				threadNum++;
				final int currentThreadNum = threadNum;
				ThreadPoolUtils.getCardSmsLoginPool().execute(() -> {
					try {
						logger.info("开启app验证码登录子线程" + currentThreadNum + ",手机号:" + card.getCard_no());
						cardLogin(card.getId(), card.getCard_no(), verCode, card.getProxy_ip(), card.getProxy_port());
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(MessageFormat.format("app验证码登录出现异常,手机号:{0}短信内容:{1}", card.getCard_no(),verCode), e);
					} finally {
						logger.info("app验证码登录子线程" + currentThreadNum + "已结束！");
					}
				});
				// 获取短信并调用登录接口
			}
		}
		
		queryModel=new CardBean();
		queryModel.setStatus(1);
		queryModel.setWeb_login_status(-1);
		List<CardBean> webLoginList = dao.queryCanLoginList(queryModel);
		for (CardBean card : webLoginList) {
			if (card.getWeb_update_time() < System.currentTimeMillis() / 1000 - 100)// 更新卡号
			{
				CardBean updateModel = new CardBean();
				updateModel.setWeb_login_status(2);
				updateModel.setId(card.getId());
				dao.update(updateModel);
				continue;
			}
			String webVerCode = card.getWeb_ver_code();
			if (!StringUtils.isEmpty(webVerCode)) {
				threadNum++;
				final int currentThreadNum = threadNum;
				ThreadPoolUtils.getCardSmsLoginPool().execute(() -> {
					try {
						logger.info("开启web验证码登录子线程" + currentThreadNum + ",手机号:" + card.getCard_no());
						cardLoginWeb(card.getId(), card.getCard_no(), webVerCode, card.getProxy_ip(),
								card.getProxy_port());
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(
								MessageFormat.format("web验证码登录出现异常,手机号:{0}短信内容:{1}", card.getCard_no(), webVerCode), e);
					} finally {
						logger.info("web验证码登录子线程" + currentThreadNum + "已结束！");
					}
				});
			}
		}
		while (true) {
	        Thread.sleep(1000);
	        if(ThreadPoolUtils.getCardSmsLoginPool().getActiveCount()==0){  
	        	logger.info("app和web验证码登录主线程结束");  
	            break;  
	        }  
		}
	}

	@Override
	public void sendLoginSmsTask() throws Exception {
		// TODO Auto-generated method stub
		CardBean queryModel=new CardBean();
		queryModel.setStatus(1);
		queryModel.setCard_status(0);
		List<CardBean> sendSmsList = dao.queryCanLoginList(queryModel);
		int threadNum = 0;
		for (CardBean card : sendSmsList) {
			threadNum++;
			final int currentThreadNum = threadNum;
			ThreadPoolUtils.getSendLoginSmsPool().execute(() -> {
				try {
					logger.info("开启发送app登录验证码子线程" + currentThreadNum + ",手机号:" + card.getCard_no());
					sendLoginSms(card.getId(), card.getCard_no(), card.getProxy_ip(), card.getProxy_port());
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(MessageFormat.format("发送app登录验证码出现异常,手机号:{0}", card.getCard_no()), e);
				} finally {
					logger.info("发送app登录验证码子线程" + currentThreadNum + "已结束！");
				}
			});
		}
		queryModel = new CardBean();
		queryModel.setStatus(1);
		queryModel.setWeb_login_status(0);
		List<CardBean> webSendSmsList = dao.queryCanLoginList(queryModel);
		for (CardBean card : webSendSmsList) {
			threadNum++;
			final int currentThreadNum = threadNum;
			ThreadPoolUtils.getSendLoginSmsPool().execute(() -> {
				try {
					logger.info("开启发送web登录验证码子线程" + currentThreadNum + ",手机号:" + card.getCard_no());
					sendLoginSmsWeb(card.getId(), card.getCard_no(), card.getProxy_ip(), card.getProxy_port());
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(MessageFormat.format("发送web登录验证码出现异常,手机号:{0}", card.getCard_no()), e);
				} finally {
					logger.info("发送web登录验证码子线程" + currentThreadNum + "已结束！");
				}
			});
		}
		while (true) {
	        Thread.sleep(1000);
	        if(ThreadPoolUtils.getSendLoginSmsPool().getActiveCount()==0){  
	        	logger.info("发送app和web登录验证码主线程结束");  
	            break;  
	        }  
		}
	}
	
	@Override
	public boolean sendLoginSmsWeb(Long cardId,String phone,String proxyIp,int proxyProt) throws Exception {
		// TODO Auto-generated method stub
		Map<String,String> headers=new HashMap<String,String>();
		headers.put("Referer", "https://uac.10010.com/portal/homeLoginNew");
		headers.put("User-Agent", UnicomConfig.Web_User_Agent);
		String cookies="ckuuid="+PassWrodsCreater.getUUIDStr();
		String back="jQuery1720" + PassWrodsCreater.getUUID() + "_" + System.currentTimeMillis();
		callback.put(phone, back);
		Response res=Http.create(UnicomConfig.WEB_SEND_LOGIN_SMS_SERVER+"?callback="+back+"&req_time="+System.currentTimeMillis()+"&mobile="+phone+ "&_=" + System.currentTimeMillis()).proxy(proxyIp, proxyProt).heads(headers).cookies(cookies).requestType(Http.RequestType.FORM).get().send().getResponse();
		String body=res.getResult();
		if(res.getStatus()==0||StringUtils.isEmpty(body))
		{
			dao.addProxyFailTimes(cardId);
			return false;
		}
		logger.info("手机号："+phone+",登录验证码发送结果:"+body);
		if(body.contains("\"0000\""))
		{
			CardBean updateModel=new CardBean();
			updateModel.setWeb_login_status(-1);
			updateModel.setId(cardId);
			updateModel.setWeb_ver_code("");
			updateModel.setWeb_update_time(System.currentTimeMillis()/1000);
			dao.update(updateModel);
			return true;
		}
		else if(body.contains("7098"))//web验证码日限制
		{
			CardBean updateModel=new CardBean();
			updateModel.setWeb_login_status(3);
			updateModel.setId(cardId);
			updateModel.setWeb_ver_code("");
			updateModel.setWeb_update_time(System.currentTimeMillis()/1000);
			dao.update(updateModel);
			return false;
		}
		else
		{
			CardBean updateModel=new CardBean();
			updateModel.setWeb_login_status(2);
			updateModel.setId(cardId);
			updateModel.setWeb_update_time(System.currentTimeMillis()/1000);
			dao.update(updateModel);
			return false;
		}
	}

	@Override
	public void cardLoginOnlineTask() throws Exception {
		// TODO Auto-generated method stub
		CardBean queryModel=new CardBean();
		queryModel.setStatus(1);
		//queryModel.setCard_status(1);
		List<CardBean> list=dao.queryList(queryModel);
		if (list!=null&&list.size()>0) {
			for(CardBean card: list) {
				try {
					new Thread(new Runnable() {
						public void run() {
							try {
								if(loginOnLine(card.getId(),card.getCard_no(),card.get_login_token(),card.getProxy_ip(),card.getProxy_port()))
								{
									logger.info(MessageFormat.format("保持登录在线,手机号:{0}，登录状态正常", card.getCard_no()));
								}
								else
								{
									logger.info(MessageFormat.format("保持登录在线,手机号:{0}，登录状态异常", card.getCard_no()));
								}
							} catch (Exception e) {
								e.printStackTrace();
								logger.info(MessageFormat.format("保持登录在线,手机号:{0}，未知错误", card.getCard_no()));
							}
						}
					}).start();
				} catch (Exception e) {
					e.printStackTrace();
					// TODO Auto-generated catch block
					logger.error(MessageFormat.format("保持登录在线,手机号:{0}，登录状态异常", card.getCard_no()));
				}
			}
		}
		queryModel=new CardBean();
		queryModel.setStatus(1);
		queryModel.setWeb_login_status(1);
		List<CardBean> webList=dao.queryList(queryModel);
		if (webList!=null&&webList.size()>0) {
			for(CardBean card: webList) {
				new Thread(new Runnable() {
					public void run() {
						try {
							if(loginOnLineWeb(card.getId(),card.getCard_no(),card.get_web_login_cookie(),card.getProxy_ip(),card.getProxy_port()))
							{
								logger.info(MessageFormat.format("Web保持登录在线,手机号:{0}，登录状态正常", card.getCard_no()));
							}
							else
							{
								logger.info(MessageFormat.format("Web保持登录在线,手机号:{0}，登录状态异常", card.getCard_no()));
							}
						} catch (Exception e) {
							e.printStackTrace();
							logger.error(MessageFormat.format("Web保持登录在线,手机号:{0}，未知错误", card.getCard_no()));
						}
					}
				}).start();
			}
		}
	}

	@Override
	public void cardGetNewUopIdTask() throws Exception {
		// TODO Auto-generated method stub
		CardBean queryModel=new CardBean();
		queryModel.setStatus(1);
		queryModel.setCard_status(1);
		List<CardBean> onlineList=dao.queryList(queryModel);
		for (CardBean card : onlineList) {
			new Thread(new Runnable() {
				public void run() {
					try {
						if (getNewUopIdCookie2(card.getId(), card.getCard_no(), card.get_login_cookie(),
								card.getProxy_ip(), card.getProxy_port())) {
							logger.info(MessageFormat.format("获取最新uopId,手机号:{0},获取成功", card.getCard_no()));
						} else {
							logger.info(MessageFormat.format("获取最新uopId,手机号:{0},获取失败", card.getCard_no()));
						}
					} catch (Exception e) {
						e.printStackTrace();
						logger.error(MessageFormat.format("获取最新uopId,手机号:{0},获取异常", card.getCard_no()));
					}
				}
			}).start();
		}
	}
	
	@Override
	public long updateVerCodeByPhone(String phone, String verCode) {
		// TODO Auto-generated method stub
		return dao.updateVerCodeByPhone(phone, verCode);
	}

	@Override
	public void updateCardProxyTask() throws Exception {
		// TODO Auto-generated method stub
		List<CardBean> list=dao.getNeedUpdateProxyList();
		if(list==null||list.size()==0)return;
		for (CardBean card : list) {
			IpPoolBean ipPool=ipPoolService.getOneCanUseIp("phone");
			if(ipPool==null)return;
			CardBean updateModel=new CardBean();
			updateModel.setId(card.getId());
			updateModel.setProxy_ip(ipPool.getIp());
			updateModel.setProxy_port(ipPool.getPort());
			updateModel.setProxy_expire_time(ipPool.getExpire_time());
			updateModel.setProxy_fail_times(0);
			dao.update(updateModel);
			logger.info("手机号:"+card.getCard_no()+",更新代理IP:("+ipPool.getIp()+":"+ipPool.getPort()+")");
		}
	}

	@Override
	public boolean loginOnLineWeb(Long cardId, String phone, String _web_login_cookie, String proxyIp, int proxyProt)
			throws Exception {
		// TODO Auto-generated method stub
		Map<String,String> header=new HashMap<String,String>();
		header.put("Referer", "https://uac.10010.com/?redirectURL=http%3A%2F%2Fwww.10010.com%2Fnet5%2F");
		header.put("User-Agent", UnicomConfig.Web_User_Agent);
		Response res=Http.create(UnicomConfig.WEB_LOGIN_ONLINE_SERVER).proxy(proxyIp, proxyProt).heads(header).cookies(_web_login_cookie).requestType(Http.RequestType.FORM).get().send().getResponse();
		String body=res.getResult();
		if(res.getStatus()==0||StringUtils.isEmpty(body))
		{
			dao.addProxyFailTimes(cardId);
			return false;
		}
		String _new_web_login_cookie=res.getHeads().get("Set-Cookie")==null?"":res.getHeads().get("Set-Cookie").toString();
		if(StringUtils.isEmpty(_new_web_login_cookie)||!_new_web_login_cookie.contains("JUT"))
		{
			CardBean updateModel=new CardBean();
			updateModel.setWeb_login_status(2);
			updateModel.setId(cardId);
			dao.update(updateModel);
			return false;
			
		}
		else
		{
			CardBean updateModel=new CardBean();
			updateModel.setId(cardId);
			updateModel.set_web_login_cookie(_new_web_login_cookie);
			dao.update(updateModel);
			return true;
		}
	}

	@Override
	public long updateWebVerCodeByPhone(String phone, String verCode) {
		// TODO Auto-generated method stub
		return dao.updateWebVerCodeByPhone(phone, verCode);
	}

	@Override
	public int appAllReLogin(Long groupId) {
		// TODO Auto-generated method stub
		return dao.appAllReLogin(groupId);
	}

	@Override
	public int webAllReLogin(Long groupId) {
		// TODO Auto-generated method stub
		return dao.webAllReLogin(groupId);
	}

	@Override
	public void cardGetEnChannelStrTask() throws Exception {
		// TODO Auto-generated method stub
		dao.updateTimeOutEnChannelStr();
		String ready_order_status=configDao.getValueByKey("ready_order_flag");
		if(!"1".equals(ready_order_status))return;
		List<CardBean> needGetNewEnChannelStrList = dao.getNeedGetNewEnChannelStrList();
		int threadNum = 0;
		for (CardBean card : needGetNewEnChannelStrList) {
			threadNum++;
			final int currentThreadNum = threadNum;
			ThreadPoolUtils.getNewEnChannelStrPool().execute(() -> {
				try {
						logger.info("开启获取最新EnChannelStr子线程"+currentThreadNum+",手机号:"+card.getCard_no());

					} catch (Exception e) {
						e.printStackTrace();
						logger.error(MessageFormat.format("手机号:{0},获取最新EnChannelStr,获取异常", card.getCard_no()));
				}
				finally {
					logger.info("获取最新EnChannelStr子线程"+currentThreadNum+"已结束！");
				}
			});
		}
		while(true){
	        Thread.sleep(1000);
	        if(ThreadPoolUtils.getNewEnChannelStrPool().getActiveCount()==0){
	        	logger.info("获取最新EnChannelStr主线程结束");
	            break;
	        }
		}
	}

	@Override
	public int removeByGroupId(Long groupId) {
		// TODO Auto-generated method stub
		return dao.removeByGroupId(groupId);
	}

	@Override
	public int reSetBuyCardCount() {
		// TODO Auto-generated method stub
		return dao.reSetBuyCardCount();
	}

	@Override
	public int reSetBuyCardStatus() {
		// TODO Auto-generated method stub
		return dao.reSetBuyCardStatus();
	}

}
