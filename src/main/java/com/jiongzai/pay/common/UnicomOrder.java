package com.jiongzai.pay.common;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jiongzai.pay.util.json.serializer.DateTimeJsonSerializer;

public class UnicomOrder {
	private String orderId;
	private Integer amount;
	private String orderState;
	@JsonSerialize(using = DateTimeJsonSerializer.class )
	private Long orderTime;
	private Integer channelType;
	public String getOrderId() {
		return orderId;
	}
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	public String getOrderState() {
		return orderState;
	}
	public void setOrderState(String orderState) {
		this.orderState = orderState;
	}
	
	public Long getOrderTime() {
		return orderTime;
	}
	public void setOrderTime(Long orderTime) {
		this.orderTime = orderTime;
	}
	public Integer getChannelType() {
		return channelType;
	}
	public void setChannelType(Integer channelType) {
		this.channelType = channelType;
	}
	
	
}
