package com.jiongzai.pay.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jiongzai.pay.util.json.serializer.DateTimeJsonSerializer;

public class UserBean implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6406013765944608952L;

	private Long id;

    private String account;

    private String nick_name;
    
    private String password;
    
    private String remarks;
    
    @JsonSerialize(using = DateTimeJsonSerializer.class )
    private Long create_time;
    
    @JsonSerialize(using = DateTimeJsonSerializer.class )
    private Long update_time;
    
    private Integer status;
    
    private String bind_ip;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
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

	public String getBind_ip() {
		return bind_ip;
	}

	public void setBind_ip(String bind_ip) {
		this.bind_ip = bind_ip;
	}
    
	
  
}