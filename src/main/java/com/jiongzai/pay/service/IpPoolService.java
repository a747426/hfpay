package com.jiongzai.pay.service;

import java.util.List;

import com.jiongzai.pay.model.IpPoolBean;
import com.jiongzai.pay.util.http.HttpProxy;

public interface IpPoolService extends BaseService<IpPoolBean>{
	
	void UpdateIpPoolTask()throws Exception;
	
	List<HttpProxy> getNewProxy();
	
	IpPoolBean getOneCanUseIp(String type);

}
