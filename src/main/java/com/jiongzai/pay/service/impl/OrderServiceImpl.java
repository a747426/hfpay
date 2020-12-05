package com.jiongzai.pay.service.impl;

import java.net.URLDecoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiongzai.pay.common.Result;
import com.jiongzai.pay.common.UnicomOrder;
import com.jiongzai.pay.config.UnicomConfig;
import com.jiongzai.pay.dao.CardDao;
import com.jiongzai.pay.dao.ConfigDao;
import com.jiongzai.pay.dao.MerchantDao;
import com.jiongzai.pay.dao.OrderDao;
import com.jiongzai.pay.dao.OrderPoolDao;
import com.jiongzai.pay.model.CardBean;
import com.jiongzai.pay.model.IpPoolBean;
import com.jiongzai.pay.model.MerchantBean;
import com.jiongzai.pay.model.OrderBean;
import com.jiongzai.pay.model.OrderPoolBean;
import com.jiongzai.pay.service.CdkeyService;
import com.jiongzai.pay.service.IpPoolService;
import com.jiongzai.pay.service.OrderService;
import com.jiongzai.pay.util.HttpUtil;
import com.jiongzai.pay.util.ThreadPoolUtils;
import com.jiongzai.pay.util.date.DateTimeUtil;
import com.jiongzai.pay.util.date.DateUtil;
import com.jiongzai.pay.util.http.Http;
import com.jiongzai.pay.util.http.Http.Response;
import com.jiongzai.pay.util.json.JSONUtil;
import com.jiongzai.pay.util.security.MD5;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

@Service
public class OrderServiceImpl implements OrderService {

	private Log log = LogFactory.getLog(OrderServiceImpl.class);

	public static int ipFailCount1 = 0;

	public static IpPoolBean proxy1 = new IpPoolBean();

	public static int ipFailCount2 = 0;

	public static IpPoolBean proxy2 = new IpPoolBean();

	public static int ipFailCount3 = 0;

	public static IpPoolBean proxy3 = new IpPoolBean();

	@Autowired
	private OrderDao dao;

	@Autowired
	private MerchantDao merchantDao;

	@Autowired
	private CardDao cardDao;

	@Autowired
	private OrderPoolDao orderPoolDao;

	@Autowired
	private IpPoolService ipPoolService;

	@Autowired
	private ConfigDao configDao;

	@Autowired
	private CdkeyService cdkeyService;

	@Override
	public int insert(OrderBean t) {
		// TODO Auto-generated method stub
		return dao.insert(t);
	}

	@Override
	public int deleteById(Long id) {
		// TODO Auto-generated method stub
		return dao.deleteById(id);
	}

	@Override
	public List<OrderBean> queryList(OrderBean t) {
		// TODO Auto-generated method stub
		return dao.queryList(t);
	}

	@Override
	public int update(OrderBean t) {
		// TODO Auto-generated method stub
		return dao.update(t);
	}

	@Override
	public OrderBean getById(Long Id) {
		// TODO Auto-generated method stub
		return dao.getById(Id);
	}

	@Override
	public long count(OrderBean t) {
		// TODO Auto-generated method stub
		return dao.count(t);
	}

