package com.jiongzai.pay.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jiongzai.pay.service.CdkeyService;

@Component
public class SendCdkeySmsTask {
	
	private Log log = LogFactory.getLog(SendCdkeySmsTask.class);

	@Autowired
	private CdkeyService cdkeyService;
	
	private static boolean isReturn=true;

	@Scheduled(fixedRate = 3000)
	public void execute() {
		try {
			if(SendCdkeySmsTask.isReturn)//上一次没执行完，不再执行
			{
				//log.info("发送获取卡密短信任务start");
				SendCdkeySmsTask.isReturn=false;
				cdkeyService.SendCdkeySmsTask();
				SendCdkeySmsTask.isReturn=true;
				//log.info("发送获取卡密短信任务end");
			}
		} catch (Exception e) {
			log.error("发送获取卡密短信任务异常", e);
			SendCdkeySmsTask.isReturn=true;
		}
	}

}
