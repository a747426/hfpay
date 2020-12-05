package com.jiongzai.pay.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jiongzai.pay.service.CdkeyService;

@Component
public class GetCdkeyTask {
	
	private Log log = LogFactory.getLog(GetCdkeyTask.class);

	@Autowired
	private CdkeyService cdkeyService;
	
	private static boolean isReturn=true;

	@Scheduled(fixedRate = 3000)
	public void execute() {
		try {
			if(GetCdkeyTask.isReturn)//上一次没执行完，不再执行
			{
				//log.info("获取卡密任务start");
				GetCdkeyTask.isReturn=false;
				cdkeyService.GetCdkeyTask();
				GetCdkeyTask.isReturn=true;
				//log.info("获取卡密任务end");
			}
		} catch (Exception e) {
			log.error("获取卡密任务异常", e);
			GetCdkeyTask.isReturn=true;
		}
	}

}
