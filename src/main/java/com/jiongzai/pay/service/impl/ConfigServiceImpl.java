package com.jiongzai.pay.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageInfo;
import com.jiongzai.pay.dao.ConfigDao;
import com.jiongzai.pay.model.ConfigBean;
import com.jiongzai.pay.service.ConfigService;

@Service
public class ConfigServiceImpl implements ConfigService{
	
	@Autowired
	private ConfigDao dao;

	@Override
	public int insert(ConfigBean t) {
		// TODO Auto-generated method stub
		return dao.insert(t);
	}

	@Override
	public int deleteById(Long id) {
		// TODO Auto-generated method stub
		return dao.deleteById(id);
	}

	@Override
	public List<ConfigBean> queryList(ConfigBean t) {
		// TODO Auto-generated method stub
		return dao.queryList(t);
	}

	@Override
	public int update(ConfigBean t) {
		// TODO Auto-generated method stub
		return dao.update(t);
	}

	@Override
	public ConfigBean getById(Long Id) {
		// TODO Auto-generated method stub
		return dao.getById(Id);
	}

	@Override
	public long count(ConfigBean t) {
		// TODO Auto-generated method stub
		return dao.count(t);
	}

	@Override
	public PageInfo<ConfigBean> pageList(ConfigBean t, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int removeById(Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<ConfigBean> queryAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getValueByKey(String key) {
		// TODO Auto-generated method stub
		return dao.getValueByKey(key);
	}


}
