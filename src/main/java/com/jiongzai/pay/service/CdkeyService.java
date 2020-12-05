package com.jiongzai.pay.service;

import java.util.List;

import com.jiongzai.pay.model.CdkeyBean;

public interface CdkeyService extends BaseService<CdkeyBean>{
	
	List<CdkeyBean> getCdkList(CdkeyBean t);
	
	List<CdkeyBean> getTodayList();
	
	List<CdkeyBean> getYesTodayList();
	
	void GetCdkeyTask()throws Exception;
	
	void SendCdkeySmsTask() throws Exception;
	
	Integer getAmountSum(CdkeyBean t);
	
	Integer notExportSumToday();
	
	Integer exportSumToday();
	
	Integer notGetSumToday();
	
	boolean getCdkByPayRspObj(Long cardId,Long orderId,String unicomOrderId,String PayRspObj) throws Exception;

}
