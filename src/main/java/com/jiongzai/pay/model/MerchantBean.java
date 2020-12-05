package com.jiongzai.pay.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jiongzai.pay.util.json.serializer.DateTimeJsonSerializer;

public class MerchantBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2052826072025474242L;
	
	private Long id;
	private String nick_name;
	private String pay_key;
	@JsonSerialize(using = DateTimeJsonSerializer.class )
	private Long create_time;
	private Integer status;
	private String remarks;
	private Integer today_income;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNick_name() {
		return nick_name;
	}
	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}
	public String getPay_key() {
		return pay_key;
	}
	public void setPay_key(String pay_key) {
		this.pay_key = pay_key;
	}
	public Long getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Integer getToday_income() {
		return today_income;
	}
	public void setToday_income(Integer today_income) {
		this.today_income = today_income;
	}

}