	@Override
	public PageInfo<OrderBean> pageList(OrderBean t, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, pageSize);
		List<OrderBean> list = dao.queryList(t);
		PageInfo<OrderBean> pageInfo = new PageInfo<OrderBean>(list);
		return pageInfo;
	}

	@Override
	public int removeById(Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<OrderBean> queryAll() {
		// TODO Auto-generated method stub
		return null;
	}


	@Transactional
	@Override
	public boolean payNotify(Long orderId) {
		OrderBean order = this.getById(orderId);
		if (order == null) {
			log.info("没有找到对应对单");
			return false;
		}
		if(order.getStatus() == 2){
			log.info("超时不回调");
			return false;
		}
		if (order.getStatus() == 1 && order.getNotify_status() == 1) {
			return false;
		} else if (order.getStatus() == 0) {
			OrderBean updateOrder = new OrderBean();
			updateOrder.setPay_time(System.currentTimeMillis() / 1000);
			updateOrder.setStatus(1);
			updateOrder.setId(order.getId());
			this.update(updateOrder);
			order.setStatus(1);
		}
		OrderBean updateOrder = new OrderBean();
		updateOrder.setId(order.getId());
		updateOrder.setNotify_times(order.getNotify_times() + 1);
		dao.update(updateOrder);
		MerchantBean merchant = merchantDao.getById(order.getMerchant_id());
		new Thread(new Runnable() {
			public void run() {
				try {
					Map<String, String> params = new HashMap<String, String>();
					params.put("order_no", order.getId().toString());
					params.put("merchant_no", order.getMerchant_id().toString());
					params.put("out_order_no", order.getMerchant_order_no());
					params.put("amount", order.getAmount().toString());
					params.put("pay_type", order.getPay_type());
					params.put("code", order.getStatus().toString());
					String md5 = MD5.getMD5("" + order.getId() + order.getMerchant_id() + order.getMerchant_order_no()
							+ order.getAmount() + order.getPay_type() + order.getStatus() + merchant.getPay_key());
					params.put("sign", md5);
					String body = HttpUtil.post(order.getNotify_url(), params);
					if (StringUtils.isEmpty(body)) {
						log.info(MessageFormat.format("回调商戶订单号:{0},网络异常", order.getId()));
						return;
					}
					body = body.replaceAll("\r|\n", "").trim();
					if ("SUCCESS".equals(body) || "success".equals(body)) {
						OrderBean localOrder = new OrderBean();
						localOrder.setId(order.getId());
						localOrder.setNotify_status(1);
						localOrder.setNotify_time(System.currentTimeMillis() / 1000);
						dao.update(localOrder);
						log.info(MessageFormat.format("。。。。。。。。。。。。。。。。。回调商戶订单号:{0}，回调成功。。。。。。。。。。。。。。。。。。。", order.getId()));
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.info(MessageFormat.format("回调商戶订单号:{0}，出现异常", order.getId()));
				}
			}
		}).start();
		return true;
	}

	@Override
	public double sumMoney(OrderBean order) {
		// TODO Auto-generated method stub
		return dao.sumMoney(order);
	}

	@Override
	public PageInfo<OrderBean> pageDayFinanceList(OrderBean t, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, pageSize);
		List<OrderBean> list = dao.queryDayFinanceList(t);
		PageInfo<OrderBean> pageInfo = new PageInfo<OrderBean>(list);
		return pageInfo;
	}

	@Override
	public List<OrderBean> queryDayList(OrderBean t) {
		return null;
	}


	@Override
	public void MerchantNotifyTask() throws Exception {
		// TODO Auto-generated method stub
		OrderBean queryModel = new OrderBean();
		queryModel.setStatus(1);
		queryModel.setNotify_status(0);
		queryModel.setNotify_times(3);
		List<OrderBean> notNotifyList = dao.queryList(queryModel);//回调3次后失败则放弃
		for (OrderBean order : notNotifyList) {
			try {
				payNotify(order.getId());
			} catch (Exception e) {
				e.printStackTrace();
				log.error(MessageFormat.format("商戶订单回调,订单号:{0}，出现异常", order.getId()));
			}
		}
	}


	public void notifyOrderOnlineTask() throws Exception {

		List<OrderBean> needCheckOrderList = this.dao.getNeedCheckOrderList();
		int threadNum = 0;
		for (OrderBean order : needCheckOrderList) {
			int currentThreadNum = ++threadNum;
			ThreadPoolUtils.getCheckOrderOnlinePool().execute(() -> {
				try {
					log.info("开启在线查单子线程" + currentThreadNum + ",联通单号:" + order.getUnicom_order_id());
					notifyOrderOnline_pc(order.getId(), order.getWx_h5_return(), order.getUnicom_order_id(), order.getCard_id());
				} catch (Exception e) {
					e.printStackTrace();
					log.error(MessageFormat.format("在线查单任务,联通单号:{0}，出现异常", order.getUnicom_order_id()));
				} finally {
					log.info("在线查单子线程" + currentThreadNum + "已结束！");
				}
			});
		}
		while (true) {
			Thread.sleep(1000L);
			if (ThreadPoolUtils.getCheckOrderOnlinePool().getActiveCount() == 0) {
				log.info("在线查单主线程结束");
				return;
			}
		}
	}


	public boolean notifyOrderOnline_pc(Long orderId, String wxH5Retrun, String unicomOrderId, Long cardId) throws Exception {
		this.log.info(MessageFormat.format("{0}", new Object[] { unicomOrderId }));
		CardBean byId = (CardBean)this.cardDao.getById(cardId);
		boolean buycard = get_order_check_order(byId.get_uop_id_cookie(), byId.getProxy_ip(), byId.getProxy_port().intValue(), "buycard", unicomOrderId);
		if (buycard) {
			this.orderPoolDao.removeByUnicomOrderId(unicomOrderId);
			payNotify(orderId);
		}
		return buycard;
	}

	private boolean get_order_check_order(String uopIdCookie, String proxyIp, int proxyProt, String queryType, String unicomOrderId) {
		Date date = new Date();
		Map<String, String> bodys = new HashMap<>();
		bodys.put("startDate", "2019-09-01");
		bodys.put("endDate", (new SimpleDateFormat("yyyy-MM-dd")).format(date));
		bodys.put("pageFlag", "1");
		bodys.put("queryType", queryType);
		bodys.put("time", String.valueOf(date.getTime()));
		Http.Response res = Http.create("https://upay.10010.com/npfwap/NpfMobAppQuery/feeSearch/queryOrderNew").proxy(proxyIp, proxyProt).cookies(uopIdCookie).body(bodys).requestType(Http.RequestType.FORM).post().send().getResponse();
		String body = res.getResult();
		if (res.getStatus() == 0 || StringUtils.isEmpty(body))
			return false;
		if (JSONUtil.isJson(body)) {
			JSONObject json = JSONObject.fromObject(body);
			String return_code = json.optString("RETURN_CODE");
			if (!"0000".equalsIgnoreCase(return_code))
				return false;
			JSONArray orderList = json.getJSONArray("orderList");
			if (orderList.isEmpty())
				return false;
			JSONArray jsonArray = orderList.getJSONArray(0);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				JSONObject source = jsonObject.optJSONObject("_source");
				String orderNo = source.optString("orderNo");
				if (unicomOrderId.equalsIgnoreCase(orderNo)) {
					String orderState = source.optString("orderState");
					if ("02".equalsIgnoreCase(orderState))
						return true;
				}
			}
			return false;
		}
		return false;
	}


	@Override
	public void delOrderOnlineTask() throws Exception {
		// TODO Auto-generated method stub
		CardBean queryModel = new CardBean();
		queryModel.setStatus(1);
		List<CardBean> onlineList = cardDao.queryList(queryModel);
		final String query_order_mode = configDao.getValueByKey("query_order_mode");
		int threadNum = 0;
		for (CardBean card : onlineList) {
			threadNum++;
			final int currentThreadNum = threadNum;
			ThreadPoolUtils.getDelOrderOnlinePool().execute(() -> {
				try {
					log.info("开启删除超时购卡订单子线程" + currentThreadNum + ",手机号:" + card.getCard_no());
					delOrderOnline(card.getId(), card.getCard_no(), "1".equals(query_order_mode) ? card.get_web_login_cookie() : card.get_uop_id_cookie(), card.getProxy_ip(), card.getProxy_port(), query_order_mode);
				} catch (Exception e) {
					e.printStackTrace();
					log.error(MessageFormat.format("删除超时购卡订单任务,手机号:{0}，出现异常", card.getCard_no()));
				} finally {
					log.info("删除超时购卡订单子线程" + currentThreadNum + "已结束！");
				}
			});
		}
		while (true) {
			Thread.sleep(1000);
			if (ThreadPoolUtils.getDelOrderOnlinePool().getActiveCount() == 0) {
				log.info("删除超时购卡订单主线程结束");
				break;
			}
		}
	}

	@Override
	public boolean delOrderOnline(Long cardId, String phone, String cookie, String proxyIp, int proxyProt, String queryMode) throws Exception {
		// TODO Auto-generated method stub
		List<UnicomOrder> orderList = "1".equals(queryMode) ? this.queryOrderWeb(cardId, phone, cookie, proxyIp, proxyProt) : this.queryOrderApp(cardId, phone, cookie, proxyIp, proxyProt, 1);
		if (orderList == null || orderList.size() == 0) {
			return false;
		}
		int delAppAmount = 0;
		int delWebAmount = 0;
		for (UnicomOrder unicomOrder : orderList) {
			if (!"02".equals(unicomOrder.getOrderState()) && !"09".equals(unicomOrder.getOrderState()) && unicomOrder.getOrderTime() < (System.currentTimeMillis() / 1000 - 1800))//删除超时订单
			{
				Response res = Http.create(UnicomConfig.DEL_ORDER_SERVER + "?orderNo=" + unicomOrder.getOrderId()).proxy(proxyIp, proxyProt).requestCount(2).requestType(Http.RequestType.FORM).get().send().getResponse();
				String body = res.getResult();
				if (res.getStatus() == 0 || StringUtils.isEmpty(body)) {
					ipFailCount1 = ipFailCount1 + 1;
					break;
				}
				if (JSONUtil.isJson(body) && body.contains("success")) {
					log.info("phone:" + phone + ",删除购卡超时订单:" + unicomOrder.getOrderId());
					if (unicomOrder.getChannelType() == 308)
						delAppAmount += unicomOrder.getAmount();
					else if (unicomOrder.getChannelType() == 101)
						delWebAmount += unicomOrder.getAmount();
				}
			}
		}
		if (delAppAmount > 0) {
			log.info("phone:" + phone + ",App端释放购卡额度:" + delAppAmount);
			CardBean updateModel = new CardBean();
			updateModel.setId(cardId);
			updateModel.setApp_place_order_flag(1);
			cardDao.updatePlaceOrderFlag(updateModel);
		}
		if (delWebAmount > 0) {
			log.info("phone:" + phone + ",Web端释放购卡额度:" + delWebAmount);
			CardBean updateModel = new CardBean();
			updateModel.setId(cardId);
			updateModel.setWeb_place_order_flag(1);
			cardDao.updatePlaceOrderFlag(updateModel);
		}
		return true;
	}

	@Transactional
	@Override
	public boolean notifyOrderOnline(Long cardId, String phone, String cookie, String proxyIp, int proxyProt) throws Exception {
		// TODO Auto-generated method stub
		int page = 1;
		do {
			List<UnicomOrder> orderList = new ArrayList<UnicomOrder>();
			try {
				orderList = this.queryOrderApp(cardId, phone, cookie, proxyIp, proxyProt, page);
			} catch (Exception e) {
				log.info("查单异常"+e.getMessage());
				e.printStackTrace();
			}
			if (orderList == null || orderList.size() == 0) {
				log.info("次号没有下单" + phone);
				break;
			}
			boolean flg = false;
			for (UnicomOrder unicomOrder : orderList) {
				if ("02".equals(unicomOrder.getOrderState()))//更新订单状态
				{
					OrderBean order = dao.selectLastByUnicomOrderId(unicomOrder.getOrderId());
					if (order == null || order.getStatus() == 1) continue;
					log.info("(●'◡'●)(●'◡'●)(●'◡'●)(●'◡'●)(●'◡'●)(●'◡'●)(●'◡'●)(●'◡'●)");
					orderPoolDao.removeByUnicomOrderId(unicomOrder.getOrderId());
					this.payNotify(order.getId());
					flg = true;
					break;
				}
			}
			if (flg) {
				break;
			}
			page++;
		} while (true);
		return true;
	}

	@Override
	public List<UnicomOrder> queryOrderApp(Long cardId, String phone, String uopIdCookie, String proxyIp, int proxyProt, int page) throws Exception {
		// TODO Auto-generated method stub
		//这地方实时性要求高，每次更换一个ip
		if (StringUtils.isEmpty(uopIdCookie)) {
			log.info("手机号：" + phone + ",uopIdCookie为空，在线查单失败");
			return null;
		}
		List<UnicomOrder> orderList = new ArrayList<UnicomOrder>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://upay.10010.com/npfwap/npfMobWeb/query/index.html");
		headers.put("User-Agent", UnicomConfig.User_Agent);

		Map<String, String> params = new HashMap<String, String>();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
		params.put("startDate", "2019-09-01");
		params.put("endDate", df.format(new Date().getTime()));//df.format(new Date().getTime()));
		params.put("pageFlag", "1");
		params.put("time", (System.currentTimeMillis()) + "");
		params.put("queryType", "buycard");
		boolean flag = false;

		params.put("pageFlag", "" + page);
		String url = "https://upay.10010.com/npfwap/NpfMobAppQuery/feeSearch/queryOrderNew";
		Response res = Http.create(url)
				/*.proxy(proxyIp, proxyProt)*/.heads(headers).cookies(uopIdCookie).body(params).requestType(Http.RequestType.FORM).post().send().getResponse();
		String body = res.getResult();

		if (res.getStatus() == 0 || StringUtils.isEmpty(body) || body.contains("频繁")) {
			ipFailCount1 = ipFailCount1 + 1;
			log.info("在线查单代理IP一：" + proxy1.getIp() + "，失败" + ipFailCount1 + "次");
			return null;
		}
		log.info("查询成功");
		flag = false;
		if (body.contains("orderList")) {
			ipFailCount1 = 0;
			JSONObject json = JSONObject.fromObject(body);
			JSONArray array = json.getJSONArray("orderList");
			for (int i = 0; i < array.size(); i++) {
				flag = true;
				try {
					JSONArray orderDataArr = array.getJSONArray(i);
					for (int j=0;j< orderDataArr.size();j++){
						JSONObject orderData =  orderDataArr.getJSONObject(j).getJSONObject("_source");
						UnicomOrder order = new UnicomOrder();
						order.setOrderId(orderData.getString("orderNo"));
						order.setAmount(Integer.parseInt(orderData.getString("topayTotalMoney")) / 100);
						order.setOrderState(orderData.getString("orderState"));
						order.setChannelType(Integer.parseInt(orderData.getString("channelType")));
						String dateStr = orderData.getString("orderTime").replace("T", " ");

						if (StringUtils.isEmpty(orderData)) {
							order.setOrderTime(DateTimeUtil.getTodayUnixTime());
						} else {
							order.setOrderTime(DateTimeUtil.strDate2UnixTime(dateStr));
						}
						orderList.add(order);
					}

				} catch (Exception e) {
					log.info("手机号"+phone+" 查单异常"+e.getMessage());
				}
				//
			}
		} else {
			log.info("手机号：" + phone + "购卡查单异常," + body);

		}
		return orderList;


	}

	@Override
	public synchronized Result pay(HttpServletRequest request, String out_order_no, String pay_type, String amount, Long merchantId, String notify_url) throws Exception {
		// TODO Auto-generated method stub
		Result result = new Result();
		OrderBean queryModel = new OrderBean();
		queryModel.setMerchant_order_no(out_order_no);
		if (dao.count(queryModel) > 0) {
			result.setCode(204);
			result.setMsg("out_order_no已存在");
			result.setData("");
			return result;
		}
		//从订单池获取可用链接
		OrderPoolBean orderPool = orderPoolDao.getOneCanUseOrder(pay_type, Integer.parseInt(amount));
		if (orderPool == null) {
			result.setCode(0);
			result.setMsg("订单库存不足,请联系商家");
			result.setData("");
			return result;
		}

		//查询订单
		OrderBean eorder = dao.selectLastByUnicomOrderId(orderPool.getUnicom_order_id());
        if(eorder != null){
            if(System.currentTimeMillis() / 1000 - eorder.getCreate_time() < 360){
                log.info("出现重单问题"+orderPool.getUnicom_order_id());
                result.setCode(5);
                result.setMsg("出现重单问题"+orderPool.getUnicom_order_id());
                result.setData("");
                return result;
            }else{
                eorder.setStatus(2);
                dao.update(eorder);
            }
        }

		OrderBean order = new OrderBean();
		order.setMerchant_id(merchantId);
		order.setAmount(Integer.parseInt(amount));
		order.setCreate_time(System.currentTimeMillis() / 1000);
		order.setMerchant_order_no(out_order_no);
		order.setPay_type(pay_type);
		order.setCard_id(orderPool.getCard_id());
		order.setCard_no(orderPool.getCard_no());
		order.setNotify_url(notify_url);
		order.setUnicom_order_id(orderPool.getUnicom_order_id());
		order.setPay_url(orderPool.getPay_url());
		order.setWx_h5_return(orderPool.getWx_h5_return());
		dao.insert(order);
		orderPoolDao.deleteByUnicomOrderId(orderPool.getUnicom_order_id());
		orderPoolDao.setUsedById(orderPool.getId());
		Map<String, String> map = new HashMap<String, String>();
		map.put("merchant_no", order.getMerchant_id().toString());
		map.put("order_no", order.getId().toString());
		map.put("out_order_no", order.getMerchant_order_no());
		map.put("amount", order.getAmount().toString());
		map.put("pay_type", order.getPay_type());
		String first_dispatch_type = configDao.getValueByKey("first_dispatch_type");
		if ("1".equals(configDao.getValueByKey("pay_model"))) {
			if("1".equals(first_dispatch_type)){
				String url = order.getPay_url();
				url = URLDecoder.decode(url).replaceAll("&alipayAppCallBackUrl=\"(.*)\"", "");
				String[] urlArr = url.split("&");
				Map<String, String> mapUrl = new HashMap<String, String>();
				for (int i = 0; i < urlArr.length; i++) {
					String[] itemArr = urlArr[i].split("=");
					mapUrl.put(itemArr[0], itemArr[1]);
				}
				url = "";
				url += "_input_charset=" + mapUrl.get("_input_charset") + "&body=" + mapUrl.get("body") + "&it_b_pay=" + mapUrl.get("it_b_pay");
				url += "&notify_url=" + mapUrl.get("notify_url") + "&out_trade_no=" + mapUrl.get("out_trade_no") + "&partner=" + mapUrl.get("partner");
				url += "&payment_type=" + mapUrl.get("payment_type") + "&seller_id=" + mapUrl.get("seller_id") + "&subject=" + mapUrl.get("subject");
				url += "&total_fee=" + mapUrl.get("total_fee") + "&sign=" + mapUrl.get("sign") + "&sign_type=" + mapUrl.get("sign_type");
				url += "&bizcontext=\"{\"an\":\"com.sinovatech.unicom.ui\"}\"";
				map.put("pay_url", url);
			}else {
				map.put("pay_url", HttpUtil.getServerPath(request) + "api/payPage.html?id=" + order.getId());
			}
		} else {
			map.put("pay_url", HttpUtil.getServerPath(request) + "api/payPage.html?id=" + order.getId());
		}
		result.setCode(1);
		result.setMsg("请求成功");
		result.setData(map);
		return result;
	}

	@Override
	public PageInfo<OrderBean> notGetCdkPageList(OrderBean t, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, pageSize);
		List<OrderBean> list = dao.notGetCdkPageList(t);
		PageInfo<OrderBean> pageInfo = new PageInfo<OrderBean>(list);
		return pageInfo;
	}

	@Override
	public int reGetAll() {
		// TODO Auto-generated method stub
		return dao.reGetAll();
	}

	@Override
	public int updateGetCdkSmsBySendTime(String phone, String verCode) {
		// TODO Auto-generated method stub
		return dao.updateGetCdkSmsBySendTime(phone, verCode);
	}

	@Override
	public int receiveCdkSmsTimeOut() {
		// TODO Auto-generated method stub
		return dao.receiveCdkSmsTimeOut();
	}

	@Override
	public List<UnicomOrder> queryOrderWeb(Long cardId, String phone, String jutCookie, String proxyIp, int proxyProt)
			throws Exception {
		// TODO Auto-generated method stub
		//这地方实时性要求高，每次更换一个ip
		if (StringUtils.isEmpty(jutCookie)) {
			log.info("手机号：" + phone + ",jutCookie为空，在线查单失败");
			return null;
		}
		List<UnicomOrder> orderList = new ArrayList<UnicomOrder>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://upay.10010.com/npfweb/npfqueryweb/fee_search.htm");
		headers.put("User-Agent", UnicomConfig.Web_User_Agent);
		Map<String, String> params = new HashMap<String, String>();
		params.put("startDate", DateUtil.getMonthFirstDayStr());
		params.put("endDate", DateUtil.getCurrentDateStr());
		params.put("time", "" + System.currentTimeMillis());
		boolean flag = false;
		int page = 1;
		do {
			params.put("pageFlag", "" + page);
			//Response res = Http.create(UnicomConfig.WEB_QUERY_BUY_CARD_ORDER_SERVER).proxy(proxy1.getIp(), proxy1.getPort()).requestCount(2).cookies(jutCookie).heads(headers).body(params).requestType(Http.RequestType.FORM).post().send().getResponse();

			Response res = Http.create(UnicomConfig.WEB_QUERY_BUY_CARD_ORDER_SERVER)
					.proxy(proxyIp, proxyProt).heads(headers).cookies(jutCookie).body(params).requestType(Http.RequestType.FORM).post().send().getResponse();

			String body = res.getResult();
			if (res.getStatus() == 0 || StringUtils.isEmpty(body) || body.contains("频繁")) {
				ipFailCount1 = ipFailCount1 + 1;
				log.info("在线查单代理IP一：" + proxy1.getIp() + "，失败" + ipFailCount1 + "次");
				break;
			}
			flag = false;
			if (body.contains("hits")) {
				ipFailCount1 = 0;
				JSONObject json = JSONObject.fromObject(body);
				if (json.containsKey("isLogin")) break;
				JSONArray array = json.getJSONObject("hits").getJSONArray("hits");
				for (int i = 0; i < array.size(); i++) {
					flag = true;
					JSONObject orderData = array.getJSONObject(i).getJSONObject("_source");
					UnicomOrder order = new UnicomOrder();
					order.setOrderId(orderData.getString("orderNo"));
					order.setAmount(Integer.parseInt(orderData.getString("topayTotalMoney")) / 100);
					order.setOrderState(orderData.getString("orderState"));
					order.setChannelType(Integer.parseInt(orderData.getString("channelType")));
					order.setOrderTime(DateTimeUtil.strDate2UnixTime(orderData.getString("orderTime").replace("T", " ")));
					orderList.add(order);
				}
			} else {
				log.info("手机号：" + phone + "购卡查单异常," + body);
				break;
			}
			page++;
		} while (flag);
		return orderList;
	}


	@Override
	public void delChargeOrderOnlineTask() throws Exception {
		CardBean queryModel = new CardBean();
		queryModel.setStatus(1);
		List<CardBean> onlineList = cardDao.queryList(queryModel);
		int threadNum = 0;
		for (CardBean card : onlineList) {
			threadNum++;
			final int currentThreadNum = threadNum;
			ThreadPoolUtils.getDelChargeOrderOnlinePool().execute(() -> {
				try {
					log.info("开启删除超时话费订单子线程" + currentThreadNum + ",手机号:" + card.getCard_no());
					delChargeOrderOnline(card.getId(), card.getCard_no(), card.get_uop_id_cookie(), card.getProxy_ip(), card.getProxy_port());
				} catch (Exception e) {
					e.printStackTrace();
					log.error(MessageFormat.format("删除超时话费订单任务,手机号:{0}，出现异常", card.getCard_no()));
				} finally {
					log.info("删除超时话费订单子线程" + currentThreadNum + "已结束！");
				}
			});
		}
		while (true) {
			Thread.sleep(1000);
			if (ThreadPoolUtils.getDelChargeOrderOnlinePool().getActiveCount() == 0) {
				log.info("删除超时话费订单主线程结束");
				break;
			}
		}
	}

	@Override
	public boolean delChargeOrderOnline(Long cardId, String phone, String uopIdCookie, String proxyIp, int proxyProt)
			throws Exception {
		// TODO Auto-generated method stub
		List<UnicomOrder> orderList = this.queryChargeOrderApp(cardId, phone, uopIdCookie, proxyIp, proxyProt);
		if (orderList == null || orderList.size() == 0) {
			return false;
		}
		for (UnicomOrder unicomOrder : orderList) {
			if (!"02".equals(unicomOrder.getOrderState()) && !"09".equals(unicomOrder.getOrderState()) && unicomOrder.getOrderTime() < (System.currentTimeMillis() / 1000 - 1800))//删除超时订单
			{
				Map<String, String> headers = new HashMap<String, String>();
				headers.put("User-Agent", UnicomConfig.User_Agent);
				Response res = Http.create(UnicomConfig.DEL_ORDER_SERVER + "?orderNo=" + unicomOrder.getOrderId()).proxy(proxyIp, proxyProt).heads(headers).requestCount(2).requestType(Http.RequestType.FORM).get().send().getResponse();
				String body = res.getResult();
				if (res.getStatus() == 0 || StringUtils.isEmpty(body)) {
					ipFailCount1 = ipFailCount1 + 1;
					break;
				}
				if (JSONUtil.isJson(body) && body.contains("success")) {
					log.info("phone:" + phone + ",成功删除超时话费订单:" + unicomOrder.getOrderId());
				}
				Thread.sleep(800);
			}
		}
		return true;
	}

	@Override
	public List<UnicomOrder> queryChargeOrderApp(Long cardId, String phone, String uopIdCookie, String proxyIp, int proxyProt) throws Exception {
		// TODO Auto-generated method stub
		if (StringUtils.isEmpty(uopIdCookie)) {
			log.info("手机号：" + phone + ",uopIdCookie为空，在线查单失败");
			return null;
		}
		if (ipFailCount1 > 4 || proxy1.getIp() == null)//使用5次換IP
		{
			IpPoolBean newProxy = ipPoolService.getOneCanUseIp("query");
			if (newProxy != null) {
				proxy1 = newProxy;
				ipFailCount1 = 0;
				log.info("更新在线查单代理IP一：" + proxy1.getIp());
			}
		}
		List<UnicomOrder> orderList = new ArrayList<UnicomOrder>();
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Referer", "https://upay.10010.com/npfwap/npfMobWeb/query/index.html?phone=" + phone);
		headers.put("User-Agent", UnicomConfig.User_Agent);
		Map<String, String> params = new HashMap<String, String>();
		params.put("startDate", DateUtil.getMonthFirstDayStr());
		params.put("endDate", DateUtil.getCurrentDateStr());
		params.put("time", "" + System.currentTimeMillis());
		int page = 1;
		boolean flag = false;
		do {
			params.put("pageFlag", "" + page);
			Response res = Http.create(UnicomConfig.QUERY_BANK_CHARGE_ORDER_SERVER).proxy(proxy1.getIp(), proxy1.getPort()).requestCount(2).cookies(uopIdCookie).heads(headers).body(params).requestType(Http.RequestType.FORM).post().send().getResponse();
			String body = res.getResult();
			if (res.getStatus() == 0 || StringUtils.isEmpty(body) || body.contains("频繁")) {
				ipFailCount1 = ipFailCount1 + 1;
				log.info("在线查单代理IP一：" + proxy1.getIp() + "，失败" + ipFailCount1 + "次");
				break;
			}
			flag = false;
			if (body.contains("hits")) {
				ipFailCount1 = 0;
				JSONObject json = JSONObject.fromObject(body);
				if (json.containsKey("isLogin")) break;
				JSONArray array = json.getJSONObject("hits").getJSONArray("hits");
				for (int i = 0; i < array.size(); i++) {
					flag = true;
					JSONObject orderData = array.getJSONObject(i).getJSONObject("_source");
					UnicomOrder order = new UnicomOrder();
					order.setOrderId(orderData.getString("orderNo"));
					order.setAmount(Integer.parseInt(orderData.getString("topayTotalMoney")) / 100);
					order.setOrderState(orderData.getString("orderState"));
					order.setChannelType(Integer.parseInt(orderData.getString("channelType")));
					try {
						order.setOrderTime(DateTimeUtil.strDate2UnixTime(orderData.getString("orderTime").replace("T", " ")));
					} catch (Exception e) {
						order.setOrderTime(DateTimeUtil.getTodayUnixTime());
					}
					orderList.add(order);
				}
			} else {
				log.info("手机号：" + phone + "话费查单异常," + body);
				break;
			}
			page++;
		} while (flag);
		return orderList;
	}

}
