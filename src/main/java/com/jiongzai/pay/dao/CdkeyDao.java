package com.jiongzai.pay.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.jiongzai.pay.model.CdkeyBean;

@Mapper
public interface CdkeyDao extends BaseDao<CdkeyBean>{
	
	List<CdkeyBean> getTodayList();
	
	List<CdkeyBean> getYesTodayList();
	
	List<CdkeyBean> needGetCdkeyList();
	
	List<CdkeyBean> getCdkList(CdkeyBean t);
	
	Integer getAmountSum(CdkeyBean t);
	
	Integer notExportSumToday();
	
	Integer exportSumToday();
	
	Integer notGetSumToday();
}
