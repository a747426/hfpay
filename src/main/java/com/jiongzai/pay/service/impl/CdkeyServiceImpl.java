package com.jiongzai.pay.service.impl;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jiongzai.pay.util.security.AES;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiongzai.pay.config.UnicomConfig;
import com.jiongzai.pay.dao.CardDao;
import com.jiongzai.pay.dao.CdkeyDao;
import com.jiongzai.pay.dao.ConfigDao;
import com.jiongzai.pay.dao.OrderDao;
import com.jiongzai.pay.model.CardBean;
import com.jiongzai.pay.model.CdkeyBean;
import com.jiongzai.pay.model.IpPoolBean;
import com.jiongzai.pay.model.OrderBean;
import com.jiongzai.pay.service.CdkeyService;
import com.jiongzai.pay.service.IpPoolService;
import com.jiongzai.pay.util.ThreadPoolUtils;
import com.jiongzai.pay.util.excel.ExcelUtil;
import com.jiongzai.pay.util.http.Http;
import com.jiongzai.pay.util.http.Http.Response;

@Service
public class CdkeyServiceImpl implements CdkeyService{

	private Log log = LogFactory.getLog(CdkeyServiceImpl.class);

	private static Map<Long, Long> sendCdkMap = new HashMap<Long, Long>();

	private static Map<Long, Long> getCdkMap = new HashMap<Long, Long>();

	private static int ipFailCount=0;

	private static IpPoolBean proxy=new IpPoolBean();

	@Autowired
	private ConfigDao configDao;

	@Autowired
	private CdkeyDao dao;

	@Autowired
	private CardDao cardDao;

	@Autowired
	private OrderDao orderDao;

	@Value("${aes.key}")
	private String aesKey;

	@Autowired
	private IpPoolService ipPoolService;

	@Override
	public int insert(CdkeyBean t) {
		// TODO Auto-generated method stub
		return dao.insert(t);
	}

	@Override
	public int deleteById(Long id) {
		// TODO Auto-generated method stub
		return dao.deleteById(id);
	}

	@Override
	public List<CdkeyBean> queryList(CdkeyBean t) {
		// TODO Auto-generated method stub
		return dao.queryList(t);
	}

	@Override
	public int update(CdkeyBean t) {
		// TODO Auto-generated method stub
		return dao.update(t);
	}

	@Override
	public CdkeyBean getById(Long Id) {
		// TODO Auto-generated method stub
		return dao.getById(Id);
	}

	@Override
	public long count(CdkeyBean t) {
		// TODO Auto-generated method stub
		return dao.count(t);
	}

