package com.jiongzai.pay.service;

import java.util.List;

import com.jiongzai.pay.model.OrderPoolBean;

public interface OrderPoolService extends BaseService<OrderPoolBean>{

	int removeByCardId(Long cardId);

	int removeByGroupId(Long groupId);

	OrderPoolBean getOneCanUseOrder(String pay_type,Integer amount);

	boolean getAppNewOrder(Long cardId,String phone,Integer amount,String uopIdCookie,String enChannelStr,String proxyIp,int proxyProt,String webCookie) throws Exception;

	boolean getWebNewOrder(Long cardId,String phone,Integer amount,String uopIdCookie,String enChannelStr,String jutCookie,String proxyIp,int proxyProt) throws Exception;

	boolean getRePayOrder(Long orderPoolId,Long cardId, String unicomOrderId,String pay_type) throws Exception;

	String getNewEnChannelStr(Long cardId,String phone,String uopIdCookie,String proxyIp,int proxyProt)throws Exception;

	void cardGetNewOrderTask()throws Exception;

	void cardRePayOrderTask()throws Exception;

	List<OrderPoolBean> getLeftOrderCount();

	List<OrderPoolBean> getLeftOrderCountByPayType(String pay_type);
}
