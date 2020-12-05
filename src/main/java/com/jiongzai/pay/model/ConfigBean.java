package com.jiongzai.pay.model;

import java.io.Serializable;

public class ConfigBean implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1297633254409368142L;

	private Long id;
	
	private String name;
	
	private String key;
	
	private String value;
	
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

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
