package com.jiongzai.pay.service;

import com.jiongzai.pay.model.ConfigBean;

public interface ConfigService extends BaseService<ConfigBean>{
	String getValueByKey(String key);
}
