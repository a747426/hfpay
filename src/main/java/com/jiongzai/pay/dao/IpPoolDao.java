package com.jiongzai.pay.dao;

import org.apache.ibatis.annotations.Mapper;

import com.jiongzai.pay.model.IpPoolBean;

@Mapper
public interface IpPoolDao extends BaseDao<IpPoolBean>{
	IpPoolBean getOneCanUseIp();
	
	int removeExpired();
}
