package com.jiongzai.pay.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jiongzai.pay.util.json.serializer.DateTimeJsonSerializer;

public class OrderBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1691083558563504397L;

	private Long id;
	
	private Integer amount;
	
	private Integer status;
	
	private String pay_type;
	
	@JsonSerialize(using = DateTimeJsonSerializer.class )
	private Long pay_time;
	
	private Long merchant_id;
	
	private String merchant_order_no;
	
	private Integer notify_status;
	
	private String notify_url;
	
	private Long card_id;
	
	private Long group_id;
	
	private String group_name;
	
	@JsonSerialize(using = DateTimeJsonSerializer.class )
	private Long create_time;
	
	@JsonSerialize(using = DateTimeJsonSerializer.class )
	private Long notify_time;
	
	@JsonSerialize(using = DateTimeJsonSerializer.class )
	private Long send_get_cdk_sms_time;
	
	private String pay_url;
	
	private Long total_amount;
	
	private Long today_amount;
	
	private Long today_wechat_amount;
	
	private Long today_alipay_amount;
	
	private Long all_sum;
	
	private Long all_count;
	
	private Long success_count;
	
	private Long success_sum;
	
	private String time;
	
	private String card_no;
	
	private String unicom_order_id;
	
	private Integer notify_times;
	
	private Integer get_cdk_status;
	
	private String get_cdk_sms;
	
	private String wx_h5_return;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public Long getPay_time() {
		return pay_time;
	}

	public void setPay_time(Long pay_time) {
		this.pay_time = pay_time;
	}

	public Long getMerchant_id() {
		return merchant_id;
	}

	public void setMerchant_id(Long merchant_id) {
		this.merchant_id = merchant_id;
	}

	public String getMerchant_order_no() {
		return merchant_order_no;
	}

	public void setMerchant_order_no(String merchant_order_no) {
		this.merchant_order_no = merchant_order_no;
	}

	public Integer getNotify_status() {
		return notify_status;
	}

	public void setNotify_status(Integer notify_status) {
		this.notify_status = notify_status;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public Long getCard_id() {
		return card_id;
	}

	public void setCard_id(Long card_id) {
		this.card_id = card_id;
	}

	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}

	public Long getNotify_time() {
		return notify_time;
	}

	public void setNotify_time(Long notify_time) {
		this.notify_time = notify_time;
	}

	public String getPay_url() {
		return pay_url;
	}

	public void setPay_url(String pay_url) {
		this.pay_url = pay_url;
	}

	public Long getTotal_amount() {
		return total_amount;
	}

	public void setTotal_amount(Long total_amount) {
		this.total_amount = total_amount;
	}

	public Long getToday_amount() {
		return today_amount;
	}

	public void setToday_amount(Long today_amount) {
		this.today_amount = today_amount;
	}

	public Long getToday_wechat_amount() {
		return today_wechat_amount;
	}

	public void setToday_wechat_amount(Long today_wechat_amount) {
		this.today_wechat_amount = today_wechat_amount;
	}

	public Long getToday_alipay_amount() {
		return today_alipay_amount;
	}

	public void setToday_alipay_amount(Long today_alipay_amount) {
		this.today_alipay_amount = today_alipay_amount;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public Long getAll_sum() {
		return all_sum;
	}

	public void setAll_sum(Long all_sum) {
		this.all_sum = all_sum;
	}

	public Long getAll_count() {
		return all_count;
	}

	public void setAll_count(Long all_count) {
		this.all_count = all_count;
	}

	public Long getSuccess_count() {
		return success_count;
	}

	public void setSuccess_count(Long success_count) {
		this.success_count = success_count;
	}

	public Long getSuccess_sum() {
		return success_sum;
	}

	public void setSuccess_sum(Long success_sum) {
		this.success_sum = success_sum;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getUnicom_order_id() {
		return unicom_order_id;
	}

	public void setUnicom_order_id(String unicom_order_id) {
		this.unicom_order_id = unicom_order_id;
	}

	public Integer getNotify_times() {
		return notify_times;
	}

	public void setNotify_times(Integer notify_times) {
		this.notify_times = notify_times;
	}

	public Integer getGet_cdk_status() {
		return get_cdk_status;
	}

	public void setGet_cdk_status(Integer get_cdk_status) {
		this.get_cdk_status = get_cdk_status;
	}

	public Long getSend_get_cdk_sms_time() {
		return send_get_cdk_sms_time;
	}

	public void setSend_get_cdk_sms_time(Long send_get_cdk_sms_time) {
		this.send_get_cdk_sms_time = send_get_cdk_sms_time;
	}

	public String getGet_cdk_sms() {
		return get_cdk_sms;
	}

	public void setGet_cdk_sms(String get_cdk_sms) {
		this.get_cdk_sms = get_cdk_sms;
	}

	public String getWx_h5_return() {
		return wx_h5_return;
	}

	public void setWx_h5_return(String wx_h5_return) {
		this.wx_h5_return = wx_h5_return;
	}

	public Long getGroup_id() {
		return group_id;
	}

	public void setGroup_id(Long group_id) {
		this.group_id = group_id;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}
	
	
}