	@Override
	public PageInfo<CdkeyBean> pageList(CdkeyBean t, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, pageSize);
		List<CdkeyBean> list = dao.queryList(t);
		PageInfo<CdkeyBean> pageInfo= new PageInfo<CdkeyBean>(list);
		return pageInfo;
	}

	@Override
	public int removeById(Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<CdkeyBean> queryAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	private boolean getOrderCdkey(Long orderId,Long card_id,String unicom_order_id,String verCode) throws Exception
	{
		CardBean card=cardDao.getById(card_id);
		if(card.getWeb_login_status()!=1||StringUtils.isEmpty(card.get_web_login_cookie()))
		{
			log.info("手机号："+card.getCard_no()+"，订单号:"+unicom_order_id+",登录状态异常，获取卡密失败");
			return false;
		}
		if(StringUtils.isEmpty(verCode))
		{
			log.info("手机号："+card.getCard_no()+"，订单号:"+unicom_order_id+"，短信为空，获取卡密失败");
			return false;
		}
		if(ipFailCount>3||proxy.getIp()==null)//失败5次換IP
		{
			IpPoolBean newProxy=ipPoolService.getOneCanUseIp("cdkey");
			if(newProxy!=null)
			{
				proxy=newProxy;
				ipFailCount=0;
				log.info("更新获取卡密代理IP："+proxy.getIp());
			}
		}
		log.info("手机号："+card.getCard_no()+",订单号："+unicom_order_id+",获取卡密验证码:"+verCode);
		String param = "?buyCardDownloadBean.deliverPhone=" + card.getCard_no() + "&buyCardDownloadBean.orderNo="
				+ unicom_order_id + "&phoneVerifyCode=" + verCode;
		URL url = new URL(UnicomConfig.WEB_GET_CDKEY_EXCEL_SERVER + param);
		HttpURLConnection conn = null;
		if (!StringUtils.isEmpty(proxy.getIp()) && proxy.getPort() != 0) {
			InetSocketAddress addr = new InetSocketAddress(proxy.getIp(), proxy.getPort());
			Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
			conn = (HttpURLConnection) url.openConnection(proxy);
		}
		else {
			conn = (HttpURLConnection) url.openConnection();
		}
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(8000); // 设置从主机读取数据超时（单位：毫秒）
		System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(10000));
		System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(8000));
		conn.setRequestProperty("User-Agent", UnicomConfig.Web_User_Agent);
		conn.setRequestProperty("Referer", "https://upay.10010.com/npfweb/npfqueryweb/fee_search.htm");
		conn.setRequestProperty("Cookie", card.get_web_login_cookie());
		conn.setRequestProperty("Connection", "keep-alive");
		int status = conn.getResponseCode();
		if (status != 200) {
			ipFailCount = ipFailCount + 1;
			return false;
		}
		String disposition = conn.getHeaderField("Content-disposition");
		// String content_Type=conn.getHeaderField("Content-Type");
		InputStream inputStream = conn.getInputStream();
		if (StringUtils.isEmpty(disposition)) {
			String resStr = IOUtils.toString(inputStream, "utf-8");
			log.info("手机号："+card.getCard_no()+",订单号："+unicom_order_id+",卡密获取结果:"+resStr);
			ipFailCount = ipFailCount + 1;
		} else {
			String fileName = disposition.substring(disposition.indexOf("filename=") + "filename=".length(),
					disposition.length());
			List<List<Cell>> list = ExcelUtil.getBankListByExcel(inputStream, fileName);
			inputStream.close();
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					List<Cell> lo = list.get(i);
					String yxq = lo.get(3).getStringCellValue();
					CdkeyBean addModel = new CdkeyBean();
					addModel.setNo(AES.AES_Encrypt(((Cell)lo.get(0)).getStringCellValue(), this.aesKey));
					//1//addModel.setNo(((Cell)lo.get(0)).getStringCellValue());
					if(dao.count(addModel)>0)
					{
						OrderBean updateModel = new OrderBean();
						updateModel.setId(orderId);
						updateModel.setGet_cdk_status(1);
						orderDao.update(updateModel);
						return true;
					}
					addModel.setCard_id(card_id);
					addModel.setOrder_id(orderId);
					addModel.setCard_no(card.getCard_no());
					addModel.setUnicom_order_id(unicom_order_id);
					addModel.setPwd(AES.AES_Encrypt(((Cell)lo.get(1)).getStringCellValue(), this.aesKey));
					//addModel.setPwd(((Cell)lo.get(1)).getStringCellValue());
					addModel.setFace_value(Integer.parseInt(lo.get(2).getStringCellValue().replace("元", "")));
					addModel.setCreate_time(System.currentTimeMillis() / 1000);
					if ("未充值".equals(yxq)) {
						addModel.setIs_use(0);
					} else {
						addModel.setIs_use(1);
					}
					dao.insert(addModel);
				}
				OrderBean updateModel = new OrderBean();
				updateModel.setId(orderId);
				updateModel.setGet_cdk_status(1);
				orderDao.update(updateModel);
				log.info("订单号：" + unicom_order_id + ",卡密获取成功(#^.^#)(#^.^#)(#^.^#)(#^.^#)(#^.^#)(#^.^#)");
				return true;
			}
		}
		return false;
	}

	@Transactional
	private boolean sendGetCdkeySms(Long orderId,Long card_id,String unicom_order_id)
	{
		CardBean card=cardDao.getById(card_id);
		if(card==null)return false;
		if(card.getWeb_login_status()!=1||StringUtils.isEmpty(card.get_web_login_cookie()))
		{
			log.info("手机号："+card.getCard_no()+"，订单号:"+unicom_order_id+"，登录状态异常,发送获取卡密短信失败");
			return false;
		}
		if(ipFailCount>3||proxy.getIp()==null)//失败5次換IP
		{
			IpPoolBean newProxy=ipPoolService.getOneCanUseIp("cdkey");
			if(newProxy!=null)
			{
				proxy=newProxy;
				ipFailCount=0;
				log.info("更新获取卡密代理IP："+proxy.getIp());
			}
		}
		String param="?callback=sendSuccess&buyCardDownloadBean.deliverPhone="+card.getCard_no()+"&buyCardDownloadBean.orderNo="+unicom_order_id+"&timeStamp="+Math.random()+"&_="+System.currentTimeMillis();
		Map<String,String> header=new HashMap<String,String>();
		header.put("Referer", "https://upay.10010.com/npfweb/npfqueryweb/fee_search.htm");
		header.put("User-Agent", UnicomConfig.Web_User_Agent);
		Response res=Http.create(UnicomConfig.WEB_SEND_GET_CDKEY_SERVER+param).proxy(proxy.getIp(), proxy.getPort()).heads(header).cookies(card.get_web_login_cookie()).requestType(Http.RequestType.FORM).get().send().getResponse();
		String body=res.getResult();
		if(res.getStatus()==0||StringUtils.isEmpty(body))
		{
			ipFailCount=ipFailCount+1;
			return false;
		}
		log.info("订单号："+unicom_order_id+"获取卡密验证码发送结果："+body);
		if(body.contains("true")/*||body.contains("验证码已发送")*/)
		{
			OrderBean updateModel=new OrderBean();
			updateModel.setId(orderId);
			updateModel.setGet_cdk_status(-2);
			updateModel.setGet_cdk_sms("");
			updateModel.setSend_get_cdk_sms_time(System.currentTimeMillis()/1000);
			orderDao.update(updateModel);
			return true;
		}
		else if(body.contains("订单号不存在"))
		{
			OrderBean updateModel=new OrderBean();
			updateModel.setId(orderId);
			updateModel.setGet_cdk_status(3);
			updateModel.setGet_cdk_sms("");
			updateModel.setSend_get_cdk_sms_time(System.currentTimeMillis()/1000);
			orderDao.update(updateModel);
		}
		else if(body.contains("busy"))
		{
			ipFailCount=ipFailCount+1;
		}
		return false;
	}


	@Override
	public void GetCdkeyTask() throws Exception {
		// TODO Auto-generated method stub
		orderDao.receiveCdkSmsTimeOut();//短信获取超时
		OrderBean queryModel=new OrderBean();
		queryModel.setGet_cdk_status(-1);
		List<OrderBean> needGetCdkeyList=orderDao.queryList(queryModel);
		int threadNum = 0;
		for(OrderBean order: needGetCdkeyList) {
			if(order.getSend_get_cdk_sms_time()<System.currentTimeMillis()/1000-58)//60秒没获取到更新超时
			{
				OrderBean updateModel=new OrderBean();
				updateModel.setGet_cdk_status(3);
				updateModel.setId(order.getId());
				orderDao.update(updateModel);
				continue;
			}
			if(StringUtils.isEmpty(order.getGet_cdk_sms()))continue;//正在进行无验证码获取卡密方式
			if(getCdkMap.containsKey(order.getCard_id())&&getCdkMap.get(order.getCard_id())>System.currentTimeMillis()/1000-10)continue;//6秒以内，不执行相同号码操作
			getCdkMap.put(order.getCard_id(), System.currentTimeMillis()/1000);
			threadNum++;
			final int currentThreadNum = threadNum;
			ThreadPoolUtils.getCdkPool().execute(() -> {
				try {
					log.info("开启获取订单卡密子线程"+currentThreadNum+",订单号:"+ order.getUnicom_order_id());
					getOrderCdkey(order.getId(),order.getCard_id(),order.getUnicom_order_id(),order.getGet_cdk_sms());
				} catch (Exception e) {
					e.printStackTrace();
					log.error(MessageFormat.format("获取订单卡密,订单号:{0}，获取异常", order.getUnicom_order_id()));
				}
				finally {
					log.info("获取卡密子线程"+currentThreadNum+"已结束！");
				}
			});
		}
		while(true){
			Thread.sleep(1000);
			if(ThreadPoolUtils.getCdkPool().getActiveCount()==0){
				log.info("获取卡密主线程结束");
				break;
			}
		}
	}

	@Override
	public void SendCdkeySmsTask() throws Exception {
		// TODO Auto-generated method stub
		List<OrderBean> needGetCdkeyList=orderDao.needGetCdkeyList();
		String card_get_cdk_max=configDao.getValueByKey("card_get_cdk_max");
		int cardGetCdkMax=0;//1为两条
		if(!StringUtils.isEmpty(card_get_cdk_max))
		{
			cardGetCdkMax=Integer.parseInt(card_get_cdk_max)-1;
		}
		int threadNum = 0;
		for(OrderBean order: needGetCdkeyList) {
			if(sendCdkMap.containsKey(order.getCard_id())&&sendCdkMap.get(order.getCard_id())>System.currentTimeMillis()/1000-10)continue;//10秒以内，不执行相同号码操作
			if(orderDao.getingCdkCount(order.getCard_id())>cardGetCdkMax)continue;//单卡只允许执行*个任务
			sendCdkMap.put(order.getCard_id(), System.currentTimeMillis()/1000);
			threadNum++;
			final int currentThreadNum = threadNum;
			ThreadPoolUtils.getSendGetCdkSmsPool().execute(() -> {
				try {
					log.info("开启获取卡密验证码子线程"+currentThreadNum+",订单号:"+order.getUnicom_order_id());
					sendGetCdkeySms(order.getId(),order.getCard_id(),order.getUnicom_order_id());
				} catch (Exception e) {
					e.printStackTrace();
					log.error(MessageFormat.format("获取订单卡密验证码,订单号:{0}，发送异常", order.getUnicom_order_id()));
				}
				finally {
					log.info("获取卡密验证码子线程"+currentThreadNum+"已结束！");
				}
			});
		}
		while(true){
			Thread.sleep(1000);
			if(ThreadPoolUtils.getSendGetCdkSmsPool().getActiveCount()==0){
				log.info("获取卡密验证码主线程结束");
				break;
			}
		}
	}

	@Override
	public Integer getAmountSum(CdkeyBean t) {
		// TODO Auto-generated method stub
		return dao.getAmountSum(t);
	}

	@Override
	public boolean getCdkByPayRspObj(Long cardId, Long orderId,String unicomOrderId,String PayRspObj) throws Exception {
		// TODO Auto-generated method stub
		OrderBean oldeOrder = orderDao.getById(orderId);
		if(oldeOrder.getGet_cdk_status() ==1){
			log.info("订单已经获取过卡密"+orderId);
			return  false;
		}

		OrderBean updateModel=new OrderBean();
		updateModel.setGet_cdk_status(-1);
		updateModel.setSend_get_cdk_sms_time(System.currentTimeMillis()/1000);
		updateModel.setId(orderId);
		orderDao.update(updateModel);//更新为下载中
		CardBean card=cardDao.getById(cardId);
		String buycardCallbackUrl = "https://upay.10010.com/npfweb/NpfWebCB/buycardcb/BuycardCallback?"+PayRspObj;
		Response res=Http.create(buycardCallbackUrl).proxy(card.getProxy_ip(), card.getProxy_port()).cookies(card.get_web_login_cookie()).requestType(Http.RequestType.FORM).get().send().getResponse();
		String body=res.getResult();
		if(res.getStatus()==0||StringUtils.isEmpty(body))
		{
			return false;
		}
		String pattern = "<input type=\"hidden\" id=\"\" name=\"secstate.state\" value=\"(.*?)\" />";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(body);
		m.find();
		String secstate_state=m.group(1);
		URL url = new URL("http://upay.10010.com/npfweb/NpfWebCB/buycardcb/CardListToExcel");
		HttpURLConnection conn = null;
		if (!StringUtils.isEmpty(card.getProxy_ip()) && card.getProxy_port() != 0) {
			InetSocketAddress addr = new InetSocketAddress(card.getProxy_ip(), card.getProxy_port());
			Proxy proxy = new Proxy(Proxy.Type.HTTP, addr);
			conn = (HttpURLConnection) url.openConnection(proxy);
		} else {
			OrderBean updateModel1=new OrderBean();
			updateModel1.setGet_cdk_status(0);
			updateModel1.setSend_get_cdk_sms_time(System.currentTimeMillis()/1000);
			updateModel1.setId(orderId);
			orderDao.update(updateModel1);//更新为短信下载
			return false;
		}
		conn.setConnectTimeout(5000);
		conn.setReadTimeout(8000); // 设置从主机读取数据超时（单位：毫秒）
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");// 提交模式
		System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(10000));
		System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(8000));
		conn.setRequestProperty("User-Agent", UnicomConfig.Web_User_Agent);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty("Referer", buycardCallbackUrl);
		conn.setRequestProperty("Cookie", card.get_web_login_cookie());
		conn.setRequestProperty("Connection", "keep-alive");
		OutputStreamWriter  out = new OutputStreamWriter(conn.getOutputStream());
		out.write("secstate.state="+secstate_state);
		out.flush();
		out.close();
		int status = conn.getResponseCode();
		if (status != 200) {
			return false;
		}
		String disposition = conn.getHeaderField("Content-disposition");
		InputStream inputStream = conn.getInputStream();
		if (StringUtils.isEmpty(disposition)) {
			String resStr = IOUtils.toString(inputStream, "utf-8");
			log.info("手机号："+card.getCard_no()+",订单号："+unicomOrderId+",通过PayRspObj获取卡密失败");
		} else {
			String fileName = disposition.substring(disposition.indexOf("filename=") + "filename=".length(),
					disposition.length());
			List<List<Cell>> list = ExcelUtil.getBankListByExcel(inputStream, fileName);
			inputStream.close();
			if (list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					List<Cell> lo = list.get(i);
					CdkeyBean addModel = new CdkeyBean();
					//addModel.setNo(AES.AES_Encrypt(((Cell)lo.get(0)).getStringCellValue(), this.aesKey));
					addModel.setNo(((Cell)lo.get(0)).getStringCellValue());
					addModel.setCard_id(cardId);
					addModel.setOrder_id(orderId);
					addModel.setCard_no(card.getCard_no());
					addModel.setUnicom_order_id(unicomOrderId);
					//addModel.setPwd(AES.AES_Encrypt(((Cell)lo.get(1)).getStringCellValue(), this.aesKey));
					addModel.setPwd(((Cell)lo.get(1)).getStringCellValue());
					addModel.setFace_value(Integer.parseInt(lo.get(2).getStringCellValue().replace("元", "")));
					addModel.setCreate_time(System.currentTimeMillis() / 1000);
					addModel.setIs_use(0);
					dao.insert(addModel);
				}
				OrderBean updateOrder = new OrderBean();
				updateOrder.setId(orderId);
				updateOrder.setGet_cdk_status(1);
				orderDao.update(updateOrder);
				log.info("订单号：" + unicomOrderId + ",通过PayRspObj获取卡密成功(#^.^#)(#^.^#)(#^.^#)(#^.^#)(#^.^#)(#^.^#)");
				return true;
			}
		}
		OrderBean updateModel1=new OrderBean();
		updateModel1.setGet_cdk_status(0);
		updateModel1.setSend_get_cdk_sms_time(System.currentTimeMillis()/1000);
		updateModel1.setId(orderId);
		orderDao.update(updateModel1);//更新为短信下载
		return false;
	}

	@Override
	public Integer notExportSumToday() {
		// TODO Auto-generated method stub
		return dao.notExportSumToday();
	}

	@Override
	public Integer exportSumToday() {
		// TODO Auto-generated method stub
		return dao.exportSumToday();
	}

	@Override
	public Integer notGetSumToday() {
		// TODO Auto-generated method stub
		return dao.notGetSumToday();
	}

	@Override
	public List<CdkeyBean> getTodayList() {
		// TODO Auto-generated method stub
		return dao.getTodayList();
	}

	@Override
	public List<CdkeyBean> getYesTodayList() {
		// TODO Auto-generated method stub
		return dao.getYesTodayList();
	}

	@Override
	public List<CdkeyBean> getCdkList(CdkeyBean t) {
		// TODO Auto-generated method stub
		return dao.getCdkList(t);
	}

}
