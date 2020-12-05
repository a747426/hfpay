package com.jiongzai.pay.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiongzai.pay.dao.MerchantDao;
import com.jiongzai.pay.model.MerchantBean;
import com.jiongzai.pay.service.MerchantService;

@Service
public class MerchantServiceImpl implements MerchantService{
	
	@Autowired
	private MerchantDao dao;

	@Override
	public int insert(MerchantBean t) {
		// TODO Auto-generated method stub
		return dao.insert(t);
	}

	@Override
	public int deleteById(Long id) {
		// TODO Auto-generated method stub
		return dao.deleteById(id);
	}

	@Override
	public List<MerchantBean> queryList(MerchantBean t) {
		// TODO Auto-generated method stub
		return dao.queryList(t);
	}

	@Override
	public int update(MerchantBean t) {
		// TODO Auto-generated method stub
		return dao.update(t);
	}

	@Override
	public MerchantBean getById(Long Id) {
		// TODO Auto-generated method stub
		return dao.getById(Id);
	}

	@Override
	public long count(MerchantBean t) {
		// TODO Auto-generated method stub
		return dao.count(t);
	}

	@Override
	public PageInfo<MerchantBean> pageList(MerchantBean t, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<MerchantBean> list = dao.queryList(t);
		PageInfo<MerchantBean> pageInfo= new PageInfo<MerchantBean>(list);
		return pageInfo;
	}
	
	@Override
	public int removeById(Long id) {
		// TODO Auto-generated method stub
		return dao.removeById(id);
	}

	@Override
	public List<MerchantBean> queryAll() {
		// TODO Auto-generated method stub
		return null;
	}


}
