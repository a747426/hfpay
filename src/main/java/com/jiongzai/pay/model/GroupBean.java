package com.jiongzai.pay.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jiongzai.pay.util.json.serializer.DateTimeJsonSerializer;

public class GroupBean  implements Serializable{
	
	private static final long serialVersionUID = 4190635485786961542L;

	private Long id;

    private String name;
    
    private String remarks;
    
    @JsonSerialize(using = DateTimeJsonSerializer.class )
    private Long create_time;
    
    private String timeStr;
    
    private Integer status;
    
    private Integer dispatch_order_status;
    
    private Integer card_count;
    
    private Integer work_card_count;
    
    private Double all_income;
    
    private Double today_income;
    
    private Double yesterday_income;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getCard_count() {
		return card_count;
	}

	public void setCard_count(Integer card_count) {
		this.card_count = card_count;
	}

	public Integer getWork_card_count() {
		return work_card_count;
	}

	public void setWork_card_count(Integer work_card_count) {
		this.work_card_count = work_card_count;
	}

	public Double getAll_income() {
		return all_income;
	}

	public void setAll_income(Double all_income) {
		this.all_income = all_income;
	}

	public Double getToday_income() {
		return today_income;
	}

	public void setToday_income(Double today_income) {
		this.today_income = today_income;
	}

	public Double getYesterday_income() {
		return yesterday_income;
	}

	public void setYesterday_income(Double yesterday_income) {
		this.yesterday_income = yesterday_income;
	}

	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}

	public String getTimeStr() {
		return timeStr;
	}

	public void setTimeStr(String timeStr) {
		this.timeStr = timeStr;
	}

	public Integer getDispatch_order_status() {
		return dispatch_order_status;
	}

	public void setDispatch_order_status(Integer dispatch_order_status) {
		this.dispatch_order_status = dispatch_order_status;
	}
    
}