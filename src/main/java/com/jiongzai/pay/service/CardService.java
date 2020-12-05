package com.jiongzai.pay.service;

import com.jiongzai.pay.model.CardBean;

public interface CardService extends BaseService<CardBean>{
	
	int reSetBuyCardCount();
	
	int reSetBuyCardStatus();
	
	int removeByGroupId(Long groupId);
	
	int appAllReLogin(Long groupId); 
	
	int webAllReLogin(Long groupId); 
	
	long updateVerCodeByPhone(String phone,String verCode);
	
	long updateWebVerCodeByPhone(String phone,String verCode);
	
	boolean sendLoginSms(Long cardId,String phone,String proxyIp,int proxyProt)throws Exception;
	
	boolean sendLoginSmsWeb(Long cardId,String phone,String proxyIp,int proxyProt)throws Exception;
	
	boolean cardLogin(Long cardId,String phone,String verCode,String proxyIp,int proxyProt)throws Exception;
	
	boolean cardLoginWeb(Long cardId,String phone,String verCode,String proxyIp,int proxyProt)throws Exception;
	
	boolean loginOnLine(Long cardId,String phone,String loginToken,String proxyIp,int proxyProt) throws Exception;
	
	boolean loginOnLineWeb(Long cardId,String phone,String loginToken,String proxyIp,int proxyProt) throws Exception;
	
	boolean getNewUopIdCookie(Long cardId,String phone,String loginCookie,String proxyIp,int proxyProt) throws Exception;
	
	void cardSmsLoginTask()throws Exception;
	
	void sendLoginSmsTask()throws Exception;
	
	void cardLoginOnlineTask()throws Exception;

	void cardGetEnChannelStrTask()throws Exception;
	
	void cardGetNewUopIdTask()throws Exception;
	
	void updateCardProxyTask()throws Exception;
}
