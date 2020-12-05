package com.jiongzai.pay.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.jiongzai.pay.dao.MessageDao;
import com.jiongzai.pay.model.MessageBean;
import com.jiongzai.pay.service.MessageService;
	
@Service
public class MessageServiceImpl implements MessageService{
	
	@Autowired
	private MessageDao dao;

	@Override
	public int insert(MessageBean t) {
		// TODO Auto-generated method stub
		return dao.insert(t);
	}

	@Override
	public int deleteById(Long id) {
		// TODO Auto-generated method stub
		return dao.deleteById(id);
	}

	@Override
	public List<MessageBean> queryList(MessageBean t) {
		// TODO Auto-generated method stub
		return dao.queryList(t);
	}

	@Override
	public int update(MessageBean t) {
		// TODO Auto-generated method stub
		return dao.update(t);
	}

	@Override
	public MessageBean getById(Long Id) {
		// TODO Auto-generated method stub
		return dao.getById(Id);
	}

	@Override
	public long count(MessageBean t) {
		// TODO Auto-generated method stub
		return dao.count(t);
	}

	@Override
	public PageInfo<MessageBean> pageList(MessageBean t, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<MessageBean> list = dao.queryList(t);
		PageInfo<MessageBean> pageInfo= new PageInfo<MessageBean>(list);
		return pageInfo;
	}

	@Override
	public int removeById(Long id) {
		// TODO Auto-generated method stub
		return dao.removeById(id);
	}

	@Override
	public List<MessageBean> queryAll() {
		// TODO Auto-generated method stub
		return dao.queryAll();
	}
}
