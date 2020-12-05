package com.jiongzai.pay.task;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jiongzai.pay.service.CardService;

@Component
public class CardSmsLoginTask {
	
	private Log log = LogFactory.getLog(CardSmsLoginTask.class);

	@Autowired
	private CardService cardService;
	
	private static boolean isReturn=true;

	@Scheduled(fixedRate = 3000)
	public void execute() {
		try {
			if(CardSmsLoginTask.isReturn)//上一次没执行完，不再执行
			{
				//log.info("定时短信登录start");
				CardSmsLoginTask.isReturn=false;
				cardService.cardSmsLoginTask();
				CardSmsLoginTask.isReturn=true;
				//log.info("定时短信登录end");
			}
		} catch (Exception e) {
			CardSmsLoginTask.isReturn=true;
			log.error("定时短信登录异常", e);
		}
	}

}
