package com.jiongzai.pay.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jiongzai.pay.util.json.serializer.DateTimeJsonSerializer;

public class CardBean implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6406013765944608952L;

	private Long id;

    private String card_no;

    private String com;
    
    private String _login_cookie;
    
    private String _login_token;
    
    private String _uop_id_cookie;
    
    @JsonSerialize(using = DateTimeJsonSerializer.class )
    private Long create_time;
    
    @JsonSerialize(using = DateTimeJsonSerializer.class )
    private Long update_time;
    
    private Integer status;
    
    private Integer card_status;
    
    private Integer group_id;
    
    private String remarks;
    
    private String pro_name;
    
    private String city_name;
    
    private String group_name;
    
    private String ver_code;
    
    private String proxy_ip;
    
    private Integer proxy_port;
    
    private Long proxy_expire_time;
    
    private Integer proxy_fail_times;
    
    private String web_ver_code;
    
    private Integer web_login_status;
    
    private String _web_login_cookie;
    
    @JsonSerialize(using = DateTimeJsonSerializer.class )
    private Long web_update_time;
    
    private Integer app_place_order_flag;
    
    private Integer web_place_order_flag;
    
    private Integer today_income;
    
    private Integer all_income;
    
    private String en_channel_str;
    
    private Long en_channel_str_update_time;
    
    private Long buy_card_last_time;
    
    private Integer buy_card_count;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String getCom() {
		return com;
	}

	public void setCom(String com) {
		this.com = com;
	}

	public String get_uop_id_cookie() {
		return _uop_id_cookie;
	}

	public void set_uop_id_cookie(String _uop_id_cookie) {
		this._uop_id_cookie = _uop_id_cookie;
	}

	public Long getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Long create_time) {
		this.create_time = create_time;
	}

	public Long getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Long update_time) {
		this.update_time = update_time;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getCard_status() {
		return card_status;
	}

	public void setCard_status(Integer card_status) {
		this.card_status = card_status;
	}

	public Integer getGroup_id() {
		return group_id;
	}

	public void setGroup_id(Integer group_id) {
		this.group_id = group_id;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getGroup_name() {
		return group_name;
	}

	public void setGroup_name(String group_name) {
		this.group_name = group_name;
	}

	public String getVer_code() {
		return ver_code;
	}

	public void setVer_code(String ver_code) {
		this.ver_code = ver_code;
	}

	public String getPro_name() {
		return pro_name;
	}

	public void setPro_name(String pro_name) {
		this.pro_name = pro_name;
	}

	public String getCity_name() {
		return city_name;
	}

	public void setCity_name(String city_name) {
		this.city_name = city_name;
	}

	public String get_login_token() {
		return _login_token;
	}

	public void set_login_token(String _login_token) {
		this._login_token = _login_token;
	}

	public String get_login_cookie() {
		return _login_cookie;
	}

	public void set_login_cookie(String _login_cookie) {
		this._login_cookie = _login_cookie;
	}

	public String getProxy_ip() {
		return proxy_ip;
	}

	public void setProxy_ip(String proxy_ip) {
		this.proxy_ip = proxy_ip;
	}

	public Integer getProxy_port() {
		return proxy_port;
	}

	public void setProxy_port(Integer proxy_port) {
		this.proxy_port = proxy_port;
	}

	public Long getProxy_expire_time() {
		return proxy_expire_time;
	}

	public void setProxy_expire_time(Long proxy_expire_time) {
		this.proxy_expire_time = proxy_expire_time;
	}

	public String getWeb_ver_code() {
		return web_ver_code;
	}

	public void setWeb_ver_code(String web_ver_code) {
		this.web_ver_code = web_ver_code;
	}

	public Integer getWeb_login_status() {
		return web_login_status;
	}

	public void setWeb_login_status(Integer web_login_status) {
		this.web_login_status = web_login_status;
	}

	public String get_web_login_cookie() {
		return _web_login_cookie;
	}

	public void set_web_login_cookie(String _web_login_cookie) {
		this._web_login_cookie = _web_login_cookie;
	}

	public Long getWeb_update_time() {
		return web_update_time;
	}

	public void setWeb_update_time(Long web_update_time) {
		this.web_update_time = web_update_time;
	}

	public Integer getToday_income() {
		return today_income;
	}

	public void setToday_income(Integer today_income) {
		this.today_income = today_income;
	}

	public Integer getAll_income() {
		return all_income;
	}

	public void setAll_income(Integer all_income) {
		this.all_income = all_income;
	}

	public Integer getProxy_fail_times() {
		return proxy_fail_times;
	}

	public void setProxy_fail_times(Integer proxy_fail_times) {
		this.proxy_fail_times = proxy_fail_times;
	}

	public Integer getApp_place_order_flag() {
		return app_place_order_flag;
	}

	public void setApp_place_order_flag(Integer app_place_order_flag) {
		this.app_place_order_flag = app_place_order_flag;
	}

	public Integer getWeb_place_order_flag() {
		return web_place_order_flag;
	}

	public void setWeb_place_order_flag(Integer web_place_order_flag) {
		this.web_place_order_flag = web_place_order_flag;
	}

	public String getEn_channel_str() {
		return en_channel_str;
	}

	public void setEn_channel_str(String en_channel_str) {
		this.en_channel_str = en_channel_str;
	}

	public Long getEn_channel_str_update_time() {
		return en_channel_str_update_time;
	}

	public void setEn_channel_str_update_time(Long en_channel_str_update_time) {
		this.en_channel_str_update_time = en_channel_str_update_time;
	}

	public Long getBuy_card_last_time() {
		return buy_card_last_time;
	}

	public void setBuy_card_last_time(Long buy_card_last_time) {
		this.buy_card_last_time = buy_card_last_time;
	}

	public Integer getBuy_card_count() {
		return buy_card_count;
	}

	public void setBuy_card_count(Integer buy_card_count) {
		this.buy_card_count = buy_card_count;
	}
	
}