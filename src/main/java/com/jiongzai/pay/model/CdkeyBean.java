package com.jiongzai.pay.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jiongzai.pay.util.json.serializer.DateTimeJsonSerializer;

public class CdkeyBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2474201573109112936L;

	private Long id;

    private Long card_id;

    private String no;
    
    private String pwd;
    
    private Integer face_value;
    
    private Integer status;
    
    @JsonSerialize(using = DateTimeJsonSerializer.class )
    private Long create_time;
    
    @JsonSerialize(using = DateTimeJsonSerializer.class )
    private Long export_time;
    
    private Integer is_use;
    
    private String unicom_order_id;
    
    private Long order_id;
    
    private String card_no;
    
    private Integer group_id;
    
    private String group_name;
    
    private String com;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCard_id() {
		return card_id;
	}

	public void setCard_id(Long card_id) {
		this.card_id = card_id;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public Integer getFace_value() {
		return face_value;
	}

	public void setFace_value(Integer face_value) {
		this.face_value = face_value;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public Integer getGroup_id() {
		return group_id;
	}

	public void setGroup_id(Integer group_id) {
		this.group_id = group_id;
	}

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;
	}

	public String getUnicom_order_id() {
		return unicom_order_id;
	}

	public void setUnicom_order_id(String unicom_order_id) {
		this.unicom_order_id = unicom_order_id;
	}

	public Long getOrder_id() {
		return order_id;
	}

	public void setOrder_id(Long order_id) {
		this.order_id = order_id;
	}

	public Long getExport_time() {
		return export_time;
	}

	public void setExport_time(Long export_time) {
		this.export_time = export_time;
	}
	
}