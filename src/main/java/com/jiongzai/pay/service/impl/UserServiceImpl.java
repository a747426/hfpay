package com.jiongzai.pay.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiongzai.pay.dao.UserDao;
import com.jiongzai.pay.model.UserBean;
import com.jiongzai.pay.service.UserService;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private UserDao dao;

	@Override
	public int insert(UserBean t) {
		// TODO Auto-generated method stub
		return dao.insert(t);
	}

	@Override
	public int deleteById(Long id) {
		// TODO Auto-generated method stub
		return dao.deleteById(id);
	}

	@Override
	public List<UserBean> queryList(UserBean t) {
		// TODO Auto-generated method stub
		return dao.queryList(t);
	}

	@Override
	public int update(UserBean t) {
		// TODO Auto-generated method stub
		return dao.update(t);
	}

	@Override
	public UserBean getById(Long Id) {
		// TODO Auto-generated method stub
		return dao.getById(Id);
	}

	@Override
	public long count(UserBean t) {
		// TODO Auto-generated method stub
		return dao.count(t);
	}

	@Override
	public PageInfo<UserBean> pageList(UserBean t, int pageNum, int pageSize) {
		// TODO Auto-generated method stub
		PageHelper.startPage(pageNum, pageSize);
		List<UserBean> list = dao.queryList(t);
		PageInfo<UserBean> pageInfo= new PageInfo<UserBean>(list);
		return pageInfo;
	}

	@Override
	public int removeById(Long id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<UserBean> queryAll() {
		// TODO Auto-generated method stub
		return null;
	}


}
