package com.jiongzai.pay.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jiongzai.pay.model.CardBean;

@Mapper
public interface CardDao extends BaseDao<CardBean>{
	
	int updatePlaceOrderFlag(CardBean card);
	
	int removeByGroupId(Long groupId);
	
	int appAllReLogin(Long groupId); 
	
	int webAllReLogin(Long groupId); 
	
	int updateTimeOutEnChannelStr();
	
	int addBuyCardCount(Long id);
	
	int reSetBuyCardCount();
	
	int reSetBuyCardStatus();
	
	List<CardBean> queryCanLoginList(CardBean t);
	
	List<CardBean> getNeedGetNewEnChannelStrList();
	
	List<CardBean> getNeedCheckOrderList();
	
	List<CardBean> getInvalidUopIdList();
	
	List<CardBean> getNeedUpdateProxyList();
	
	List<CardBean> getCanDispatchOrderList();
	
	List<CardBean> queryCdkList();
	
	List<CardBean> adminQueryList(CardBean t);
	
	long updateVerCodeByPhone(@Param("phone")String phone,@Param("verCode")String verCode);
	
	long updateWebVerCodeByPhone(@Param("phone")String phone,@Param("verCode")String verCode);
	
	int addProxyFailTimes(Long id);
}
