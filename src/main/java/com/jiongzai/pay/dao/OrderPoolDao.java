package com.jiongzai.pay.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.jiongzai.pay.model.OrderPoolBean;

@Mapper
public interface OrderPoolDao extends BaseDao<OrderPoolBean>{
	
	int removeByGroupId(Long groupId);
	
	int removeByCardId(Long cardId);
	
	int updateByUnicomOrderId(String unicomOrderId);
	
	int removeByUnicomOrderId(String unicomOrderId);
	
	int rePayAlipayOrder();
	
	int removeTimeOutOrder();
	
	int setUsedById(Long id);
	
	OrderPoolBean getWechatByUnicomOrderId(String unicomOrderId);
	OrderPoolBean getByUnicomOrderId(String unicomOrderId);
	
	int deleteByUnicomOrderId(String unicomOrderId);
	
	List<OrderPoolBean> getCannotUseOrder();
	
	List<OrderPoolBean> getCannotUseAlipayOrder();
	
	List<OrderPoolBean> getLeftOrderCount();
	
	List<OrderPoolBean> getLeftOrderCountByPayType(String pay_type);
	
	OrderPoolBean getOneCanUseOrder(@Param("pay_type")String pay_type,@Param("amount")Integer amount);

	OrderPoolBean getOneCanUseOrder1(@Param("pay_type")String pay_type,@Param("amount")Integer amount);
}
