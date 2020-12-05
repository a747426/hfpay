package com.jiongzai.pay.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiongzai.pay.dao.GroupDao;
import com.jiongzai.pay.model.GroupBean;
import com.jiongzai.pay.service.GroupService;
	
@Service
public class GroupServiceImpl implements GroupService{
	
	@Autowired
	private GroupDao dao;

	@Override
	public int insert(GroupBean t) {
		// TODO Auto-generated method stub
		return dao.insert(t);
	}

	@Override
	public int deleteById(Long id) {
		// TODO Auto-generated method stub
		return dao.deleteById(id);
	}

	@Override
	public List<GroupBean> queryList(GroupBean t) {
		// TODO Auto-generated method stub
		return dao.queryList(t);
	}

	@Override
	public int update(GroupBean t) {
		// TODO Auto-generated method stub
		return dao.update(t);
	}

	@Override
	public GroupBean getById(Long Id) {
		// TODO Auto-generated method stub
		return dao.getById(Id);
	}

	@Override
	public long count(GroupBean t) {
		// TODO Auto-generated method stub
		return dao.count(t);
	}

	@Override
	public PageInfo<GroupBean> pageList(GroupBean t, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<GroupBean> list = dao.queryList(t);
		PageInfo<GroupBean> pageInfo= new PageInfo<GroupBean>(list);
		return pageInfo;
	}

	@Override
	public int removeById(Long id) {
		// TODO Auto-generated method stub
		return dao.removeById(id);
	}

	@Override
	public List<GroupBean> queryAll() {
		// TODO Auto-generated method stub
		return dao.queryAll();
	}
}
