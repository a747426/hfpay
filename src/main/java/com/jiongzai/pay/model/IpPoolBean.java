package com.jiongzai.pay.model;

import java.io.Serializable;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jiongzai.pay.util.json.serializer.DateTimeJsonSerializer;

public class IpPoolBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8390294158843856272L;

	private Long id;
	
	private String ip;
	
	private Integer port;
	
    @JsonSerialize(using = DateTimeJsonSerializer.class )
    private Long expire_time;
    
    private String city;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		if(port==null)
			return 0;
		else
			return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Long getExpire_time() {
		return expire_time;
	}

	public void setExpire_time(Long expire_time) {
		this.expire_time = expire_time;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	

}