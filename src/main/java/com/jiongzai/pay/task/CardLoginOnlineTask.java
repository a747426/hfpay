package com.jiongzai.pay.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jiongzai.pay.service.CardService;

@Component
public class CardLoginOnlineTask {
	
	private Log log = LogFactory.getLog(CardLoginOnlineTask.class);

	@Autowired
	private CardService cardService;
	
	private static boolean isReturn=true;

	@Scheduled(fixedRate = 40000)
	public void execute() {
		try {
			if(CardLoginOnlineTask.isReturn)//上一次没执行完，不再执行
			{
				//log.info("保持手机号登录在线任务start");
				CardLoginOnlineTask.isReturn=false;
				cardService.cardLoginOnlineTask();
				CardLoginOnlineTask.isReturn=true;
				//log.info("保持手机号登录在线任务end");
			}
		} catch (Exception e) {
			CardLoginOnlineTask.isReturn=true;
			log.error("保持手机号登录在线任务异常", e);
		}
	}

}
