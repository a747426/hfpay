package com.jiongzai.pay.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jiongzai.pay.util.json.serializer.DateTimeJsonSerializer;

public class OrderPoolBean implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6406013765944608952L;

	private Long id;
	
	private String unicom_order_id;
	
	private String order_no;

	private Long card_id;
	
	private String card_no;
	
    private Integer amount;
    
    private String pay_url;
    
    @JsonSerialize(using = DateTimeJsonSerializer.class )
    private Long create_time;
    
    @JsonSerialize(using = DateTimeJsonSerializer.class )
    private Long update_time;
    
    private Integer is_use;
    
    private Integer status;
    
    private String order_type;
    
    private Integer order_count;
    
    private String wx_h5_return;
    
    private String pay_type;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUnicom_order_id() {
		return unicom_order_id;
	}

	public void setUnicom_order_id(String unicom_order_id) {
		this.unicom_order_id = unicom_order_id;
	}

	public String getOrder_no() {
		return order_no;
	}

	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}

	public Long getCard_id() {
		return card_id;
	}

	public void setCard_id(Long card_id) {
		this.card_id = card_id;
	}

	public Integer getAmount() {
		return amount;
	}

	public void setAmount(Integer amount) {
		this.amount = amount;
	}


	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}


	public Integer getIs_use() {
		return is_use;
	}

	public void setIs_use(Integer is_use) {
		this.is_use = is_use;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getOrder_count() {
		return order_count;
	}

	public void setOrder_count(Integer order_count) {
		this.order_count = order_count;
	}

	public Long getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Long update_time) {
		this.update_time = update_time;
	}

	public String getOrder_type() {
		return order_type;
	}

	public void setOrder_type(String order_type) {
		this.order_type = order_type;
	}

	public String getWx_h5_return() {
		return wx_h5_return;
	}

	public void setWx_h5_return(String wx_h5_return) {
		this.wx_h5_return = wx_h5_return;
	}

	public String getPay_url() {
		return pay_url;
	}

	public void setPay_url(String pay_url) {
		this.pay_url = pay_url;
	}

	public String getPay_type() {
		return pay_type;
	}

	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

}