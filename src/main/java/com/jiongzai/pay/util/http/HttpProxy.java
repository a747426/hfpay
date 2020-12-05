package com.jiongzai.pay.util.http;

public class HttpProxy {
	 private String host;
     private int port;
     private Long expire_time;
     private String city;
     
    public HttpProxy(String host,int port)
    {
    	this.host=host;
    	this.port=port;
    }
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
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
