package com.jiongzai.pay.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jiongzai.pay.model.OrderBean;

@Mapper
public interface OrderDao extends BaseDao<OrderBean>{
	int updateGetCdkSmsBySendTime(@Param("phone")String phone,@Param("verCode")String verCode);
	
	int receiveCdkSmsTimeOut();
	
	long getingCdkCount(Long cardId);
	
	int reGetAll();
	
	List<OrderBean> needGetCdkeyList();
	
	List<OrderBean> getNeedCheckOrderList();
	
	void successByUnicomOrderId(String unicomOrderId);
	
	OrderBean selectLastByUnicomOrderId(String unicomOrderId);
	
	double sumMoney(OrderBean order);
	
	List<OrderBean> queryDayFinanceList(OrderBean order);
	
	List<OrderBean> notGetCdkPageList(OrderBean order);
}
