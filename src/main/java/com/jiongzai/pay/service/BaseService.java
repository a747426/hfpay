package com.jiongzai.pay.service;

import java.util.List;

import com.github.pagehelper.PageInfo;

public interface BaseService<T> {
	
	int insert(T t);
	
	int deleteById(Long id);
	
	int removeById(Long id);
	
	int update(T t);
	
	T getById(Long Id);
	
	List<T> queryList(T t);
	
	List<T> queryAll();
	
	long count(T t);
	
	PageInfo<T> pageList(T t,int pageNum,int pageSize);
	
}
