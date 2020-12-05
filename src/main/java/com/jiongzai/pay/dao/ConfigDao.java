package com.jiongzai.pay.dao;

import org.apache.ibatis.annotations.Mapper;

import com.jiongzai.pay.model.ConfigBean;

@Mapper
public interface ConfigDao extends BaseDao<ConfigBean>{
	String getValueByKey(String key);
}
