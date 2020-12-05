package com.jiongzai.pay.dao;

import java.util.List;

public interface BaseDao<T> {
	int insert(T t);
	
	int deleteById(Long id);
	
	int removeById(Long id);
	
	int update(T t);
	
	T getById(Long Id);
	
	List<T> queryList(T t);
	
	long count(T t);
	
	List<T> queryAll();
}
