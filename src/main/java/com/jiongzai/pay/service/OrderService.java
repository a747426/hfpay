package com.jiongzai.pay.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.github.pagehelper.PageInfo;
import com.jiongzai.pay.common.Result;
import com.jiongzai.pay.common.UnicomOrder;
import com.jiongzai.pay.model.OrderBean;

public interface OrderService extends BaseService<OrderBean>{

	int updateGetCdkSmsBySendTime(String phone,String verCode);

	int reGetAll();

	int receiveCdkSmsTimeOut();

	boolean payNotify(Long orderId);

	double sumMoney(OrderBean order);

	PageInfo<OrderBean> pageDayFinanceList(OrderBean t,int pageNum,int pageSize);
	List<OrderBean> queryDayList(OrderBean t);

	PageInfo<OrderBean> notGetCdkPageList(OrderBean t,int pageNum,int pageSize);

	void MerchantNotifyTask()throws Exception;

	List<UnicomOrder> queryOrderApp(Long cardId,String phone,String uopIdCookie,String proxyIp,int proxyProt,int page) throws Exception;

	List<UnicomOrder> queryChargeOrderApp(Long cardId,String phone,String uopIdCookie,String proxyIp,int proxyProt) throws Exception;

	List<UnicomOrder> queryOrderWeb(Long cardId,String phone,String jutCookie,String proxyIp,int proxyProt) throws Exception;

	boolean notifyOrderOnline(Long cardId,String phone,String uopIdCookie,String proxyIp,int proxyProt) throws Exception;

	boolean delOrderOnline(Long cardId,String phone,String uopIdCookie,String proxyIp,int proxyProt,String queryMode) throws Exception;

	boolean delChargeOrderOnline(Long cardId,String phone,String uopIdCookie,String proxyIp,int proxyProt) throws Exception;

	void notifyOrderOnlineTask()throws Exception;

	void delOrderOnlineTask()throws Exception;

	void delChargeOrderOnlineTask()throws Exception;

	Result pay(HttpServletRequest request,String out_order_no,String pay_type,String amount,Long merchantId,String notify_url)throws Exception;
}
